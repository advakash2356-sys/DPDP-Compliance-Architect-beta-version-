// Public reference data shown on the landing page (Schedule to the DPDP Act and Rule 1 dates).
// Schedule to the Act (section 33). Maximum amounts, "may extend to".
export const PENALTIES = [
  { breach: "Failure to take reasonable security safeguards to prevent a personal data breach", ref: "s. 8(5)", max: "₹250 crore" },
  { breach: "Failure to intimate the Board or affected Data Principals of a personal data breach", ref: "s. 8(6)", max: "₹200 crore" },
  { breach: "Non-fulfilment of additional obligations for children", ref: "s. 9", max: "₹200 crore" },
  { breach: "Non-fulfilment of additional obligations of a Significant Data Fiduciary", ref: "s. 10", max: "₹150 crore" },
  { breach: "Breach of the duties of a Data Principal", ref: "s. 15", max: "₹10,000" },
  { breach: "Breach of a voluntary undertaking accepted by the Board", ref: "s. 32", max: "Up to the penalty applicable to the original breach" },
  { breach: "Breach of any other provision of the Act or Rules (residuary)", ref: "Schedule, item 7", max: "₹50 crore" },
];
// Rule 1 of the DPDP Rules, 2025. Dates computed from publication on 13 Nov 2025.
export const TIMELINE = [
  { date: "2023-08-11", label: "DPDP Act, 2023 published in the Gazette", status: "done" },
  { date: "2025-11-13", label: "Rules notified. Rules 1, 2 and 17–21 (the Data Protection Board) in force", status: "done" },
  { date: "2026-11-13", label: "Rule 4 (Consent Managers) comes into force, one year after publication", status: "next" },
  { date: "2027-05-13", label: "Rules 3, 5–16, 22 and 23 come into force, 18 months after publication: notice, security, breach intimation, retention, children, SDF and rights", status: "later" },
];

// Mirror of lib/content.js CHECKLIST (a test keeps them identical). Used by the free self-check.
export const CHECKLIST = [
  { id: "inventory", ref: "s. 4–5", q: "We keep a record of what personal data we collect and why." },
  { id: "notice", ref: "s. 5, Rule 3", q: "Our notice itemises the data and the purpose, in plain language, and is available in English or an Eighth Schedule language." },
  { id: "consent", ref: "s. 6", q: "Consent is taken by a clear affirmative action (no pre-ticked boxes) and is not bundled with unrelated purposes." },
  { id: "withdraw", ref: "s. 6(4)", q: "Withdrawing consent is as easy as giving it." },
  { id: "security", ref: "s. 8(5), Rule 6", q: "We use reasonable security safeguards, such as encryption, access control and logs kept for at least one year." },
  { id: "breach", ref: "s. 8(6), Rule 7", q: "We have a breach plan to inform affected people and the Board without delay, with a detailed report within 72 hours." },
  { id: "retention", ref: "s. 8(7), Rule 8", q: "We erase data when the purpose is served, and know whether the Third Schedule retention periods apply to us." },
  { id: "contact", ref: "s. 8(9)", q: "We publish the business contact of a person (or DPO) who answers questions about processing." },
  { id: "grievance", ref: "s. 13, Rule 14", q: "We have a grievance process with a published response period not exceeding 90 days." },
  { id: "rights", ref: "s. 11, 12, 14", q: "People can request access, correction, erasure and nomination, and we publish how." },
  { id: "children", ref: "s. 9, Rule 10", q: "If we may process data of under-18s, we obtain verifiable parental consent and do not track or target them." },
  { id: "processors", ref: "s. 8(2)", q: "Our vendors (Data Processors) work under a valid contract." },
];
export const SOURCES_PUBLIC = { act: "https://www.meity.gov.in/documents/act-and-policies/digital-personal-data-protection-act-2023", rules: "https://www.meity.gov.in/documents/act-and-policies/digital-personal-data-protection-rules-2025-gDOxUjMtQWa" };
