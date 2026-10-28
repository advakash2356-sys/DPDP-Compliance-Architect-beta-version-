import { CHECKLIST, SOURCES_PUBLIC as S } from "/public-data.js";
import { bindTheme } from "/theme.js";
// This page makes no network requests (CSP connect-src 'none'). Answers stay in this browser.
bindTheme(document.getElementById("theme"));
const $ = id => document.getElementById(id);
const esc = s => String(s).replace(/[&<>"]/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" })[c]);
const KEY = "dpdp_check"; const ans = JSON.parse(localStorage.getItem(KEY) || "{}");
$("qf").innerHTML = CHECKLIST.map((c, i) => `<fieldset class="q"><legend><span class="ref">${i + 1}. ${c.ref}</span></legend><p>${esc(c.q)}</p><div class="opts">${[["yes", "Yes"], ["no", "No"], ["na", "Not applicable"]].map(([v, l]) => `<label><input type="radio" name="${c.id}" value="${v}"${ans[c.id] === v ? " checked" : ""}><span>${l}</span></label>`).join("")}</div></fieldset>`).join("");
$("qf").onchange = e => { ans[e.target.name] = e.target.value; localStorage.setItem(KEY, JSON.stringify(ans)); };
$("go").onclick = () => {
  const yes = CHECKLIST.filter(c => ans[c.id] === "yes").length, na = CHECKLIST.filter(c => ans[c.id] === "na").length, d = CHECKLIST.length - na;
  const pct = d ? Math.round(yes / d * 100) : 0; const gaps = CHECKLIST.filter(c => ans[c.id] !== "yes" && ans[c.id] !== "na");
  const date = new Date().toLocaleDateString("en-IN", { dateStyle: "long" });
  $("report").innerHTML = `<p class="label">Indicative self-assessment · ${date}</p><h2>Indicative score: ${pct}%</h2>
  <p>${yes} of ${d} applicable checks answered "Yes". ${gaps.length} open gap${gaps.length === 1 ? "" : "s"}.</p>
  ${gaps.length ? `<table><thead><tr><th>#</th><th>Provision</th><th>Requirement</th><th>Status</th></tr></thead><tbody>${gaps.map((g, i) => `<tr><td>${i + 1}</td><td><strong>${g.ref}</strong></td><td>${esc(g.q)}</td><td class="sev">${ans[g.id] ? "Gap" : "Unanswered"}</td></tr>`).join("")}</tbody></table>` : "<p>No open gaps on your answers. Have a qualified professional confirm.</p>"}
  <p class="fine" style="margin-top:1.5rem"><strong>When this applies.</strong> Under Rule 1 of the DPDP Rules, 2025, most obligations above (Rules 3, 5–16, 22, 23) come into force on 13 May 2027; Consent Manager rules (Rule 4) on 13 November 2026. Sources: <a href="${S.act}">DPDP Act, 2023 (MeitY)</a> · <a href="${S.rules}">DPDP Rules, 2025 (MeitY)</a>.</p>
  <p class="fine"><strong>For your assistance only. Not legal advice. No assurance of compliance.</strong> This self-assessment reflects only your own answers. It is not a legal opinion, audit or certification and does not create a lawyer–client relationship. Have a qualified professional of your choice review it before you rely on it. · DPDP Compliance Architect · Built with Akash Sync Agent</p>`;
  $("out").hidden = false; $("out").scrollIntoView({ behavior: "smooth" });
};
$("pdf").onclick = () => print();
