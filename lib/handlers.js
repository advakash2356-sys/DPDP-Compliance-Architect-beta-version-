import { requireUser, json, fail, checkOrigin, HttpError, ADMIN_EMAILS } from "./auth.js";
import { entitlement, paymentsEnabled } from "./entitlement.js";
import { store } from "./store.js";
import { verifyWebhook, createSubscription } from "./razorpay.js";

// TEST_MODE=true (preview only): visitors may use the tools without signing in. Admin still needs sign-in.
export const testMode = () => process.env.TEST_MODE === "true";
const GUEST = { sub: "guest", email: "guest@test-mode", name: "Test visitor" };
const anon = r => !(r.headers.get("authorization") || "").startsWith("Bearer ");
const body = async r => { try { const t = await r.text(); if (t.length > 20000) throw 0; return t ? JSON.parse(t) : {}; } catch { throw new HttpError(400, "BAD_INPUT", "Invalid request body."); } };
const str = (v, n = 600) => String(v ?? "").slice(0, n);

export async function me(request) {
  try { if (testMode() && anon(request)) return json({ access: true, plan: "test", email: GUEST.email, paymentsEnabled: false }); const u = await requireUser(request); return json(await entitlement(u)); } catch (e) { return fail(e); }
}
// Tools (self-check, notice, breach clock) run only in the browser: there are no tool endpoints.

export async function subscribe(request) {
  try {
    checkOrigin(request); const u = await requireUser(request); const e = await entitlement(u);
    if (!paymentsEnabled()) return json({ error: "Subscriptions are coming soon. No payment is taken yet.", code: "PAYMENTS_OFF" }, 503);
    if (e.plan === "admin" || e.plan === "paid") return json({ error: "You already have access.", code: "ALREADY" }, 409);
    const s = await createSubscription({ keyId: process.env.RAZORPAY_KEY_ID, keySecret: process.env.RAZORPAY_KEY_SECRET, planId: process.env.RAZORPAY_PLAN_ID, sub: u.sub, email: u.email });
    return json({ keyId: process.env.RAZORPAY_KEY_ID, subscriptionId: s.id });
  } catch (e) { return fail(e); }
}
export async function webhook(request) {
  const raw = await request.text();
  if (!verifyWebhook(raw, request.headers.get("x-razorpay-signature"), process.env.RAZORPAY_WEBHOOK_SECRET)) return json({ error: "bad signature" }, 400);
  let ev; try { ev = JSON.parse(raw); } catch { return json({ error: "bad json" }, 400); }
  const s = ev?.payload?.subscription?.entity; const sub = s?.notes?.sub;
  if (!s || !sub) return json({ ok: true, ignored: true });
  try {
    if (["subscription.activated", "subscription.charged", "subscription.resumed"].includes(ev.event)) await store().setSubscription(sub, { sub_status: "active", paidUntil: (s.current_end || 0) * 1000, rzp_sub: s.id });
    else if (["subscription.cancelled", "subscription.completed"].includes(ev.event)) await store().setSubscription(sub, { sub_status: "cancelled", rzp_sub: s.id });
    else if (["subscription.halted", "subscription.paused"].includes(ev.event)) await store().setSubscription(sub, { sub_status: "halted", rzp_sub: s.id });
    return json({ ok: true });
  } catch (e) { return fail(e); }
}

// Anonymous visit counts from the home page only (no personal data, no free text). Tool pages send nothing.
const EVENTS = ["visit"];
export async function track(request) {
  try { checkOrigin(request); const b = await body(request);
    if (!EVENTS.includes(b.ev)) throw new HttpError(400, "BAD_INPUT", "Unknown event.");
    await store().addEvent({ at: Date.now(), vid: str(b.vid, 40), ev: b.ev, src: str(b.src, 60).replace(/[^\w.-]/g, "") || "direct", page: str(b.page, 60) });
    return json({ ok: true });
  } catch (e) { return fail(e); }
}
export async function admin(request) {
  try { const u = await requireUser(request);
    if (!ADMIN_EMAILS().includes(u.email)) throw new HttpError(403, "NOT_ADMIN", "This dashboard is for the site owner only.");
    return json({ ...(await store().listAll()), now: Date.now() });
  } catch (e) { return fail(e); }
}
