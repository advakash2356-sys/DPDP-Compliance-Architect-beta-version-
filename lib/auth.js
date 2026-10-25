import { createRemoteJWKSet, jwtVerify } from "jose";
// The OAuth client ID is public (it ships in the browser); env can override it.
export const DEFAULT_CLIENT_ID = "81710705394-thc3444tmiu97ms60hcjcjesi56mb6ab.apps.googleusercontent.com";
export const clientId = () => process.env.GOOGLE_CLIENT_ID || DEFAULT_CLIENT_ID;
export const ADMIN_EMAILS = () => (process.env.ADMIN_EMAILS || "adv.akash2356@gmail.com").toLowerCase().split(",").map(s => s.trim());
let jwks = createRemoteJWKSet(new URL("https://www.googleapis.com/oauth2/v3/certs"));
let issuers = ["https://accounts.google.com", "accounts.google.com"];
/** Test/dev only: swap Google's key set for a local one. Not reachable over HTTP. */
export function __setVerifier(keyset, iss) { jwks = keyset; issuers = [iss]; }
export class HttpError extends Error { constructor(status, code, message) { super(message); this.status = status; this.code = code; } }

/** Verifies the Google ID token in "Authorization: Bearer <token>" (signature, expiry, issuer, audience, verified email). */
export async function requireUser(request) {
  const h = request.headers.get("authorization") || "";
  const token = h.startsWith("Bearer ") ? h.slice(7).trim() : "";
  if (!token || token.length > 4096) throw new HttpError(401, "NO_TOKEN", "Please sign in.");
  let p;
  try { ({ payload: p } = await jwtVerify(token, jwks, { audience: clientId(), issuer: issuers, clockTolerance: 30 })); }
  catch { throw new HttpError(401, "BAD_TOKEN", "Your sign-in has expired. Please sign in again."); }
  if (!p.sub || !p.email || p.email_verified !== true) throw new HttpError(401, "EMAIL_UNVERIFIED", "Your Google email is not verified.");
  return { sub: String(p.sub), email: String(p.email).toLowerCase(), name: String(p.name || ""), picture: String(p.picture || "") };
}
export const json = (body, status = 200, extra = {}) => new Response(JSON.stringify(body), { status, headers: { "content-type": "application/json", "cache-control": "no-store", ...extra } });
export function fail(e) {
  if (e instanceof HttpError) return json({ error: e.message, code: e.code }, e.status);
  console.error("internal error", e?.message);
  return json({ error: "Something went wrong.", code: "INTERNAL" }, 500);
}
export function checkOrigin(request) {
  const o = request.headers.get("origin"); if (!o) return;
  const host = request.headers.get("x-forwarded-host") || request.headers.get("host");
  try { if (new URL(o).host !== host) throw 0; } catch { throw new HttpError(403, "BAD_ORIGIN", "Cross-origin request refused."); }
}
