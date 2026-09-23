package com.example.data

enum class RegulationDomain(val displayName: String, val badgeColorHex: Long) {
    ALL("All Frameworks", 0xFF0D9488),
    DPDP_ACT_2023("DPDP Act 2023", 0xFF2563EB),
    AADHAAR_UIDAI("Aadhaar (UIDAI)", 0xFFD97706),
    RBI_REGULATIONS("RBI Master Directions", 0xFF7C3AED),
    CROSS_STATUTORY("Cross-Regulatory Matrix", 0xFF059669)
}

enum class ComplianceSeverity(val label: String, val badgeColorHex: Long) {
    CRITICAL("MANDATORY / STATUTORY", 0xFFDC2626),
    HIGH("HIGH RISK", 0xFFEA580C),
    MEDIUM("STANDARD PROCEDURE", 0xFF0284C7),
    ADVISORY("BEST PRACTICE", 0xFF16A34A)
}

data class GroundingSource(
    val title: String,
    val uri: String,
    val snippet: String = ""
)

data class GroundedGuideResult(
    val query: String,
    val synthesizedText: String,
    val domain: RegulationDomain,
    val sources: List<GroundingSource>,
    val timestamp: Long = System.currentTimeMillis(),
    val isSimulatedOrFallback: Boolean = false
)

data class ComplianceGuideItem(
    val id: String,
    val domain: RegulationDomain,
    val sectionRef: String,
    val title: String,
    val executiveSummary: String,
    val detailedRequirements: List<String>,
    val technicalImplementationCode: String,
    val statutoryPenalty: String,
    val officialGazetteRef: String,
    val officialUrl: String,
    val severity: ComplianceSeverity,
    val tags: List<String> = emptyList()
)

object ComplianceGuideKnowledgeBase {

    val items: List<ComplianceGuideItem> = listOf(
        // ==========================================
        // 1. DPDP ACT 2023 PROVISIONS
        // ==========================================
        ComplianceGuideItem(
            id = "dpdp-sec-5",
            domain = RegulationDomain.DPDP_ACT_2023,
            sectionRef = "DPDPA 2023 Sec. 5",
            title = "Notice Obligation Preceding or Accompanying Consent",
            executiveSummary = "Data Fiduciaries must present an itemized, unbundled, and easily comprehensible notice in English and all 22 Eighth Schedule languages before or at the time of requesting consent.",
            detailedRequirements = listOf(
                "Notice must explicitly state the specific personal data collected and the distinct purpose of processing.",
                "Must inform Data Principals of the manner of exercising right of withdrawal (Sec 6(4)) and grievance redressal (Sec 13).",
                "Must state how to make a complaint to the Data Protection Board of India (DPBI).",
                "Notice cannot be hidden inside lengthy General Terms & Conditions (unbundled notice rule)."
            ),
            technicalImplementationCode = """
// Jetpack Compose Unbundled DPDP Section 5 Notice Sheet
@Composable
fun DpdpNoticeSheet(
    dataPointsCollected: List<String>,
    purpose: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("DPDP Act 2023 Section 5 Statutory Notice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Data Items: " + dataPointsCollected.joinToString(", "))
            Text("Specified Purpose: " + purpose)
            Text("Grievance Officer: dpo@company.in | DPBI Redressal Available")
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAccept) { Text("Acknowledge & Proceed") }
                OutlinedButton(onClick = onDecline) { Text("Decline") }
            }
        }
    }
}
            """.trimIndent(),
            statutoryPenalty = "Up to ₹250 Crore for major non-compliance under Schedule 1.",
            officialGazetteRef = "The Gazette of India Extraordinary, Act No. 22 of 2023, Chapter II, Section 5",
            officialUrl = "https://www.meity.gov.in/content/digital-personal-data-protection-act-2023",
            severity = ComplianceSeverity.CRITICAL,
            tags = listOf("Notice", "Section 5", "Consent", "MeitY", "Multilingual")
        ),
        ComplianceGuideItem(
            id = "dpdp-sec-6",
            domain = RegulationDomain.DPDP_ACT_2023,
            sectionRef = "DPDPA 2023 Sec. 6",
            title = "Conditions for Valid Affirmative Consent & Easy Withdrawal",
            executiveSummary = "Consent must be free, specific, informed, unconditional, and unambiguous with a clear affirmative action. Consent withdrawal must be as effortless as giving consent.",
            detailedRequirements = listOf(
                "Pre-ticked checkboxes or implicit opt-ins are strictly prohibited under Indian law.",
                "Consent cannot be made a condition for service if the data is not strictly necessary for that service.",
                "Withdrawal mechanism must be self-serve, in-app, and frictionless (one-click withdrawal).",
                "Consent management must integrate with registered Consent Managers."
            ),
            technicalImplementationCode = """
// Dynamic Consent Manager with Immutable Room Audit Log
suspend fun recordConsentState(
    userId: String,
    consentType: String,
    granted: Boolean,
    dao: ConsentLogDao,
    auditDao: AuditLogDao
) {
    val token = "DPDP-SEC6-" + UUID.randomUUID().toString().take(8)
    val log = AuditLog(
        actionType = if (granted) "CONSENT_RECORDED" else "CONSENT_REVOKED",
        entityAffected = "ConsentLedger",
        actor = "DATA_PRINCIPAL",
        description = "Consent for " + consentType + " set to " + granted + " by user " + userId,
        statutoryReference = "DPDP Act 2023 Sec 6",
        auditHash = token
    )
    auditDao.insertAuditLog(log)
}
            """.trimIndent(),
            statutoryPenalty = "Up to ₹250 Crore under Schedule 1 for unlawful data processing.",
            officialGazetteRef = "The Gazette of India, Act No. 22 of 2023, Chapter II, Section 6",
            officialUrl = "https://www.meity.gov.in/content/digital-personal-data-protection-act-2023",
            severity = ComplianceSeverity.CRITICAL,
            tags = listOf("Consent", "Section 6", "Withdrawal", "Affirmative Action", "Opt-In")
        ),
        ComplianceGuideItem(
            id = "dpdp-sec-9",
            domain = RegulationDomain.DPDP_ACT_2023,
            sectionRef = "DPDPA 2023 Sec. 9",
            title = "Special Obligations in Relation to Children & Minors (<18 Years)",
            executiveSummary = "Processing personal data of a child requires verifiable consent from parents/guardians. Behavioral monitoring, targeted advertising, and detrimental tracking of children are strictly barred.",
            detailedRequirements = listOf(
                "Mandatory age-gating and verifiable parental consent (VPC) before gathering any child records.",
                "Strict ban on tracking or behavioral monitoring directed at children (<18 years).",
                "Targeted ad SDKs (e.g. AdMob personalized tracking) must be completely deactivated for minor profiles.",
                "Penalties apply to gaming, educational apps, and social feeds accessible to minors."
            ),
            technicalImplementationCode = """
// Minor Verification & AdMob Behavioral Tracking Blocker
fun configureAnalyticsForUser(age: Int, isParentalConsentVerified: Boolean) {
    if (age < 18) {
        // DPDP Sec. 9 Compliance: Kill all behavioral tracking & personalized ad trackers
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(false)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .build()
        )
        if (!isParentalConsentVerified) {
            throw SecurityException("DPDP Sec 9 Violation: Parental consent unverified for minor.")
        }
    }
}
            """.trimIndent(),
            statutoryPenalty = "Up to ₹200 Crore for non-fulfillment of obligations regarding children.",
            officialGazetteRef = "The Gazette of India, Act No. 22 of 2023, Chapter II, Section 9",
            officialUrl = "https://www.meity.gov.in/content/digital-personal-data-protection-act-2023",
            severity = ComplianceSeverity.CRITICAL,
            tags = listOf("Children", "Minors", "Section 9", "Parental Consent", "AdMob Ban")
        ),
        ComplianceGuideItem(
            id = "dpdp-sec-12",
            domain = RegulationDomain.DPDP_ACT_2023,
            sectionRef = "DPDPA 2023 Sec. 12",
            title = "Right to Correction, Completion, Updating, and Erasure (Data Wipe)",
            executiveSummary = "Data Principals have a statutory right to request erasure of their personal data unless retention is mandatory under legal obligation. Cascading deletion across databases is required.",
            detailedRequirements = listOf(
                "One-click erasure must delete rows across all local SQLite/Room tables and remote servers.",
                "Data Fiduciary must cease retention as soon as the specified purpose is no longer being served.",
                "Correction workflows must allow self-service updates for inaccurate or incomplete profiles.",
                "Erasure confirmation token must be issued to the user for statutory audit records."
            ),
            technicalImplementationCode = """
// Atomic Erasure Transaction in Room Database (Sec. 12(3))
@Transaction
suspend fun executeStatutoryErasure(database: AppDatabase, userId: String): String {
    database.userProfileDao().deleteProfileByUserId(userId)
    database.nomineeDao().deleteNomineeForUser(userId)
    database.grievanceReportDao().deleteReportsForUser(userId)
    
    val purgeToken = "DPDP-SEC12-PURGE-" + System.currentTimeMillis()
    val auditEntry = AuditLog(
        actionType = "DATA_ERASED",
        entityAffected = "All_Tables",
        actor = "DATA_PRINCIPAL",
        description = "Total right to erasure executed for " + userId + " under Sec 12(3)",
        statutoryReference = "DPDP Act 2023 Sec 12(3)",
        auditHash = purgeToken
    )
    database.auditLogDao().insertAuditLog(auditEntry)
    return purgeToken
}
            """.trimIndent(),
            statutoryPenalty = "Up to ₹50 Crore for failing to fulfill data principal rights under Section 12.",
            officialGazetteRef = "The Gazette of India, Act No. 22 of 2023, Chapter III, Section 12",
            officialUrl = "https://www.meity.gov.in/content/digital-personal-data-protection-act-2023",
            severity = ComplianceSeverity.CRITICAL,
            tags = listOf("Erasure", "Right to Delete", "Section 12", "Data Minimization", "Correction")
        ),
        ComplianceGuideItem(
            id = "dpdp-sec-14",
            domain = RegulationDomain.DPDP_ACT_2023,
            sectionRef = "DPDPA 2023 Sec. 14",
            title = "Right to Nominate Legal Representatives in Event of Death or Incapacity",
            executiveSummary = "Data Principals must be empowered to designate a nominee to exercise their data protection rights in the event of death or physical/mental incapacity.",
            detailedRequirements = listOf(
                "App must provide a dedicated UI form to register and update legal nominee credentials.",
                "Nominee records must be encrypted and safeguarded with strict role-based access.",
                "Nominee can exercise rights under Sections 11, 12, and 13 upon verification."
            ),
            technicalImplementationCode = """
// Register Nominee for DPDP Section 14
suspend fun registerNominee(
    database: AppDatabase,
    userId: String,
    nomineeName: String,
    relationship: String,
    contactInfo: String
) {
    val nominee = Nominee(
        userId = userId,
        nomineeName = nomineeName,
        relationship = relationship,
        contactInfo = contactInfo,
        registeredTimestamp = System.currentTimeMillis()
    )
    database.nomineeDao().insertNominee(nominee)
}
            """.trimIndent(),
            statutoryPenalty = "Up to ₹50 Crore under Schedule 1 for breach of Data Principal rights.",
            officialGazetteRef = "The Gazette of India, Act No. 22 of 2023, Chapter III, Section 14",
            officialUrl = "https://www.meity.gov.in/content/digital-personal-data-protection-act-2023",
            severity = ComplianceSeverity.MEDIUM,
            tags = listOf("Nomination", "Section 14", "Incapacity", "Legal Delegate")
        ),

        // ==========================================
        // 2. AADHAAR & UIDAI COMPLIANCE PROVISIONS
        // ==========================================
        ComplianceGuideItem(
            id = "uidai-masked-aadhaar",
            domain = RegulationDomain.AADHAAR_UIDAI,
            sectionRef = "UIDAI Circular No. 1 of 2018 / Aadhaar Act Sec. 29",
            title = "Mandatory Masked Aadhaar & Storage Prohibition",
            executiveSummary = "Storing complete 12-digit Aadhaar numbers in plaintext on application databases is strictly illegal under the Aadhaar Act. Only the last 4 digits (e.g. `XXXXXXXX1234`) or Virtual ID (VID) may be stored or displayed.",
            detailedRequirements = listOf(
                "Zero Plaintext Storage: Raw 12-digit Aadhaar numbers MUST NEVER be saved in SQLite, Room, SharedPreferences, or Logs.",
                "Masking Format: First 8 digits must be replaced with 'X' or bullet characters: `XXXXXXXX3849` or `•••• •••• 3849`.",
                "Only UIDAI-licensed Authentication User Agencies (AUAs) / KYC User Agencies (KUAs) can store Aadhaar in an Aadhaar Data Vault.",
                "Non-AUA entities must use Offline Paperless e-KYC (XML/QR Code) and instantly discard full Aadhaar."
            ),
            technicalImplementationCode = """
// UIDAI Compliant In-Memory Masking & Aadhaar Sanitizer
object AadhaarComplianceUtils {
    private val AADHAAR_REGEX = Regex("^[2-9][0-9]{3}[0-9]{4}[0-9]{4}${'$'}")

    fun isValidAadhaarStructure(aadhaar: String): Boolean {
        val clean = aadhaar.replace(" ", "").replace("-", "")
        return clean.matches(AADHAAR_REGEX) && VerhoeffAlgorithm.validateVerhoeff(clean)
    }

    fun maskAadhaar(rawAadhaar: String): String {
        val clean = rawAadhaar.replace(" ", "").replace("-", "")
        if (clean.length != 12) return "XXXXXXXXXXXX"
        return "XXXXXXXX" + clean.takeLast(4)
    }

    fun sanitizeForStorage(rawAadhaar: String): StoredAadhaarRef {
        val last4 = rawAadhaar.takeLast(4)
        val masked = "XXXXXXXX" + last4
        val refHash = sha256(rawAadhaar + "UIDAI_APP_SALT")
        return StoredAadhaarRef(maskedDisplay = masked, referenceHash = refHash, last4 = last4)
    }
}
            """.trimIndent(),
            statutoryPenalty = "Imprisonment up to 3 years and fine up to ₹10,000 to ₹1 Lakh per day under Aadhaar Act Sec 38-42. License cancellation for AUAs.",
            officialGazetteRef = "UIDAI Circular Ref: F. No. 13012/171/2017-UIDAI (Tech), Masked Aadhaar Directives",
            officialUrl = "https://uidai.gov.in/en/ecosystem/authentication-ecosystem/aadhaar-data-vault.html",
            severity = ComplianceSeverity.CRITICAL,
            tags = listOf("Aadhaar", "UIDAI", "Masked Aadhaar", "Aadhaar Act Sec 29", "Data Vault", "Zero Storage")
        ),
        ComplianceGuideItem(
            id = "uidai-data-vault",
            domain = RegulationDomain.AADHAAR_UIDAI,
            sectionRef = "UIDAI Circular F. No. 11020/205/2017",
            title = "Aadhaar Data Vault (ADV) Architecture Guidelines",
            executiveSummary = "AUAs/KUAs handling Aadhaar must isolate all Aadhaar numbers inside a dedicated, encrypted, hardware security module (HSM) backed Aadhaar Data Vault with reference keys (UUID/GUID).",
            detailedRequirements = listOf(
                "Aadhaar numbers must reside in a separate encrypted database (Aadhaar Data Vault).",
                "Internal systems can only communicate using generated Reference Keys (GUID/UUID), never raw Aadhaar.",
                "Encryption algorithm must be AES-256 with keys managed inside FIPS 140-2 Level 3 Hardware Security Modules.",
                "Audit logs must record every access to the Aadhaar Vault with purpose, timestamp, and operator ID."
            ),
            technicalImplementationCode = """
// Aadhaar Reference Key Architecture (Room Schema)
@Entity(tableName = "user_identity_tokens")
data class UserIdentityToken(
    @PrimaryKey val userId: String,
    val referenceKey: String, // GUID generated by Aadhaar Data Vault (never store raw Aadhaar)
    val maskedAadhaar: String, // e.g. "XXXXXXXX5412"
    val kycTimestamp: Long,
    val kycMethod: String // e.g. "OFFLINE_XML_EKyC"
)
            """.trimIndent(),
            statutoryPenalty = "Suspension of AUA/KUA license and fine up to ₹1 Crore under UIDAI Enforcement Regulations.",
            officialGazetteRef = "UIDAI Circular F. No. 11020/205/2017-UIDAI (Auth-I)",
            officialUrl = "https://uidai.gov.in/",
            severity = ComplianceSeverity.HIGH,
            tags = listOf("Aadhaar Data Vault", "HSM", "Reference Key", "AES-256", "AUA/KUA")
        ),
        ComplianceGuideItem(
            id = "uidai-offline-ekyc",
            domain = RegulationDomain.AADHAAR_UIDAI,
            sectionRef = "Aadhaar (Electronic Service Delivery) Regulations 2019",
            title = "Offline Paperless e-KYC (XML & QR Code) Verification Protocol",
            executiveSummary = "Allows entities to verify identity without accessing central UIDAI servers. Uses zip/XML signed with UIDAI digital certificate and a 4-digit share code.",
            detailedRequirements = listOf(
                "Entity downloads password-protected XML/ZIP with 4-digit share code.",
                "Verify UIDAI Digital Signature using UIDAI public certificate before trusting name/photo/DOB.",
                "Obtain explicit consent disclosing exact purpose of offline e-KYC.",
                "Immediately purge raw XML after extracting verified name and masked identity."
            ),
            technicalImplementationCode = """
// Offline e-KYC Verification & In-Memory Extraction
fun verifyOfflineEkyc(xmlBytes: ByteArray, shareCode: String): VerifiedEkycData {
    // 1. Decrypt using Share Code
    // 2. Validate XML Signature against UIDAI Public Key
    // 3. Extract Name, DOB, Gender, and Masked Aadhaar
    // 4. Zero-out raw bytes from memory
    Arrays.fill(xmlBytes, 0.toByte())
    return VerifiedEkycData(isSignatureValid = true, maskedAadhaar = "XXXXXXXX9812")
}
            """.trimIndent(),
            statutoryPenalty = "Penal action under Section 43A of IT Act and Aadhaar Act 2016 for unauthorized access.",
            officialGazetteRef = "Aadhaar and Other Laws (Amendment) Act 2019, Section 8A",
            officialUrl = "https://uidai.gov.in/en/ecosystem/authentication-ecosystem/offline-paperless-e-kyc.html",
            severity = ComplianceSeverity.HIGH,
            tags = listOf("e-KYC", "Offline XML", "QR Code", "Share Code", "Digital Signature")
        ),

        // ==========================================
        // 3. RBI MASTER DIRECTIONS & FINTECH RULES
        // ==========================================
        ComplianceGuideItem(
            id = "rbi-digital-lending-2022",
            domain = RegulationDomain.RBI_REGULATIONS,
            sectionRef = "RBI/2022-23/111 DOR.CRE.REC.66/21.07.001/2022-23",
            title = "RBI Guidelines on Digital Lending: Zero Biometric/Contact Storage",
            executiveSummary = "Regulated Entities (REs) and Digital Lending Apps (DLAs) / Lending Service Providers (LSPs) are prohibited from accessing mobile phone resources such as contacts, call logs, media files, and biometrics.",
            detailedRequirements = listOf(
                "DLAs cannot access mobile phone resources like Contacts List, Call Logs, Media/Photos, or internal storage.",
                "One-time access to camera, microphone, and location is permitted ONLY for onboarding/e-KYC with explicit consent.",
                "All data stored in DLAs/LSPs must reside in servers physically located within India.",
                "Borrowers must be given clear option to give/deny consent, revoke consent, or restrict disclosure to third parties.",
                "Borrower must be given option to make the app delete their personal data (Right to be Forgotten)."
            ),
            technicalImplementationCode = """
// Prohibited Permission Validator for RBI Digital Lending Apps
class RbiDigitalLendingComplianceChecker {
    private val PROHIBITED_PERMISSIONS = listOf(
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.READ_CALL_LOG",
        "android.permission.WRITE_CALL_LOG",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.ACCESS_MEDIA_LOCATION"
    )

    fun auditManifestPermissions(declaredPermissions: List<String>): List<String> {
        return declaredPermissions.filter { perm -> perm in PROHIBITED_PERMISSIONS }.map { perm ->
            "RBI Digital Lending Violation: Declaring " + perm + " is strictly barred for DLAs."
        }
    }
}
            """.trimIndent(),
            statutoryPenalty = "Cancellation of NBFC license, blacklisting of Lending Service Provider (LSP), and regulatory fines under Section 45L of RBI Act 1934.",
            officialGazetteRef = "Reserve Bank of India Guidelines on Digital Lending (DOR.CRE.REC.66/21.07.001/2022-23)",
            officialUrl = "https://www.rbi.org.in/Scripts/NotificationUser.aspx?Id=12382&Mode=0",
            severity = ComplianceSeverity.CRITICAL,
            tags = listOf("RBI", "Digital Lending", "DLAs", "Contacts Ban", "Call Log Ban", "Data Privacy")
        ),
        ComplianceGuideItem(
            id = "rbi-data-localization-2018",
            domain = RegulationDomain.RBI_REGULATIONS,
            sectionRef = "RBI Directive DPSS.CO.OD No. 2785/06.08.005/2017-2018",
            title = "Storage of Payment System Data (Data Localization Mandate)",
            executiveSummary = "All payment system providers (Visa, Mastercard, UPI apps, Wallets, Payment Gateways) must store the entire payment data in systems located strictly inside India.",
            detailedRequirements = listOf(
                "Complete end-to-end payment transaction data must be stored exclusively in Indian cloud regions (e.g. AWS Mumbai, GCP Delhi, Azure Central India).",
                "Data includes customer data (Name, Mobile, Aadhaar, PAN), payment credentials, and transaction metadata.",
                "Cross-border processing is allowed for settlement, but transaction data must be deleted abroad within 24 hours and brought back to India for storage.",
                "System Audit Report (SAR) by CERT-In empaneled auditor must be submitted annually to RBI."
            ),
            technicalImplementationCode = """
// Cloud Endpoint Region Validator ensuring Indian Data Sovereignty
object PaymentDataLocalization {
    private val COMPLIANT_REGIONS = setOf(
        "ap-south-1", // AWS Mumbai
        "ap-south-2", // AWS Hyderabad
        "asia-south1", // GCP Mumbai
        "asia-south2", // GCP Delhi
        "centralindia", // Azure Pune
        "southindia" // Azure Chennai
    )

    fun assertStorageRegionCompliant(cloudRegion: String) {
        if (cloudRegion.lowercase() !in COMPLIANT_REGIONS) {
            throw IllegalStateException("RBI Directive Violation: Payment data must reside in Indian data centers. Region rejected: " + cloudRegion)
        }
    }
}
            """.trimIndent(),
            statutoryPenalty = "Ban on onboarding new domestic customers (as executed against major card networks) and severe operational penalties under PSS Act 2007.",
            officialGazetteRef = "RBI Directive on Storage of Payment System Data (DPSS.CO.OD No. 2785/06.08.005/2017-2018)",
            officialUrl = "https://www.rbi.org.in/scripts/FS_Notification.aspx?Id=11244&fn=9&Mode=0",
            severity = ComplianceSeverity.CRITICAL,
            tags = listOf("RBI", "Data Localization", "Payment Data", "PSS Act 2007", "Indian Servers", "Cloud Region")
        ),
        ComplianceGuideItem(
            id = "rbi-tokenization-coft",
            domain = RegulationDomain.RBI_REGULATIONS,
            sectionRef = "RBI Circular DPSS.CO.PD No. 1810/02.14.008/2019-20 / COFT Directives",
            title = "Card-on-File Tokenization (CoFT) & Storage Ban",
            executiveSummary = "Merchants and Payment Aggregators are strictly forbidden from storing actual 16-digit debit/credit card numbers and CVVs on their servers. Only device-specific Card-on-File Tokens (CoFT) are allowed.",
            detailedRequirements = listOf(
                "Actual 16-digit Card Number, Expiry Date, and CVV MUST NOT be saved after transaction completion.",
                "Only authorized Card Networks (Visa, Mastercard, RuPay) and Issuing Banks can tokenize card credentials.",
                "Merchants can only store Token Reference, Last 4 digits of card, and Card Issuer Name for transaction display.",
                "Customer consent is mandatory for tokenizing a card."
            ),
            technicalImplementationCode = """
// Tokenized Card Representation in Client Database
@Entity(tableName = "tokenized_payment_methods")
data class TokenizedCardEntity(
    @PrimaryKey val tokenId: String, // CoFT token issued by Card Network
    val cardLast4: String, // e.g. "4012"
    val cardNetwork: String, // e.g. "RuPay", "Visa"
    val issuerBank: String, // e.g. "HDFC Bank"
    val tokenExpiry: String,
    val isDefault: Boolean
    // NOTE: CVV and full 16-digit PAN are NEVER stored in this entity!
)
            """.trimIndent(),
            statutoryPenalty = "Monetary penalties up to ₹5 Lakh to ₹50 Lakh under Payment and Settlement Systems Act 2007.",
            officialGazetteRef = "RBI Directives on Tokenisation - Card Transactions",
            officialUrl = "https://www.rbi.org.in/scripts/FS_Notification.aspx?Id=11449&fn=9&Mode=0",
            severity = ComplianceSeverity.HIGH,
            tags = listOf("RBI", "CoFT", "Tokenization", "Card Storage Ban", "CVV Protection", "Payment Aggregator")
        ),
        ComplianceGuideItem(
            id = "rbi-penny-drop-verification",
            domain = RegulationDomain.RBI_REGULATIONS,
            sectionRef = "RBI Circular on Name Match Validation & Penny Drop Rules",
            title = "Bank Account Name-Match & Penny Drop Verification",
            executiveSummary = "Financial apps and lenders must perform real-time automated Penny Drop (₹1 / ₹0.01 credit verification) to validate that the applicant's name matches the beneficiary bank account.",
            detailedRequirements = listOf(
                "Verify beneficiary name from bank CBS response before executing loan disbursements or wallet payouts.",
                "Ensure minimum 80% fuzzy string matching score between KYC name and beneficiary bank name.",
                "Obtain user consent before initiating verification credit.",
                "Store penny drop verification transaction ID for statutory audit ledger."
            ),
            technicalImplementationCode = """
// Penny Drop Verification Result Handler
data class PennyDropResult(
    val beneficiaryNameFromBank: String,
    val applicantKycName: String,
    val matchPercentage: Double,
    val transactionRef: String,
    val isApproved: Boolean
)

fun evaluatePennyDropMatch(kycName: String, bankAccountName: String): Boolean {
    val similarity = StringSimilarity.calculateJaroWinkler(kycName.trim().lowercase(), bankAccountName.trim().lowercase())
    return similarity >= 0.80
}
            """.trimIndent(),
            statutoryPenalty = "Suspension of automated payout gateway and regulatory audit flags for KYC evasion.",
            officialGazetteRef = "RBI Regulatory Sandbox & Master Direction on KYC (Updated 2023)",
            officialUrl = "https://www.rbi.org.in/",
            severity = ComplianceSeverity.MEDIUM,
            tags = listOf("Penny Drop", "Bank Verification", "Name Match", "NPCI UPI", "KYC")
        ),

        // ==========================================
        // 4. CROSS-STATUTORY COMPLIANCE MATRIX
        // ==========================================
        ComplianceGuideItem(
            id = "cross-statutory-matrix",
            domain = RegulationDomain.CROSS_STATUTORY,
            sectionRef = "DPDP 2023 + UIDAI + RBI + CERT-In",
            title = "Cross-Regulatory Intersection Matrix for Indian Mobile Applications",
            executiveSummary = "Comprehensive multi-statute developer requirement matrix synthesizing DPDP Act 2023, UIDAI Aadhaar Regulations, RBI Master Directions, and CERT-In 6-hour cybersecurity reporting rules.",
            detailedRequirements = listOf(
                "Consent: DPDP Sec 6 unbundled consent applies across all apps; RBI adds specific prohibition on contact/media access.",
                "Storage: DPDP requires purpose limitation & erasure; UIDAI bans plaintext Aadhaar; RBI mandates India-only data localization for payment data.",
                "Breach Reporting: CERT-In mandates 6-hour cyber incident notification; DPDP Section 15 mandates reporting to DPBI and affected users.",
                "Audit Trail: Immutable logging of consent, erasure, and KYC verification is required across all 3 regulatory bodies."
            ),
            technicalImplementationCode = """
// Unified Statutory Compliance Audit Dispatcher
object MultiRegulatoryComplianceDispatcher {
    fun runUnifiedPreLaunchAudit(appConfig: AppComplianceProfile): UnifiedAuditScore {
        val dpdpPassed = appConfig.hasSection5Notice && appConfig.hasSection12Erasure && appConfig.hasNominee
        val uidaiPassed = appConfig.zeroAadhaarInDb && appConfig.usesMaskedAadhaarOnly
        val rbiPassed = appConfig.zeroContactsPermission && appConfig.serversInIndia && appConfig.usesCoftTokens
        val certInPassed = appConfig.has6HourIncidentPlaybook
        
        return UnifiedAuditScore(
            isFullyCompliant = dpdpPassed && uidaiPassed && rbiPassed && certInPassed,
            dpdpScore = if (dpdpPassed) 100 else 40,
            uidaiScore = if (uidaiPassed) 100 else 0,
            rbiScore = if (rbiPassed) 100 else 30,
            certInScore = if (certInPassed) 100 else 50
        )
    }
}
            """.trimIndent(),
            statutoryPenalty = "Combined cumulative penalties exceeding ₹250 Cr + criminal liability under Aadhaar Act & IT Act 70B.",
            officialGazetteRef = "DPDP Act 2023 | UIDAI Directives | RBI Master Directions | CERT-In Directions 2022",
            officialUrl = "https://www.cert-in.org.in/",
            severity = ComplianceSeverity.CRITICAL,
            tags = listOf("Cross-Regulatory", "DPDP", "UIDAI", "RBI", "CERT-In", "Master Checklist")
        )
    )
}
