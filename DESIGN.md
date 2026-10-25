# DESIGN.md · DPDP Compliance Architect (web)

**Subject:** India's Digital Personal Data Protection Act, 2023 and DPDP Rules, 2025, for lawyers, law students, compliance officers and Indian businesses.
**Emotion trigger:** trust, authority, quiet luxury. The feel of a senior counsel's chamber, not a SaaS dashboard.
**Signature visual:** an open leather-bound law book, a brass pen and an oxblood wax seal on an ink-navy desk, with film grain. A slow scroll-linked zoom gives it depth.

## Tokens
| Token | Light | Dark |
|---|---|---|
| bg | #F5EFE3 (cream) | #0B111D (ink) |
| surface | #FBF8F1 | #121A2A |
| ink | #0E1626 | #EEE7D8 |
| muted | #4A5263 | #A9B0BE |
| line | #D9CFBD | #26314A |
| accent (the one accent) | #8C1D2B oxblood | #E0858E |
| label (gold) | #7A5C24 | #D6B46A |

- **Type:** Fraunces (variable editorial serif; hero at 9.5vw, opsz 144) vs Inter. Labels use tiny tracked uppercase.
- **Radius:** 999px for pills and buttons, 16–22px for cards. **Motion:** reveal-on-scroll, a hero parallax and a marquee. All of it is disabled under `prefers-reduced-motion`.
- **Contrast:** the body text and accent pairs are WCAG AA or better in both modes. Every focus state shows a visible 2px accent ring.

## Structure
- `/` **Landing** (story): frosted pill nav, full-bleed textured hero, marquee of obligations, three chapters (I–III), a bento of workspace tools, the penalty Schedule, the commencement timeline, a dark peak-end CTA, the disclaimer and the footer credit ("Built with Akash Sync Agent").
- `/app.html` **App shell** (calm): sidebar tabs (a horizontal scroller on mobile) for Self-assessment, Notice drafter, Breach clock, Section guide and Penalties.

## Sales psychology (honest)
There is one dominant CTA, "Start the free self-assessment", repeated in the hero, the final band and a sticky corner button, plus one ghost secondary. The real deadline (13 May 2027, from Rule 1) is used as true urgency, and risk reversal is real: free, no account, data stays in the browser. There is **no** fake scarcity, testimonials, user counts or statistics.
