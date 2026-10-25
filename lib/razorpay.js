import { createHmac, timingSafeEqual } from "node:crypto";
/** Razorpay webhook check: HMAC-SHA256 of the raw body with the webhook secret, hex, constant-time compare. */
export function verifyWebhook(rawBody, signature, secret) {
  if (!signature || !secret) return false;
  const exp = Buffer.from(createHmac("sha256", secret).update(rawBody).digest("hex"));
  const got = Buffer.from(String(signature));
  return exp.length === got.length && timingSafeEqual(exp, got);
}
/** Checkout handler signature: HMAC(payment_id + "|" + subscription_id, key_secret). */
export function verifyCheckout(paymentId, subscriptionId, signature, keySecret) {
  return verifyWebhook(`${paymentId}|${subscriptionId}`, signature, keySecret);
}
export async function createSubscription({ keyId, keySecret, planId, sub, email }) {
  const r = await fetch("https://api.razorpay.com/v1/subscriptions", {
    method: "POST", signal: AbortSignal.timeout(10000),
    headers: { "content-type": "application/json", authorization: "Basic " + Buffer.from(`${keyId}:${keySecret}`).toString("base64") },
    body: JSON.stringify({ plan_id: planId, total_count: 120, customer_notify: 1, notes: { sub, email } }),
  });
  if (!r.ok) throw new Error("razorpay " + r.status);
  return r.json();
}
