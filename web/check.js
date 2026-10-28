import { CHECKLIST, SOURCES_PUBLIC as S } from "/public-data.js";
import { bindTheme } from "/theme.js"; import { track } from "/track.js";
bindTheme(document.getElementById("theme")); track("visit");
const $ = id => document.getElementById(id);
const esc = s => String(s).replace(/[&<>"]/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" })[c]);
const KEY = "dpdp_check"; const ans = JSON.parse(localStorage.getItem(KEY) || "{}");
$("qf").innerHTML = CHECKLIST.map((c, i) => `<fieldset class="q"><legend><span class="ref">${i + 1}. ${c.ref}</span></legend><p>${esc(c.q)}</p><div class="opts">${[["yes", "Yes"], ["no", "No"], ["na", "Not applicable"]].map(([v, l]) => `<label><input type="radio" name="${c.id}" value="${v}"${ans[c.id] === v ? " checked" : ""}><span>${l}</span></label>`).join("")}</div></fieldset>`).join("");
let started = false;
$("qf").onchange = e => { ans[e.target.name] = e.target.value; localStorage.setItem(KEY, JSON.stringify(ans)); if (!started) { started = true; track("check_start"); } };
let last = { pct: "", gaps: "" };
$("go").onclick = () => {
  const yes = CHECKLIST.filter(c => ans[c.id] === "yes").length, na = CHECKLIST.filter(c => ans[c.id] === "na").length, d = CHECKLIST.length - na;
  const pct = d ? Math.round(yes / d * 100) : 0; const gaps = CHECKLIST.filter(c => ans[c.id] !== "yes" && ans[c.id] !== "na");
  last = { pct: String(pct), gaps: gaps.map(g => g.ref).join("; ") };
  const date = new Date().toLocaleDateString("en-IN", { dateStyle: "long" });
  $("report").innerHTML = `<p class="label">DPDP gap report · ${date}</p><h2>Readiness: ${pct}%</h2>
  <p>${yes} of ${d} applicable checks answered "Yes". ${gaps.length} open gap${gaps.length === 1 ? "" : "s"}.</p>
  ${gaps.length ? `<table><thead><tr><th>#</th><th>Provision</th><th>Requirement</th><th>Status</th></tr></thead><tbody>${gaps.map((g, i) => `<tr><td>${i + 1}</td><td><strong>${g.ref}</strong></td><td>${esc(g.q)}</td><td class="sev">${ans[g.id] ? "Gap" : "Unanswered"}</td></tr>`).join("")}</tbody></table>` : "<p>No open gaps on your answers. Have counsel confirm.</p>"}
  <p class="fine" style="margin-top:1.5rem"><strong>When this applies.</strong> Under Rule 1 of the DPDP Rules, 2025, most obligations above (Rules 3, 5–16, 22, 23) come into force on 13 May 2027; Consent Manager rules (Rule 4) on 13 November 2026. Sources: <a href="${S.act}">DPDP Act, 2023 (MeitY)</a> · <a href="${S.rules}">DPDP Rules, 2025 (MeitY)</a>.</p>
  <p class="fine"><strong>Educational, not legal advice.</strong> This report reflects your own answers. It is not a legal opinion, audit or certification and does not create a lawyer–client relationship. · DPDP Compliance Architect, by Adv. Akash Sharma · Built with Akash Sync Agent</p>`;
  $("out").hidden = false; $("out").scrollIntoView({ behavior: "smooth" }); track("check_done");
};
$("pdf").onclick = () => { track("report_download"); print(); };
let opened = false; $("review").addEventListener("focusin", () => { if (!opened) { opened = true; track("review_open"); } });
$("rf").onsubmit = async e => { e.preventDefault(); const f = new FormData(e.target); const b = Object.fromEntries(f); b.consent = $("rc").checked; Object.assign(b, last);
  $("rmsg").textContent = "Sending…";
  const r = await fetch("/api/review", { method: "POST", headers: { "content-type": "application/json" }, body: JSON.stringify(b) }).catch(() => null); const d = await r?.json().catch(() => ({})) || {};
  $("rmsg").innerHTML = r?.ok ? `Received. Your reference is <strong>${esc(d.id)}</strong>. Expect a reply by email within 2 working days.` : `${esc(d.error || "Could not send.")} You can also email <a href="mailto:adv.akash2356@gmail.com?subject=DPDP%20lawyer%20review">adv.akash2356@gmail.com</a>.`; };
$("ff").onsubmit = e => { e.preventDefault(); const t = $("ft").value.trim(); if (t) { track("feedback", t); $("fmsg").textContent = "Thank you."; $("ft").value = ""; } };
