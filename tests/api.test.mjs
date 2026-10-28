import { test, before } from "node:test";
import assert from "node:assert/strict";
import { generateKeyPair, SignJWT, createLocalJWKSet, exportJWK } from "jose";
import { createHmac } from "node:crypto";
process.env.STORE_DRIVER = "memory"; delete process.env.VERCEL; delete process.env.TRIAL_STORE_URL; delete process.env.TRIAL_STORE_SECRET; // never touch the real store from tests
const auth = await import("../lib/auth.js");
const { __resetStore, store } = await import("../lib/store.js");
const H = await import("../lib/handlers.js");
const { decide } = await import("../lib/entitlement.js");
let priv; const ISS = "https://test.local";
before(async () => { const k = await generateKeyPair("RS256"); priv = k.privateKey; const j = await exportJWK(k.publicKey); j.kid = "t"; j.alg = "RS256"; auth.__setVerifier(createLocalJWKSet({ keys: [j] }), ISS); });
const tok = (email, extra = {}) => new SignJWT({ email, email_verified: true, name: "T", ...extra }).setProtectedHeader({ alg: "RS256", kid: "t" }).setSubject("sub-" + email).setIssuer(ISS).setAudience(auth.clientId()).setIssuedAt().setExpirationTime("1h").sign(priv);
const req = async (t, body, method = body ? "POST" : "GET") => new Request("https://x.test/api", { method, headers: t ? { authorization: "Bearer " + t, "content-type": "application/json" } : {}, body: body ? JSON.stringify(body) : undefined });
const st = async r => (await r).status;
const DAY = 86400000;

test("tools reject anonymous and forged tokens", async () => {
  assert.equal(await st(H.content(await req())), 401);
  assert.equal(await st(H.noticeTool(await req("a.b.c", {}))), 401);
  const other = await new SignJWT({ email: "x@y.z", email_verified: true }).setProtectedHeader({ alg: "HS256" }).setAudience(auth.clientId()).setIssuer(ISS).setExpirationTime("1h").sign(new Uint8Array(32));
  assert.equal(await st(H.content(await req(other))), 401);
});
test("wrong audience and unverified email are rejected", async () => {
  const bad = await new SignJWT({ email: "a@b.c", email_verified: true }).setProtectedHeader({ alg: "RS256", kid: "t" }).setSubject("s").setIssuer(ISS).setAudience("someone-else").setExpirationTime("1h").sign(priv);
  assert.equal(await st(H.me(await req(bad))), 401);
  assert.equal(await st(H.me(await req(await tok("u@b.c", { email_verified: false })))), 401);
});
test("new user gets a 7-day trial and tools work", async () => {
  __resetStore(); (await import("../lib/store.js")).store().__reset({});
  const t = await tok("new@user.in");
  const m = await (await H.me(await req(t))).json();
  assert.equal(m.plan, "trial"); assert.equal(m.daysLeft, 7);
  assert.equal(await st(H.content(await req(t))), 200);
  const n = await (await H.noticeTool(await req(t, { business: "Acme", days: 30 }))).json();
  assert.match(n.text, /Acme/);
  const b = await (await H.breachTool(await req(t, { aware: "2026-01-01T00:00:00Z" }))).json();
  assert.equal(b.deadline, "2026-01-04T00:00:00.000Z");
});
test("trial start is server-side and not reset by signing in again", async () => {
  const t = await tok("old@user.in");
  await H.me(await req(t));
  await store().setSubscription("sub-old@user.in", { trialStart: Date.now() - 8 * DAY });
  const m = await (await H.me(await req(t))).json();
  assert.equal(m.plan, "expired");
  for (const f of [H.content, H.scoreTool, H.noticeTool, H.breachTool]) assert.equal(await st(f(await req(t, f === H.content ? undefined : {}))), 402);
});
test("admin bypass", async () => {
  const t = await tok("adv.akash2356@gmail.com");
  await H.me(await req(t)); await store().setSubscription("sub-adv.akash2356@gmail.com", { trialStart: 0 });
  assert.equal((await (await H.me(await req(t))).json()).plan, "admin");
  assert.equal(await st(H.content(await req(t))), 200);
});
test("payments are off without keys: subscribe returns coming soon", async () => {
  const r = await H.subscribe(await req(await tok("old@user.in"), {}));
  assert.equal(r.status, 503); assert.equal((await r.json()).code, "PAYMENTS_OFF");
});
test("webhook needs a valid signature, then unlocks access", async () => {
  process.env.RAZORPAY_WEBHOOK_SECRET = "whsec_test";
  const ev = JSON.stringify({ event: "subscription.charged", payload: { subscription: { entity: { id: "sub_1", current_end: Math.floor(Date.now() / 1000) + 30 * 86400, notes: { sub: "sub-old@user.in" } } } } });
  const mk = sig => new Request("https://x.test/api", { method: "POST", headers: { "x-razorpay-signature": sig }, body: ev });
  assert.equal((await H.webhook(mk("deadbeef"))).status, 400);
  assert.equal((await H.webhook(mk(createHmac("sha256", "wrong").update(ev).digest("hex")))).status, 400);
  assert.equal((await H.webhook(mk(createHmac("sha256", "whsec_test").update(ev).digest("hex")))).status, 200);
  assert.equal((await (await H.me(await req(await tok("old@user.in")))).json()).plan, "paid");
});
test("decide(): halted subscription loses access", () => {
  assert.equal(decide({ email: "a@b" }, { trialStart: 0, paidUntil: Date.now() + DAY, sub_status: "halted" }, Date.now()).access, false);
});
test("store not configured on Vercel fails closed", async () => {
  __resetStore(); process.env.VERCEL = "1";
  assert.equal(await st(H.me(await req(await tok("z@z.in")))), 503);
  delete process.env.VERCEL; __resetStore();
});
test("cross-origin requests are refused", async () => {
  const r = new Request("https://x.test/api", { headers: { origin: "https://evil.test", host: "x.test", authorization: "Bearer " + await tok("a@a.in") } });
  assert.equal((await H.content(r)).status, 403);
});
