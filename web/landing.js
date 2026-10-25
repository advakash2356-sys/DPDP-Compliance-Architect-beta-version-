import { PENALTIES, TIMELINE } from "/public-data.js";
import { bindTheme } from "/theme.js";
bindTheme(document.getElementById("theme"));
const esc = s => String(s).replace(/[&<>]/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" })[c]);
document.getElementById("ptable").innerHTML = PENALTIES.map(p => `<tr><td>${esc(p.breach)}</td><td>${p.ref}</td><td class="amt">${esc(p.max)}</td></tr>`).join("");
const fmt = d => new Date(d + "T00:00:00").toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" });
document.getElementById("tl").innerHTML = TIMELINE.map(t => `<li><div><time datetime="${t.date}">${fmt(t.date)}</time><span class="st">${new Date(t.date) <= new Date() ? "In force" : "Coming"}</span></div><div>${esc(t.label)}</div></li>`).join("");

const reduce = matchMedia("(prefers-reduced-motion: reduce)").matches;
const fine = matchMedia("(pointer: fine)").matches;
const reveal = () => { const io = new IntersectionObserver(es => es.forEach(e => { if (e.isIntersecting) { e.target.classList.add("in"); io.unobserve(e.target); } }), { threshold: .12 }); document.querySelectorAll(".reveal").forEach(el => io.observe(el)); };
// bento spotlight (cheap, CSS vars only)
document.querySelectorAll(".tile").forEach(t => t.addEventListener("pointermove", e => { const r = t.getBoundingClientRect(); t.style.setProperty("--mx", e.clientX - r.left + "px"); t.style.setProperty("--my", e.clientY - r.top + "px"); }));
if (reduce || !window.gsap) { document.querySelectorAll(".reveal").forEach(e => e.classList.add("in")); }
else {
  document.documentElement.classList.add("motion"); const { gsap, ScrollTrigger, Lenis } = window; gsap.registerPlugin(ScrollTrigger);
  const lenis = new Lenis({ lerp: .1 }); lenis.on("scroll", ScrollTrigger.update); gsap.ticker.add(t => lenis.raf(t * 1000)); gsap.ticker.lagSmoothing(0);
  document.querySelectorAll('a[href^="#"]').forEach(a => a.addEventListener("click", e => { const el = document.querySelector(a.getAttribute("href")); if (el) { e.preventDefault(); lenis.scrollTo(el, { offset: -20 }); } }));
  // headline split + entrance
  const h = document.querySelector(".split");
  h.innerHTML = h.innerHTML.split(/(<em>.*?<\/em>|\s+)/).filter(Boolean).map(w => /^\s+$/.test(w) ? " " : `<span class="w"><span>${w}</span></span>`).join("");
  gsap.from(".split .w>span", { yPercent: 110, duration: 1.1, ease: "expo.out", stagger: .08, delay: .1 });
  gsap.from([".hero .label", ".hero .lede", ".hero .cta-row"], { y: 24, opacity: 0, duration: .9, ease: "power3.out", stagger: .1, delay: .5 });
  // scroll parallax depth
  document.querySelectorAll("[data-depth]").forEach(el => gsap.to(el, { yPercent: parseFloat(el.dataset.depth) * 260, ease: "none", scrollTrigger: { trigger: "#hero", start: "top top", end: "bottom top", scrub: true } }));
  gsap.to(".hero .wrap", { y: -80, opacity: .2, ease: "none", scrollTrigger: { trigger: "#hero", start: "top top", end: "bottom top", scrub: true } });
  // pinned chapters (desktop)
  ScrollTrigger.matchMedia({ "(min-width: 761px)": () => {
    const sc = gsap.utils.toArray(".pin .scene");
    const tl = gsap.timeline({ scrollTrigger: { trigger: ".pin", start: "top top", end: "+=" + sc.length * 90 + "%", pin: true, scrub: .6 } });
    tl.to(".pin .progress", { width: "100%", ease: "none", duration: sc.length }, 0);
    sc.forEach((s, i) => { if (i) tl.fromTo(s, { opacity: 0, y: 60 }, { opacity: 1, y: 0, duration: .5 }, i - .25); if (i < sc.length - 1) tl.to(s, { opacity: 0, y: -60, duration: .5 }, i + .5); tl.from(s.querySelector(".roman"), { scale: .7, duration: .6 }, Math.max(0, i - .25)); });
  } });
  gsap.fromTo(".bigdate", { xPercent: 12 }, { xPercent: -12, ease: "none", scrollTrigger: { trigger: "#timeline", start: "top bottom", end: "bottom top", scrub: true } });
  gsap.utils.toArray(".bento .tile").forEach((t, i) => gsap.from(t, { y: 50, opacity: 0, duration: .8, ease: "power3.out", delay: (i % 3) * .08, scrollTrigger: { trigger: t, start: "top 90%" } }));
  reveal();
  // pointer parallax on hero layers
  if (fine) { const hero = document.getElementById("hero"); hero.addEventListener("pointermove", e => { const x = e.clientX / innerWidth - .5, y = e.clientY / innerHeight - .5; hero.querySelectorAll("[data-depth]").forEach(l => gsap.to(l, { x: -x * 120 * l.dataset.depth * 4, y: -y * 60 * l.dataset.depth * 4, duration: 1.2, ease: "power3.out", overwrite: "auto" })); }); }
  // magnetic CTAs
  if (fine) document.querySelectorAll(".magnetic").forEach(b => { b.addEventListener("pointermove", e => { const r = b.getBoundingClientRect(); gsap.to(b, { x: (e.clientX - r.left - r.width / 2) * .25, y: (e.clientY - r.top - r.height / 2) * .35, duration: .4, ease: "power3.out" }); }); b.addEventListener("pointerleave", () => gsap.to(b, { x: 0, y: 0, duration: .6, ease: "elastic.out(1,.4)" })); });
  // gold dust in the light (2D canvas, paused off-screen)
  const c = document.querySelector(".dust"), ctx = c.getContext("2d"); let W, H, P = [], on = true;
  const size = () => { const d = Math.min(devicePixelRatio, 1.5); W = c.width = c.offsetWidth * d; H = c.height = c.offsetHeight * d; P = Array.from({ length: innerWidth < 700 ? 40 : 90 }, () => ({ x: Math.random() * W, y: Math.random() * H, r: Math.random() * 1.6 + .3, v: Math.random() * .25 + .05, a: Math.random() * .6 + .2, p: Math.random() * 6.28 })); };
  size(); addEventListener("resize", size);
  new IntersectionObserver(([e]) => on = e.isIntersecting).observe(c);
  const draw = t => { if (on && !document.hidden) { ctx.clearRect(0, 0, W, H); for (const q of P) { q.y -= q.v; q.x += Math.sin(t / 2000 + q.p) * .2; if (q.y < 0) q.y = H; ctx.globalAlpha = q.a * (.6 + .4 * Math.sin(t / 900 + q.p)); ctx.fillStyle = "#E8CF95"; ctx.beginPath(); ctx.arc(q.x, q.y, q.r * 1.5, 0, 6.28); ctx.fill(); } } requestAnimationFrame(draw); };
  requestAnimationFrame(draw);
}
