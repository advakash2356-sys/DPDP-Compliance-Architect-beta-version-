import { bindTheme } from "/theme.js";
const CLIENT_ID = "81710705394-thc3444tmiu97ms60hcjcjesi56mb6ab.apps.googleusercontent.com";
const $ = id => document.getElementById(id);
const esc = s => String(s).replace(/[&<>"]/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" })[c]);
bindTheme($("theme"));
let token = sessionStorage.getItem("dpdp_token");
const views = ["v-signin", "v-paywall", "v-error", "v-app"];
const view = v => views.forEach(id => $(id).hidden = id !== v);

async function api(path, body) {
  const r = await fetch(path, { method: body ? "POST" : "GET", headers: { ...(token ? { authorization: "Bearer " + token } : {}), ...(body ? { "content-type": "application/json" } : {}) }, body: body ? JSON.stringify(body) : undefined });
  const d = await r.json().catch(() => ({}));
  if (r.status === 401) { signOut(true); throw new Error("auth"); }
  if (r.status === 402) { showPaywall(d); throw new Error("paywall"); }
  if (!r.ok) throw Object.assign(new Error(d.error || "Request failed"), { status: r.status, code: d.code });
  return d;
}
function signOut(expired) { sessionStorage.removeItem("dpdp_token"); token = null; try { google.accounts.id.disableAutoSelect(); } catch {} showSignIn(expired ? "Your session expired. Please sign in again." : ""); }
document.querySelectorAll("[data-signout]").forEach(b => b.onclick = () => signOut(false));

function showSignIn(msg = "") {
  view("v-signin"); $("signin-msg").textContent = msg;
  const init = () => {
    google.accounts.id.initialize({ client_id: CLIENT_ID, callback: r => { token = r.credential; sessionStorage.setItem("dpdp_token", token); boot(); }, auto_select: false, ux_mode: "popup", itp_support: true });
    google.accounts.id.renderButton($("gsi"), { theme: "filled_black", size: "large", shape: "pill", text: "continue_with", width: 300 });
  };
  if (window.google?.accounts?.id) init(); else { let n = 0; const t = setInterval(() => { if (window.google?.accounts?.id) { clearInterval(t); init(); } else if (++n > 50) { clearInterval(t); $("signin-msg").textContent = "Google sign-in could not load. Check your connection or ad blocker and reload."; } }, 200); }
}
function showPaywall(me) {
  view("v-paywall");
  if (me?.email) $("pw-user").textContent = "Signed in as " + me.email;
  if (me?.paymentsEnabled) { const b = $("subscribe"); b.disabled = false; b.removeAttribute("aria-disabled"); b.textContent = "Subscribe"; $("pw-text").textContent = "Subscribe to continue. You will see the full price, including GST, and approve an e-mandate before anything is charged."; b.onclick = subscribe; }
}
async function subscribe() {
  try { const s = await api("/api/billing/subscribe", {}); await loadScript("https://checkout.razorpay.com/v1/checkout.js");
    new Razorpay({ key: s.keyId, subscription_id: s.subscriptionId, name: "DPDP Compliance Architect", description: "Practitioner plan (monthly)", handler: () => { $("pw-text").textContent = "Payment received. Your access will unlock within a minute."; setTimeout(boot, 8000); } }).open();
  } catch (e) { if (e.message !== "paywall") $("pw-text").textContent = e.message; }
}
const loadScript = src => new Promise((ok, no) => { const s = document.createElement("script"); s.src = src; s.onload = ok; s.onerror = no; document.head.appendChild(s); });

async function boot() {
  let me;
  if (!token) { me = await fetch("/api/me").then(r => r.json()).catch(() => ({})); if (me.plan !== "test") return showSignIn(); }
  if (!me) try { me = await api("/api/me"); } catch (e) { if (e.message === "auth") return; view("v-error"); $("err-text").textContent = e.message; return; }
  if (!me.access) return showPaywall(me);
  view("v-app"); $("user").innerHTML = `<span>${esc(me.email)}</span>`;
  const b = $("banner"); b.hidden = false;
  b.innerHTML = me.plan === "trial" ? `<span><strong>${me.daysLeft} day${me.daysLeft === 1 ? "" : "s"} left</strong> in your free trial. It ends on ${new Date(me.trialEnds).toLocaleDateString("en-IN", { dateStyle: "medium" })}, and you won't be charged automatically.</span><a class="btn ghost" href="/pricing.html">See pricing</a>`
    : me.plan === "test" ? `<span><strong>Test mode</strong> · tools open without sign-in for this preview.</span>` : me.plan === "admin" ? `<span><strong>Admin access</strong> · all tools unlocked.</span>` : `<span><strong>Practitioner plan</strong> · active until ${new Date(me.paidUntil).toLocaleDateString("en-IN", { dateStyle: "medium" })}.</span>`;
  await tools();
}

let started = false;
async function tools() {
  if (started) return; started = true;
  const tabs = [...document.querySelectorAll("[role=tab]")];
  const show = p => { if (!$(p) || !$(p).classList.contains("panel")) p = "assess"; tabs.forEach(t => { const on = t.dataset.p === p; t.setAttribute("aria-selected", on); t.tabIndex = on ? 0 : -1; }); document.querySelectorAll(".panel").forEach(s => s.classList.toggle("on", s.id === p)); history.replaceState(null, "", "#" + p); scrollTo(0, 0); };
  tabs.forEach((t, i) => { t.onclick = () => show(t.dataset.p); t.onkeydown = e => { const d = { ArrowDown: 1, ArrowRight: 1, ArrowUp: -1, ArrowLeft: -1 }[e.key]; if (d) { const j = (i + d + tabs.length) % tabs.length; tabs[j].focus(); show(tabs[j].dataset.p); } }; });
  show(location.hash.slice(1));
  const c = await api("/api/tools/content").catch(() => null); if (!c) return;
  // assessment
  let ans = JSON.parse(localStorage.getItem("dpdp_answers") || "{}");
  $("qs").innerHTML = c.checklist.map(q => `<div class="q"><p id="q-${q.id}" style="margin:0"><small>${q.ref}</small>${esc(q.q)}</p><div class="seg" role="radiogroup" aria-labelledby="q-${q.id}">${["yes", "no", "na"].map(v => `<label><input type="radio" name="${q.id}" value="${v}" ${ans[q.id] === v ? "checked" : ""}><span>${v === "na" ? "N/A" : v[0].toUpperCase() + v.slice(1)}</span></label>`).join("")}</div></div>`).join("");
  const render = async () => { const s = await api("/api/tools/score", { answers: ans }).catch(() => null); if (!s) return;
    $("pct").textContent = s.pct + "%"; $("ring").style.setProperty("--p", s.pct);
    $("sumtxt").textContent = `${s.yes} of ${s.total - s.na} applicable checks met${s.na ? ` · ${s.na} not applicable` : ""}.`;
    $("gaps").innerHTML = s.gaps.length ? s.gaps.map(g => `<li><strong>${g.ref}</strong>: ${esc(g.q)}${ans[g.id] ? "" : " <em>(unanswered)</em>"}</li>`).join("") : "<li>No open gaps on your answers. Have counsel confirm.</li>"; };
  $("qs").onchange = e => { ans[e.target.name] = e.target.value; localStorage.setItem("dpdp_answers", JSON.stringify(ans)); render(); };
  $("reset").onclick = () => { if (confirm("Clear all answers saved in this browser?")) { ans = {}; localStorage.removeItem("dpdp_answers"); $("qs").querySelectorAll("input").forEach(i => i.checked = false); render(); } };
  render();
  // notice
  const nf = $("nf"); const saved = JSON.parse(localStorage.getItem("dpdp_notice") || "{}"); for (const [k, v] of Object.entries(saved)) if (nf.elements[k]) nf.elements[k].value = v;
  let nt; const rn = () => { clearTimeout(nt); nt = setTimeout(async () => { const f = Object.fromEntries(new FormData(nf)); localStorage.setItem("dpdp_notice", JSON.stringify(f)); const r = await api("/api/tools/notice", f).catch(() => null); if (r) $("nout").textContent = r.text; }, 250); };
  nf.oninput = rn; rn();
  $("copy").onclick = async () => { try { await navigator.clipboard.writeText($("nout").textContent); $("copied").textContent = "Copied."; } catch { $("copied").textContent = "Select the text and copy it manually."; } };
  // breach
  let deadline = null;
  const tick = () => { if (!deadline) { $("bout").textContent = ""; $("bleft").textContent = "Enter a date and time to calculate the deadline."; return; }
    $("bout").textContent = "Report due by " + deadline.toLocaleString("en-IN", { dateStyle: "medium", timeStyle: "short" });
    const h = (deadline - Date.now()) / 36e5; $("bleft").textContent = h > 0 ? `${Math.floor(h)} h ${Math.floor((h % 1) * 60)} min left (your device's time zone).` : "This deadline has passed. Ask counsel about seeking an extension from the Board."; };
  $("aware").oninput = async () => { const v = $("aware").value; if (!v) { deadline = null; return tick(); } const r = await api("/api/tools/breach", { aware: new Date(v).toISOString() }).catch(() => null); deadline = r ? new Date(r.deadline) : null; tick(); };
  tick(); setInterval(tick, 60000);
  $("glist").innerHTML = c.sections.map(([r, t, d]) => `<div class="card"><p class="label">${r}</p><h3 style="font-size:1.35rem;margin:.4rem 0">${esc(t)}</h3><p style="margin:0;color:var(--muted)">${esc(d)}</p></div>`).join("");
  $("ptable").innerHTML = c.penalties.map(p => `<tr><td>${esc(p.breach)}</td><td>${p.ref}</td><td class="amt">${esc(p.max)}</td></tr>`).join("");
}
$("retry").onclick = () => location.reload(); $("printbtn").onclick = () => print();
boot();
