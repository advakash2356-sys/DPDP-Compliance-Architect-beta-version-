import { requireUser, json, fail, checkOrigin, HttpError, ADMIN_EMAILS } from "./auth.js";
import { entitlement, requireAccess, paymentsEnabled } from "./entitlement.js";
import { CHECKLIST, SECTIONS, PENALTIES, score, noticeText, breachDeadline } from "./content.js";
import { store } from "./store.js";
import { verifyWebhook, createSubscription } from "./razorpay.js";

// TEST_MODE=true (preview only): visitors may use the tools without signing in. Admin still needs sign-in.
export const testMode = () => process.env.TEST_MODE === "true";
const GUEST = { sub: "guest", email: "guest@test-mode", name: "Test visitor" };
const anon = r => !(r.headers.get("authorization") || "").startsWith("Bearer ");
const gated = fn => async request => {
  try { checkOrigin(request);
    if (testMode() && anon(request)) return await fn(request, GUEST, { access: true, plan: "test" });
    const u = await requireUser(request); const e = await requireAccess(u); return await fn(request, u, e); }
  catch (e) { return fail(e); }
};
const body = async r => { try { const t = await r.text(); if (t.length > 20000) throw 0; return t ? JSON.parse(t) : {}; } catch { throw new HttpError(400, "BAD_INPUT", "Invalid request body."); } };
const str = (v, n = 600) => String(v ?? "").slice(0, n);

export async function me(request) {
  try { if (testMode() && anon(request)) return json({ access: true, plan: "test", email: GUEST.email, paymentsEnabled: false }); const u = await requireUser(request); return json(await entitlement(u)); } catch (e) { return fail(e); }
}
export const content = gated(async () => json({ checklist: CHECKLIST, sections: SECTIONS, penalties: PENALTIES }));
export const scoreTool = gated(async r => { const b = await body(r); const a = {}; for (const c of CHECKLIST) if (["yes", "no", "na"].includes(b.answers?.[c.id])) a[c.id] = b.answers[c.id]; return json(score(a)); });
export const noticeTool = gated(async r => {
  const b = await body(r); const f = {};
  for (const k of ["business", "data", "purpose", "withdraw", "contact"]) f[k] = str(b[k]);
  const d = parseInt(b.days, 10); f.days = d >= 1 && d <= 90 ? String(d) : "";
  return json({ text: noticeText(f) });
});
export const breachTool = gated(async r => { const b = await body(r); const d = breachDeadline(str(b.aware, 40)); if (!d) throw new HttpError(400, "BAD_INPUT", "Enter a valid date and time."); return json({ deadline: d.toISOString() }); });

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

// Lawyer review request: no payment, no account needed. Stored for Adv. Akash Sharma to follow up.
export async function reviewRequest(request) {
  try {
    checkOrigin(request); const b = await body(request);
    if (b.website) return json({ ok: true }); // honeypot
    const r = { name: str(b.name, 120).trim(), email: str(b.email, 200).trim(), phone: str(b.phone, 30).trim(), org: str(b.org, 200).trim(), role: str(b.role, 40), pct: str(b.pct, 5), gaps: str(b.gaps, 400), note: str(b.note, 1000).trim() };
    if (!r.name || !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(r.email)) throw new HttpError(400, "BAD_INPUT", "Enter your name and a valid email.");
    if (b.consent !== true) throw new HttpError(400, "NO_CONSENT", "Please agree to be contacted about your request.");
    r.id = "LR-" + Date.now().toString(36).toUpperCase();
    await store().addReview(r);
    return json({ ok: true, id: r.id });
  } catch (e) { return fail(e); }
}

// Anonymous funnel/channel events (no personal data besides optional feedback text).
const EVENTS = ["visit", "check_start", "check_done", "report_download", "review_open", "feedback"];
export async function track(request) {
  try { checkOrigin(request); const b = await body(request);
    if (!EVENTS.includes(b.ev)) throw new HttpError(400, "BAD_INPUT", "Unknown event.");
    await store().addEvent({ at: Date.now(), vid: str(b.vid, 40), ev: b.ev, src: str(b.src, 60).replace(/[^\w.-]/g, "") || "direct", page: str(b.page, 60), text: b.ev === "feedback" ? str(b.text, 1000) : "" });
    return json({ ok: true });
  } catch (e) { return fail(e); }
}
export async function admin(request) {
  try { const u = await requireUser(request);
    if (!ADMIN_EMAILS().includes(u.email)) throw new HttpError(403, "NOT_ADMIN", "This dashboard is for the site owner only.");
    return json({ ...(await store().listAll()), now: Date.now() });
  } catch (e) { return fail(e); }
}
