import { test, before } from "node:test";
import assert from "node:assert/strict";
import { generateKeyPair, SignJWT, createLocalJWKSet, exportJWK } from "jose";
import { createHmac } from "node:crypto";
process.env.STORE_DRIVER = "memory"; delete process.env.VERCEL; delete process.env.TRIAL_STORE_URL; delete process.env.TRIAL_STORE_SECRET; delete process.env.TEST_MODE; process.env.SOFT_LAUNCH = "false"; // never touch the real store from tests
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

test("sign-in rejects anonymous and forged tokens", async () => {
  assert.equal(await st(H.me(await req())), 401);
  assert.equal(await st(H.me(await req("a.b.c"))), 401);
  const other = await new SignJWT({ email: "x@y.z", email_verified: true }).setProtectedHeader({ alg: "HS256" }).setAudience(auth.clientId()).setIssuer(ISS).setExpirationTime("1h").sign(new Uint8Array(32));
  assert.equal(await st(H.me(await req(other))), 401);
});
test("there are no server endpoints that receive tool inputs", () => {
  for (const k of ["content", "scoreTool", "noticeTool", "breachTool", "reviewRequest"]) assert.equal(H[k], undefined, k);
});
test("wrong audience and unverified email are rejected", async () => {
  const bad = await new SignJWT({ email: "a@b.c", email_verified: true }).setProtectedHeader({ alg: "RS256", kid: "t" }).setSubject("s").setIssuer(ISS).setAudience("someone-else").setExpirationTime("1h").sign(priv);
  assert.equal(await st(H.me(await req(bad))), 401);
  assert.equal(await st(H.me(await req(await tok("u@b.c", { email_verified: false })))), 401);
});
test("new user gets a 7-day trial", async () => {
  __resetStore(); (await import("../lib/store.js")).store().__reset({});
  const m = await (await H.me(await req(await tok("new@user.in")))).json();
  assert.equal(m.plan, "trial"); assert.equal(m.daysLeft, 7);
});
test("soft launch: payments off means free access, even after the trial", async () => {
  process.env.SOFT_LAUNCH = "true";
  try { const t = await tok("old2@user.in"); await H.me(await req(t)); await store().setSubscription("sub-old2@user.in", { trialStart: 0 });
    const m = await (await H.me(await req(t))).json(); assert.equal(m.plan, "free"); assert.equal(m.access, true); }
  finally { process.env.SOFT_LAUNCH = "false"; }
});
test("trial start is server-side and not reset by signing in again", async () => {
  const t = await tok("old@user.in");
  await H.me(await req(t));
  await store().setSubscription("sub-old@user.in", { trialStart: Date.now() - 8 * DAY });
  const m = await (await H.me(await req(t))).json();
  assert.equal(m.plan, "expired");
  assert.equal(m.access, false);
});
test("admin bypass", async () => {
  const t = await tok("adv.akash2356@gmail.com");
  await H.me(await req(t)); await store().setSubscription("sub-adv.akash2356@gmail.com", { trialStart: 0 });
  assert.equal((await (await H.me(await req(t))).json()).plan, "admin");
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
  assert.equal((await H.track(new Request("https://x.test/api/track", { method: "POST", headers: { origin: "https://evil.test", host: "x.test" }, body: "{}" }))).status, 403);
});

test("public checklist mirrors the server checklist", async () => {
  const pub = await import("../web/public-data.js"); const { CHECKLIST } = await import("../lib/content.js");
  assert.deepEqual(pub.CHECKLIST, CHECKLIST);
});
test("browser tool code is identical to the server copy and makes no network calls", async () => {
  const { readFileSync } = await import("node:fs");
  assert.equal(readFileSync("web/content.js", "utf8"), readFileSync("lib/content.js", "utf8"));
  for (const f of ["web/check.js", "web/content.js"]) assert.doesNotMatch(readFileSync(f, "utf8"), /fetch\(|track\(|XMLHttpRequest|sendBeacon/, f);
  assert.doesNotMatch(readFileSync("web/app.js", "utf8"), /\/api\/tools|\/api\/review/);
  assert.doesNotMatch(readFileSync("web/check.html", "utf8"), /track\.js|\/api\//);
});
test("site copy: no advocate name, no paid review, no compliance promises", async () => {
  const { readFileSync, readdirSync } = await import("node:fs");
  for (const f of readdirSync("web").filter(f => /\.(html|js)$/.test(f))) {
    const t = readFileSync("web/" + f, "utf8");
    assert.doesNotMatch(t, /Akash Sharma|Adv\.|adv\.akash|lawyer review|guarantee|100%|audit-ready|9,999|₹999/i, f);
  }
});

test("admin dashboard is owner-only; TEST_MODE opens tools but not admin", async () => {
  __resetStore(); store().__reset({});
  assert.equal(await st(H.admin(await req())), 401);
  assert.equal(await st(H.admin(await req(await tok("someone@x.in")))), 403);
  const ok = await H.admin(await req(await tok("adv.akash2356@gmail.com"))); assert.equal(ok.status, 200); assert.ok(Array.isArray((await ok.json()).users));
  process.env.TEST_MODE = "true";
  try { assert.equal((await (await H.me(await req())).json()).plan, "test"); assert.equal(await st(H.admin(await req())), 401); }
  finally { delete process.env.TEST_MODE; }
});
test("track accepts known events only", async () => {
  __resetStore(); store().__reset({});
  const r = b => H.track(new Request("https://x.test/api/track", { method: "POST", body: JSON.stringify(b) }));
  assert.equal((await r({ ev: "hack" })).status, 400);
  assert.equal((await r({ ev: "visit", src: "reddit-1<x>" })).status, 200);
  assert.equal((await store().listAll()).events[0].src, "reddit-1x");
});
test("site stays private: noindex on every page, robots.txt disallows all", async () => {
  const { readFileSync, readdirSync } = await import("node:fs");
  for (const f of readdirSync("web").filter(f => f.endsWith(".html"))) assert.match(readFileSync("web/" + f, "utf8"), /<meta name="robots" content="noindex/, f);
  assert.match(readFileSync("web/robots.txt", "utf8"), /Disallow: \/\n/);
  assert.match(readFileSync("vercel.json", "utf8"), /X-Robots-Tag/);
});
test("sample demo data scores as shown on the home page", async () => {
  const { SAMPLE, ACTIONS, CHECKLIST, score } = await import("../lib/content.js");
  const s = score(SAMPLE.answers); assert.equal(s.pct, 27); assert.equal(s.gaps.length, 8);
  for (const c of CHECKLIST) assert.ok(ACTIONS[c.id], c.id);
  assert.match(SAMPLE.name, /sample/i);
});
