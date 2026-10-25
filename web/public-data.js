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
