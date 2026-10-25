# Audit, 25 Oct 2026

**What it was:** an Android (Kotlin, Jetpack Compose, Room) app called "Data Rakshak", generated with Google AI Studio. The Vercel project linked to this repo had no web output, so the production URL returned 404.

## Features (Android source)
| Feature | Status |
|---|---|
| Compliance guide / statutory data (ComplianceGuideData, ComplianceGuideService) | Real static content; several legal errors (fixed, below) |
| URL / website scanner (ComplianceViewModel) | **Simulated**: it does not crawl and uses hard-coded "simulatedPrivacyText" |
| Agentic auditor (AgenticAuditorService) | Keyword matching plus Gemini if a key is set; otherwise canned fallbacks |
| Live data-monitoring feed | **Fake**: random events generated every 4 s |
| OTP login | **Simulated**: a random OTP is shown on screen; demo passcode "2356" |
| Google auth / admin user management | Untested, needs a configured client and admin email |
| Breach drill, 72-hour timer | Simulation (labelled as a drill) |
| Erasure / retention purge | Real local DB deletes; the "retention" purge deletes logs older than 30 s (demo) |
| Legal update feed | Manual entries only; the README's "real-time legal tracking" claim was false |
| Room persistence | Real (local) |

## Legal accuracy fixes (checked against the MeitY / Gazette texts)
- **Nomination** was cited as s. 13. It is **s. 14**; s. 13 is grievance redressal.
- An invented **"Rule 4.2" for games** with "national ID visual proof" was replaced with **Rule 10** (verifiable parental consent).
- "Breach within 72 hours, **₹150 crore under Chapter VII**" was wrong. Failing to intimate a breach (s. 8(6)) carries **up to ₹200 crore**, and Rule 7 sets the 72-hour detailed report.
- "Section 15 mandates breach reporting" was wrong. s. 15 sets out the duties of Data Principals; breach intimation is **s. 8(6) and Rule 7**.
- ₹250 crore was cited for notice and consent breaches. ₹250 crore applies only to **s. 8(5) security safeguards**; notice and consent breaches fall under the **₹50 crore residuary** item.
- The grievance officer was attributed to "s. 13/14". It is now **s. 8(9), s. 8(10) and s. 13**, with a response period **not exceeding 90 days** under Rule 14(3).
- Commencement: Rules notified 13 Nov 2025 (G.S.R. 846(E)). Rule 4 (Consent Managers) commences 13 Nov 2026; Rules 3, 5–16, 22 and 23 commence 13 May 2027 (computed).

## What the new web app does
Every web feature is real and runs client-side: the self-assessment, notice drafter, breach-deadline calculator, section guide and penalty Schedule. There is no AI, no scanner and no login, and nothing claims to.
