// Verified against the DPDP Act, 2023 (Act 22 of 2023, Gazette 11 Aug 2023) and the
// DPDP Rules, 2025 (G.S.R. 846(E), 13 Nov 2025; corrigendum G.S.R. 892(E)). For assistance only.
// web/content.js is a byte-identical copy served to the browser (a test checks this).
export const SOURCES = {
  act: "https://www.meity.gov.in/documents/act-and-policies/digital-personal-data-protection-act-2023",
  rules: "https://www.meity.gov.in/documents/act-and-policies/digital-personal-data-protection-rules-2025-gDOxUjMtQWa",
};
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
export const SECTIONS = [
  ["s. 4", "Grounds for processing", "Personal data may be processed only for a lawful purpose, with consent or for a 'certain legitimate use' under s. 7."],
  ["s. 5", "Notice", "Every consent request must be accompanied or preceded by a notice: the personal data, the purpose, how to exercise rights and how to complain to the Board. Rule 3 sets the content."],
  ["s. 6", "Consent", "Consent must be free, specific, informed, unconditional and unambiguous, by a clear affirmative action. It must be as easy to withdraw as to give (s. 6(4)). Consent Managers: s. 6(7)–(9), Rule 4."],
  ["s. 7", "Certain legitimate uses", "Limited non-consent grounds, such as data the person voluntarily provided for a specified purpose, State functions, legal obligations, medical emergencies and employment."],
  ["s. 8", "General obligations", "Accuracy, reasonable security safeguards (8(5), Rule 6), breach intimation (8(6), Rule 7), erasure once the purpose is served (8(7), Rule 8), a published contact (8(9)) and grievance redressal (8(10))."],
  ["s. 9", "Children", "A child is under 18. Verifiable parental consent is required (Rule 10). No tracking, behavioural monitoring or targeted advertising directed at children (s. 9(3)); Rule 12 and the Fourth Schedule set exemptions."],
  ["s. 10", "Significant Data Fiduciaries", "If notified as an SDF: a Data Protection Officer based in India, an independent data auditor, and periodic Data Protection Impact Assessments and audits (Rule 13)."],
  ["s. 11", "Right to access information", "A summary of the data processed and the processing activities, and the identities of others it was shared with."],
  ["s. 12", "Right to correction and erasure", "Correction, completion, updating and erasure of personal data, subject to retention required by law."],
  ["s. 13", "Right of grievance redressal", "A readily available means of grievance redressal. Rule 14(3) refers to a period not exceeding 90 days. The person must exhaust this before approaching the Board."],
  ["s. 14", "Right to nominate", "A Data Principal may nominate another individual to exercise her rights in the event of death or incapacity."],
  ["s. 15", "Duties of Data Principals", "Comply with law, do not impersonate, do not suppress material information, do not file false or frivolous complaints."],
  ["s. 16", "Cross-border transfer", "Transfers are allowed except to countries the Central Government restricts by notification; stricter sectoral laws still apply."],
  ["s. 33", "Penalties", "The Board may impose monetary penalties up to the amounts in the Schedule, after an inquiry, considering the factors in s. 33(2)."],
  ["s. 44(2)", "IT Act s. 43A", "Section 43A of the IT Act, 2000 (compensation for failure to protect data) is omitted once this provision comes into force."],
];
// Rule 1 of the DPDP Rules, 2025. Dates computed from publication on 13 Nov 2025.
export const TIMELINE = [
  { date: "2023-08-11", label: "DPDP Act, 2023 published in the Gazette", status: "done" },
  { date: "2025-11-13", label: "Rules notified. Rules 1, 2 and 17–21 (the Data Protection Board) in force", status: "done" },
  { date: "2026-11-13", label: "Rule 4 (Consent Managers) comes into force, one year after publication", status: "next" },
  { date: "2027-05-13", label: "Rules 3, 5–16, 22 and 23 come into force, 18 months after publication: notice, security, breach intimation, retention, children, SDF and rights", status: "later" },
];
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
export function score(answers) {
  const n = CHECKLIST.length;
  const yes = CHECKLIST.filter(c => answers[c.id] === "yes").length;
  const na = CHECKLIST.filter(c => answers[c.id] === "na").length;
  const denom = n - na;
  return { yes, na, total: n, pct: denom ? Math.round((yes / denom) * 100) : 0,
    gaps: CHECKLIST.filter(c => answers[c.id] !== "yes" && answers[c.id] !== "na") };
}
export function breachDeadline(awareISO) {
  const t = new Date(awareISO);
  if (isNaN(t)) return null;
  return new Date(t.getTime() + 72 * 3600 * 1000);
}
export function noticeText(f) {
  const v = k => (f[k] || "").trim() || `[${k}]`;
  return `PRIVACY NOTICE (Section 5, DPDP Act 2023 and Rule 3, DPDP Rules 2025)

${v("business")} ("we") will process the following personal data about you:
${v("data")}

Purpose: we use it only to ${v("purpose")}.

Your consent: you may withdraw consent at any time, as easily as you gave it, at ${v("withdraw")}. Withdrawal does not affect processing done before it.

Your rights: you may ask for a summary of your data, correction, completion, updating or erasure, and you may nominate someone to act for you. Write to ${v("contact")}.

Grievances: contact ${v("contact")}. We will respond within ${v("days")} days. If you are not satisfied, you may complain to the Data Protection Board of India.

Draft generated by DPDP Compliance Architect. Template for your assistance only, not legal advice; have it reviewed by a qualified professional before use.`;
}
// Suggested next steps per check, for the action plan. For assistance only; not legal advice.
export const ACTIONS = {
  inventory: "List each type of personal data you collect (forms, orders, WhatsApp, staff records), where it is kept and why. This record is the base for every other step.",
  notice: "Write a standalone notice that itemises the personal data and each purpose, in clear plain language, with links to withdraw consent, exercise rights and complain to the Board (s. 5, Rule 3). Use the notice template in step 4 as a starting point.",
  consent: "Replace pre-ticked or bundled checkboxes with a separate, unticked opt-in for each purpose, such as marketing messages (s. 6(1)).",
  withdraw: "Add a one-click or one-message way to withdraw consent, as easy as giving it, and stop the processing that relied on it (s. 6(4), 6(6)).",
  security: "Put basic safeguards in place: encryption, access control, backups, and logs kept for at least one year (s. 8(5), Rule 6).",
  breach: "Write a short breach plan: who decides, how affected people and the Board are told without delay, and the detailed report to the Board within 72 hours (s. 8(6), Rule 7). Use the breach clock in step 4.",
  retention: "Set a deletion period for each purpose and erase data once the purpose is served, unless a law requires you to keep it (s. 8(7), Rule 8).",
  contact: "Publish the business contact details of a person who can answer questions about your processing, on your website or app (s. 8(9), Rule 9).",
  grievance: "Set up a grievance channel and publish your response period, which must not exceed 90 days (s. 8(10), s. 13, Rule 14(3)).",
  rights: "Publish how people can ask for access, correction, erasure and nomination, and how you verify the request (s. 11–14, Rule 14).",
  children: "If you may have users under 18, add verifiable parental consent and stop any tracking or targeted ads aimed at them (s. 9, Rule 10).",
  processors: "Sign a written contract with each vendor that handles personal data for you, such as payment, courier, hosting or messaging providers (s. 8(2)).",
};
// Clearly fictional sample business for the demo mode. Not a real company.
export const SAMPLE = {
  name: "Kesar Threads (sample)",
  about: "A fictional three-person online saree shop in Delhi. It sells through its own website and WhatsApp, takes payments through a payment gateway, ships by courier, and has about 4,000 customers.",
  answers: { inventory: "yes", notice: "no", consent: "no", withdraw: "no", security: "yes", breach: "no", retention: "no", contact: "yes", grievance: "no", rights: "no", children: "na", processors: "no" },
  notes: { consent: "Sample: the checkout has a pre-ticked box for WhatsApp offers.", children: "Sample: sells to adults only. Check this for your own business.", processors: "Sample: no written contract with the courier or the website host." },
  notice: { business: "Kesar Threads (sample)", data: "Name, mobile number, email address, delivery address, order history", purpose: "process and deliver your orders and handle returns", withdraw: "kesarthreads.example/privacy or WhatsApp 'STOP'", contact: "Grievance Officer, privacy@kesarthreads.example", days: "30" },
  aware: "2026-10-28T10:00",
};
