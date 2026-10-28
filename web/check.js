// Guided walkthrough. This page makes no network requests (CSP connect-src 'none'). Answers stay in this browser.
import { CHECKLIST, ACTIONS, SAMPLE, SOURCES as S, score, noticeText, breachDeadline } from "/content.js";
const $ = id => document.getElementById(id);
const esc = s => String(s).replace(/[&<>"]/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" })[c]);
const KEY = "dpdp_check", NKEY = "dpdp_notice_pub", BKEY = "dpdp_aware";
let demo = new URLSearchParams(location.search).has("demo");
let ans = demo ? { ...SAMPLE.answers } : JSON.parse(localStorage.getItem(KEY) || "{}");
const save = () => { if (!demo) localStorage.setItem(KEY, JSON.stringify(ans)); };
const NF = ["business", "data", "purpose", "withdraw", "contact", "days"];

function renderQs() {
  $("qf").innerHTML = CHECKLIST.map((c, i) => `<fieldset class="q"><legend><span class="ref">${i + 1} of ${CHECKLIST.length} · ${c.ref}</span></legend><p>${esc(c.q)}</p><div class="opts">${[["yes", "Yes"], ["no", "No"], ["na", "Not applicable"]].map(([v, l]) => `<label><input type="radio" name="${c.id}" value="${v}"${ans[c.id] === v ? " checked" : ""}><span>${l}</span></label>`).join("")}</div>${demo && SAMPLE.notes[c.id] ? `<p class="note">${esc(SAMPLE.notes[c.id])}</p>` : ""}</fieldset>`).join("");
  count();
}
const count = () => { const n = CHECKLIST.filter(c => ans[c.id]).length; $("count").textContent = `${n} of ${CHECKLIST.length} answered`; };
$("qf").onchange = e => { ans[e.target.name] = e.target.value; save(); count(); };

function renderResult() {
  const s = score(ans), app = s.total - s.na;
  $("pct").textContent = s.pct + "%"; $("ring").style.setProperty("--p", s.pct);
  $("sumtxt").textContent = `${s.yes} of ${app} applicable checks met · ${s.gaps.length} gap${s.gaps.length === 1 ? "" : "s"}`;
  $("gaps").innerHTML = s.gaps.length ? s.gaps.map(g => `<tr><td><strong>${g.ref}</strong></td><td>${esc(g.q)}</td><td><span class="tag">${ans[g.id] ? "Gap" : "Unanswered"}</span></td></tr>`).join("") : `<tr><td colspan="3">No open gaps on your answers. Have a qualified professional confirm.</td></tr>`;
  const date = new Date().toLocaleDateString("en-IN", { dateStyle: "long" });
  $("plan").innerHTML = `${demo ? `<p><span class="sample-tag">Sample data</span> ${esc(SAMPLE.name)}. ${esc(SAMPLE.about)}</p>` : ""}
  <p class="label">DPDP action plan · ${date}</p><h3>Indicative score: ${s.pct}% (${s.yes} of ${app} applicable checks)</h3>
  ${s.gaps.length ? `<ol>${s.gaps.map(g => `<li style="margin:.5rem 0"><strong>${g.ref}.</strong> ${esc(ACTIONS[g.id])}</li>`).join("")}</ol>` : "<p>No open gaps on your answers. Have a qualified professional confirm.</p>"}
  <p class="fine" style="font-size:.9rem;color:var(--muted)"><strong>When this applies.</strong> Under Rule 1 of the DPDP Rules, 2025, most of these duties (Rules 3, 5–16, 22, 23) apply from 13 May 2027. Sources: <a href="${S.act}">DPDP Act, 2023 (MeitY)</a> · <a href="${S.rules}">DPDP Rules, 2025 (MeitY)</a>.</p>
  <p style="font-size:.88rem;color:var(--muted)"><strong>For your assistance only. Not legal advice. No assurance of compliance.</strong> This plan reflects only the answers given. It is not a legal opinion, audit or certification and does not create a lawyer–client relationship. Have a qualified professional of your choice review it before you rely on it. · DPDP Compliance Architect · Built with Akash Sync Agent (ASA)</p>`;
}

const nf = () => Object.fromEntries(NF.map(k => [k, $(k).value]));
function renderNotice() { const f = nf(); if (!demo) localStorage.setItem(NKEY, JSON.stringify(f)); const d = parseInt(f.days, 10); $("nout").textContent = noticeText({ ...f, days: d >= 1 && d <= 90 ? String(d) : "" }); }
function fillNotice() { const f = demo ? SAMPLE.notice : JSON.parse(localStorage.getItem(NKEY) || "{}"); NF.forEach(k => { $(k).value = f[k] ?? (k === "days" ? "30" : ""); }); renderNotice(); }
NF.forEach(k => $(k).oninput = renderNotice);
$("copy").onclick = async () => { try { await navigator.clipboard.writeText($("nout").textContent); $("copied").textContent = "Copied."; } catch { $("copied").textContent = "Select the text and copy it."; } };

let deadline = null;
const tick = () => { if (!deadline) { $("bout").textContent = ""; $("bleft").textContent = "Enter a date and time to see the deadline."; return; }
  $("bout").textContent = "Report due by " + deadline.toLocaleString("en-IN", { dateStyle: "medium", timeStyle: "short" });
  const h = (deadline - Date.now()) / 36e5; $("bleft").textContent = h > 0 ? `${Math.floor(h)} h ${Math.floor((h % 1) * 60)} min left (your device's time zone).` : "This deadline has passed. Ask a qualified professional about seeking more time from the Board."; };
const setAware = v => { $("aware").value = v || ""; deadline = v ? breachDeadline(new Date(v).toISOString()) : null; if (!demo) localStorage.setItem(BKEY, v || ""); tick(); };
$("aware").oninput = () => setAware($("aware").value);
setInterval(tick, 60000);

function go(n) {
  n = Math.min(4, Math.max(1, +n || 1));
  if (n >= 3) renderResult();
  document.querySelectorAll(".step").forEach(s => s.classList.toggle("on", s.id === "s" + n));
  document.querySelectorAll("#prog li").forEach(li => { const k = +li.dataset.s; li.classList.toggle("done", k < n); k === n ? li.setAttribute("aria-current", "step") : li.removeAttribute("aria-current"); });
  history.replaceState(null, "", (demo ? "?demo=1" : location.pathname) + "#step" + n); scrollTo({ top: 0 });
  $("h" + n)?.focus?.();
}
document.addEventListener("click", e => { const b = e.target.closest("[data-go]"); if (b) go(b.dataset.go); });
document.querySelectorAll(".step h1, .step h2").forEach(h => h.tabIndex = -1);

function setDemo(on) {
  demo = on; ans = on ? { ...SAMPLE.answers } : JSON.parse(localStorage.getItem(KEY) || "{}");
  $("demobar").hidden = !on; $("demoname").textContent = SAMPLE.name; $("demoabout").textContent = SAMPLE.about; $("demoon").hidden = on;
  renderQs(); fillNotice(); setAware(on ? SAMPLE.aware : localStorage.getItem(BKEY) || "");
}
$("demoon").onclick = () => { setDemo(true); go(2); };
$("demooff").onclick = () => { setDemo(false); history.replaceState(null, "", location.pathname); go(2); };
$("reset").onclick = () => { if (confirm("Clear all answers, notice details and breach time saved in this browser?")) { [KEY, NKEY, BKEY].forEach(k => localStorage.removeItem(k)); setDemo(false); go(1); } };
$("pdf").onclick = () => print();

setDemo(demo);
go((location.hash.match(/step(\d)/) || [])[1] || 1);
