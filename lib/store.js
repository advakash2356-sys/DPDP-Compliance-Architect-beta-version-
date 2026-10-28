// Durable user store. Production: a Google Apps Script web app bound to a Sheet (free),
// called server-to-server with a shared secret (TRIAL_STORE_URL, TRIAL_STORE_SECRET).
// Local dev/tests only: a JSON file (never used on Vercel).
import { readFileSync, writeFileSync } from "node:fs";
import { HttpError } from "./auth.js";
const FILE = ".dev-store.json";
let mem = null;
function fileDriver() {
  const load = () => { try { return JSON.parse(readFileSync(FILE, "utf8")); } catch { return {}; } };
  const db = () => (mem ??= load());
  const save = () => { if (process.env.STORE_DRIVER === "file") writeFileSync(FILE, JSON.stringify(mem, null, 1)); };
  return {
    async ensureUser(u, now) { const d = db(); d[u.sub] ??= { sub: u.sub, email: u.email, trialStart: now, sub_status: "none", paidUntil: 0 }; save(); return d[u.sub]; },
    async getUser(sub) { return db()[sub] || null; },
    async setSubscription(sub, f) { const d = db(); if (!d[sub]) return false; Object.assign(d[sub], f); save(); return true; },
    async addEvent(e) { const d = db(); (d.__events ??= []).push(e); save(); return true; },
    async listAll() { const d = db(); return { users: Object.entries(d).filter(([k]) => !k.startsWith("__")).map(([, v]) => v), events: d.__events || [] }; },
    __reset(v = {}) { mem = v; },
  };
}
function scriptDriver(url, secret) {
  const call = async (action, data) => {
    let r;
    try { r = await fetch(url, { method: "POST", headers: { "content-type": "application/json" }, body: JSON.stringify({ action, secret, ...data }), redirect: "follow", signal: AbortSignal.timeout(10000) }); }
    catch { throw new HttpError(503, "STORE_DOWN", "The account service is unreachable. Try again shortly."); }
    const d = await r.json().catch(() => null);
    if (!d || !d.ok) throw new HttpError(503, "STORE_ERROR", "The account service returned an error.");
    return d;
  };
  return {
    async ensureUser(u, now) { return (await call("ensureUser", { sub: u.sub, email: u.email, now })).user; },
    async getUser(sub) { return (await call("getUser", { sub })).user || null; },
    async addEvent(e) { return (await call("addEvent", e)).ok; },
    async listAll() { const d = await call("listAll", {}); return { users: d.users || [], events: d.events || [] }; },
    async setSubscription(sub, f) { return (await call("setSubscription", { sub, ...f })).ok; },
  };
}
let cached;
export function store() {
  if (cached) return cached;
  const { TRIAL_STORE_URL: url, TRIAL_STORE_SECRET: secret, STORE_DRIVER, VERCEL } = process.env;
  if (url && secret) return (cached = scriptDriver(url, secret));
  if ((STORE_DRIVER === "file" || STORE_DRIVER === "memory") && !VERCEL) return (cached = fileDriver());
  throw new HttpError(503, "STORE_NOT_CONFIGURED", "Accounts are being set up. Sign-in will open shortly.");
}
export function __resetStore() { cached = undefined; }
