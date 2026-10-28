import { bindTheme } from "/theme.js";
bindTheme(document.getElementById("theme"));
const CLIENT_ID = "81710705394-thc3444tmiu97ms60hcjcjesi56mb6ab.apps.googleusercontent.com";
const $ = id => document.getElementById(id), DAY = 864e5;
const esc = s => String(s ?? "").replace(/[&<>"]/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" })[c]);
const rows = (h, r) => `<thead><tr>${h.map(x => `<th>${x}</th>`).join("")}</tr></thead><tbody>${r.map(x => `<tr>${x.map(c => `<td>${esc(c)}</td>`).join("")}</tr>`).join("") || `<tr><td colspan="${h.length}">No data yet</td></tr>`}</tbody>`;
let data;
async function load(tok) {
  const r = await fetch("/api/admin", { headers: { authorization: "Bearer " + tok } }); const d = await r.json().catch(() => ({}));
  if (!r.ok) { $("amsg").textContent = d.error || "Could not load."; return; }
  data = d; $("gsi").hidden = true; $("dash").hidden = false; render(d);
}
function render({ users, reviews, events, now }) {
  const U = users.filter(u => u.sub !== "guest"), vis = new Set(events.filter(e => e.ev === "visit").map(e => e.vid));
  const act = (ms) => new Set(events.filter(e => now - e.at < ms).map(e => e.vid)).size;
  const new7 = U.filter(u => now - u.trialStart < 7 * DAY).length, prev7 = U.filter(u => now - u.trialStart >= 7 * DAY && now - u.trialStart < 14 * DAY).length;
  const growth = prev7 ? Math.round((new7 - prev7) / prev7 * 100) + "%" : (new7 ? "new" : "–");
  const seen = vid => events.filter(e => e.vid === vid).map(e => e.at); const vids = [...new Set(events.map(e => e.vid))];
  const ret = vids.filter(v => { const t = seen(v); return Math.max(...t) - Math.min(...t) > DAY; }).length;
  const k = [["Signed-up users", U.length], ["Visitors", vis.size], ["Active 1 day / 7 days", act(DAY) + " / " + act(7 * DAY)], ["Sign-ups this week vs last", `${new7} (${growth})`], ["Returning visitors", vids.length ? Math.round(ret / vids.length * 100) + "%" : "–"], ["Lawyer review requests", reviews.length], ["Paid", U.filter(u => u.paidUntil > now).length]];
  $("kpis").innerHTML = k.map(([l, v]) => `<div class="tile glass"><p class="label">${l}</p><h3>${esc(v)}</h3></div>`).join("");
  const cnt = ev => new Set(events.filter(e => e.ev === ev).map(e => e.vid)).size;
  const F = [["Visit", cnt("visit")], ["Started self-check", cnt("check_start")], ["Finished self-check", cnt("check_done")], ["Downloaded report", cnt("report_download")], ["Opened review form", cnt("review_open")], ["Requested review", reviews.length], ["Signed up (trial)", U.length]];
  $("funnel").innerHTML = rows(["Step", "People", "% of visits"], F.map(([s, n]) => [s, n, F[0][1] ? Math.round(n / F[0][1] * 100) + "%" : "–"]));
  const ch = {}; events.forEach(e => { const c = ch[e.src || "direct"] ??= { v: new Set(), d: new Set() }; if (e.ev === "visit") c.v.add(e.vid); if (e.ev === "check_done") c.d.add(e.vid); });
  $("chan").innerHTML = rows(["Source", "Visitors", "Finished check"], Object.entries(ch).sort((a, b) => b[1].v.size - a[1].v.size).map(([s, c]) => [s, c.v.size, c.d.size]));
  const st = u => u.paidUntil > now ? "Paid" : now < u.trialStart + 7 * DAY ? "In trial" : "Trial ended";
  $("trial").innerHTML = rows(["Status", "Users"], ["In trial", "Trial ended", "Paid"].map(s => [s, U.filter(u => st(u) === s).length]));
  $("rev").innerHTML = rows(["When", "Ref", "Name", "Email", "Phone", "Business", "Role", "Score", "Gaps", "Note"], reviews.map(r => [new Date(r.at || 0).toLocaleString("en-IN"), r.id, r.name, r.email, r.phone, r.org, r.role, r.pct, r.gaps, r.note]));
  $("fb").innerHTML = events.filter(e => e.ev === "feedback").map(e => `<li>${esc(e.text)} <span class="fine">(${esc(e.src)})</span></li>`).join("") || "<li>No feedback yet</li>";
}
$("csv").onclick = () => {
  const q = v => `"${String(v ?? "").replace(/"/g, '""')}"`; const out = [];
  for (const [name, list] of [["users", data.users], ["reviews", data.reviews], ["events", data.events]]) { out.push(name); const h = [...new Set(list.flatMap(Object.keys))]; out.push(h.map(q).join(",")); list.forEach(r => out.push(h.map(k => q(r[k])).join(","))); out.push(""); }
  const a = document.createElement("a"); a.href = URL.createObjectURL(new Blob([out.join("\n")], { type: "text/csv" })); a.download = `dpdp-metrics-${new Date().toISOString().slice(0, 10)}.csv`; a.click();
};
const init = () => { google.accounts.id.initialize({ client_id: CLIENT_ID, callback: r => load(r.credential), ux_mode: "popup" }); google.accounts.id.renderButton($("gsi"), { theme: "filled_black", size: "large", shape: "pill", text: "signin_with" }); };
const t = setInterval(() => { if (window.google?.accounts?.id) { clearInterval(t); init(); } }, 200);
