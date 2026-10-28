// Anonymous funnel events with ?src= channel tag (kept for the session).
const q = new URLSearchParams(location.search).get("src"); if (q) sessionStorage.setItem("dpdp_src", q.slice(0, 60));
const vid = localStorage.getItem("dpdp_vid") || (localStorage.setItem("dpdp_vid", crypto.randomUUID()), localStorage.getItem("dpdp_vid"));
export const track = (ev, text = "") => fetch("/api/track", { method: "POST", headers: { "content-type": "application/json" }, body: JSON.stringify({ ev, vid, src: sessionStorage.getItem("dpdp_src") || "direct", page: location.pathname, text }), keepalive: true }).catch(() => {});
