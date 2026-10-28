import { PENALTIES, TIMELINE } from "/public-data.js";
import { bindTheme } from "/theme.js"; import { track } from "/track.js"; track("visit");
bindTheme(document.getElementById("theme"));
const esc = s => String(s).replace(/[&<>]/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" })[c]);
document.getElementById("ptable").innerHTML = PENALTIES.map(p => `<tr><td>${esc(p.breach)}</td><td>${p.ref}</td><td class="amt">${esc(p.max)}</td></tr>`).join("");
const fmt = d => new Date(d + "T00:00:00").toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" });
document.getElementById("tl").innerHTML = TIMELINE.map(t => `<li><div><time datetime="${t.date}">${fmt(t.date)}</time><span class="st">${new Date(t.date) <= new Date() ? "In force" : "Coming"}</span></div><div>${esc(t.label)}</div></li>`).join("");

const io = new IntersectionObserver(es => es.forEach(e => { if (e.isIntersecting) { e.target.classList.add("in"); io.unobserve(e.target); } }), { threshold: .1 });
document.querySelectorAll(".reveal").forEach(el => matchMedia("(prefers-reduced-motion: reduce)").matches ? el.classList.add("in") : io.observe(el));
