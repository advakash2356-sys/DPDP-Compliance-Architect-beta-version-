import { ADMIN_EMAILS, HttpError } from "./auth.js";
import { store } from "./store.js";
export const TRIAL_DAYS = 7;
const DAY = 86400000;
export const paymentsEnabled = () => process.env.PAYMENTS_ENABLED === "true" && !!process.env.RAZORPAY_KEY_ID && !!process.env.RAZORPAY_KEY_SECRET && !!process.env.RAZORPAY_PLAN_ID && !!process.env.RAZORPAY_WEBHOOK_SECRET;
// Soft launch: while payments are off, signed-in users have free access (set SOFT_LAUNCH=false to restore the trial gate).
export const softLaunch = () => process.env.SOFT_LAUNCH !== "false" && !paymentsEnabled();
export function decide(user, rec, now) {
  if (ADMIN_EMAILS().includes(user.email)) return { access: true, plan: "admin" };
  if (softLaunch()) return { access: true, plan: "free" };
  if (rec.paidUntil > now && rec.sub_status !== "halted") return { access: true, plan: "paid", paidUntil: rec.paidUntil };
  const ends = rec.trialStart + TRIAL_DAYS * DAY;
  if (now < ends) return { access: true, plan: "trial", trialEnds: ends, daysLeft: Math.ceil((ends - now) / DAY) };
  return { access: false, plan: "expired", trialEnds: ends };
}
export async function entitlement(user, now = Date.now()) {
  const rec = await store().ensureUser(user, now);
  return { ...decide(user, rec, now), paymentsEnabled: paymentsEnabled(), email: user.email, name: user.name };
}
/** Server-side gate for every tool endpoint. */
export async function requireAccess(user) {
  const e = await entitlement(user);
  if (!e.access) throw new HttpError(402, "TRIAL_ENDED", e.paymentsEnabled ? "Your free trial has ended. Subscribe to continue." : "Your free trial has ended. Subscriptions are coming soon.");
  return e;
}
