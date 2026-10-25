// Local dev server ONLY (never deployed): serves web/, routes /api/*, and with DEV_AUTH=1
// mints local test tokens so the sign-in, trial and paywall states can be previewed.
import { createServer } from "node:http";
import { readFile } from "node:fs/promises";
import { extname, join, normalize } from "node:path";
process.env.STORE_DRIVER ||= "memory";
const H = await import("../lib/handlers.js");
const routes = { "GET /api/me": H.me, "GET /api/tools/content": H.content, "POST /api/tools/score": H.scoreTool, "POST /api/tools/notice": H.noticeTool, "POST /api/tools/breach": H.breachTool, "POST /api/billing/subscribe": H.subscribe, "POST /api/billing/webhook": H.webhook };
let mint;
if (process.env.DEV_AUTH === "1") {
  const { generateKeyPair, SignJWT, exportJWK, createLocalJWKSet } = await import("jose");
  const auth = await import("../lib/auth.js"); const { store } = await import("../lib/store.js");
  const k = await generateKeyPair("RS256"); const j = { ...(await exportJWK(k.publicKey)), kid: "dev", alg: "RS256" };
  auth.__setVerifier(createLocalJWKSet({ keys: [j] }), "dev");
  mint = async (email, days) => { const t = await new SignJWT({ email, email_verified: true, name: email.split("@")[0] }).setProtectedHeader({ alg: "RS256", kid: "dev" }).setSubject("dev-" + email).setIssuer("dev").setAudience(auth.clientId()).setExpirationTime("1h").sign(k.privateKey);
    if (days != null) { await store().ensureUser({ sub: "dev-" + email, email }, Date.now()); await store().setSubscription("dev-" + email, { trialStart: Date.now() - days * 86400000 }); } return t; };
}
const types = { ".html": "text/html", ".css": "text/css", ".js": "text/javascript", ".jpg": "image/jpeg", ".png": "image/png", ".svg": "image/svg+xml" };
createServer(async (req, res) => {
  const url = new URL(req.url, "http://localhost"); const key = `${req.method} ${url.pathname}`;
  if (mint && url.pathname === "/dev/token") { res.end(await mint(url.searchParams.get("email"), url.searchParams.get("days") && Number(url.searchParams.get("days")))); return; }
  if (routes[key]) {
    const chunks = []; for await (const c of req) chunks.push(c);
    const r = await routes[key](new Request("http://localhost" + req.url, { method: req.method, headers: req.headers, body: chunks.length ? Buffer.concat(chunks) : undefined }));
    res.writeHead(r.status, Object.fromEntries(r.headers)); res.end(Buffer.from(await r.arrayBuffer())); return;
  }
  let p = normalize(url.pathname).replace(/^(\.\.[/\\])+/, ""); if (p.endsWith("/")) p += "index.html";
  try { const b = await readFile(join("web", p)); res.writeHead(200, { "content-type": types[extname(p)] || "application/octet-stream" }); res.end(b); }
  catch { res.writeHead(404); res.end("not found"); }
}).listen(process.env.PORT || 8787, () => console.log("dev on", process.env.PORT || 8787));
