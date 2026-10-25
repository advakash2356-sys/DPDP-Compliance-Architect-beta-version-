# Subscription setup (Akash)

The landing page and legal pages are public. `/app.html` and every `/api/tools/*` endpoint require a Google ID token, which is verified server-side (signature, audience, issuer, expiry and verified email), plus an active trial, a paid plan or admin access. Tests: `npm test`.

## 1. Google sign-in origin
In Google Cloud Console › Credentials › OAuth client `81710705394-thc3444tmiu97ms60hcjcjesi56mb6ab...`, add these **Authorized JavaScript origins**:
- `https://dpdp-compliance-architect-beta-vers.vercel.app`
- (optional, for previews) each preview URL you want to test

## 2. Trial store (free: Google Sheet + Apps Script)
1. Create a Google Sheet, open Extensions › Apps Script, and paste `apps-script/Code.gs`.
2. In Project Settings › Script properties, add `SECRET` with a long random value.
3. Deploy › New deployment › Web app (Execute as: Me; Who has access: Anyone) and copy the `/exec` URL.
4. In Vercel › Project › Settings › Environment Variables (Production), add `TRIAL_STORE_URL` = the URL and `TRIAL_STORE_SECRET` = the same SECRET. Redeploy.

Until these are set, the workspace shows "Accounts are being set up", and the tools refuse every request. It fails closed.

## 3. Payments (stay OFF until you decide)
Razorpay has no setup fee and no annual or monthly fee. Subscriptions on cards cost 0.9% plus the standard 2% platform fee per transaction, plus 18% GST on the fee. UPI Autopay and e-mandate pricing is "on request". (Source: razorpay.com/pricing, checked 26 Oct 2026.)
1. Complete Razorpay KYC and enable Subscriptions.
2. Create a Plan (monthly, price incl. GST) and copy its `plan_id`.
3. Create a webhook to `https://<domain>/api/billing/webhook` with these events: subscription.activated, charged, cancelled, completed, halted, paused, resumed. Set a webhook secret.
4. Add these env vars: `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET`, `RAZORPAY_PLAN_ID`, `RAZORPAY_WEBHOOK_SECRET`, `PAYMENTS_ENABLED=true`.
5. Update the price on `pricing.html` and the landing pricing card, and add your address (and GSTIN if applicable) to the seller details. Redeploy.

Admin bypass: `adv.akash2356@gmail.com`. Override it with `ADMIN_EMAILS` (comma-separated) if needed.
