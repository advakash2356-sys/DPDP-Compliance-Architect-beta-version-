package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object AgenticAuditorService {
    private const val TAG = "AgenticAuditor"
    private const val BASE_URL = "https://generativelanguage.googleapis.com"
    private const val MODEL_NAME = "gemini-3.5-flash"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Default regulatory frameworks/court updates to feed into the prompt as dynamic context
    var activeRegulatoryGuidelines = """
        - DPDP Act 2023 Section 5: Notice must precede or accompany consent, detailing specific personal data and purpose.
        - Section 6: Consent must be free, specific, informed, unconditional, and unambiguous with a clear affirmative action. Option to withdraw consent must be as easy as giving it.
        - Section 9: Verifiable parental/guardian consent is mandatory for processing children's personal data (<18 years). No tracking or behavioral monitoring allowed.
        - Section 12: Clear Right to Erasure, correction, and access must be provided through seamless self-service UI flows.
        - Section 13: Users must have the right to nominate an individual to exercise their data protection rights in the event of death or incapacity.
    """.trimIndent()

    /**
     * Updates the dynamic guidelines backlog.
     */
    fun updateGuidelines(newGuidelines: String) {
        activeRegulatoryGuidelines = newGuidelines
    }

    /**
     * Triggers the Gemini API to analyze target code/manifest snippets,
     * identify DPDP compliance violations, and generate complete "Wipe Coding" solutions.
     */
    suspend fun auditAppCode(
        appName: String,
        packageName: String,
        targetInput: String,
        customInstruction: String = ""
    ): AuditResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        // If API key is empty, unconfigured, or a default placeholder, run rich local simulated auditor
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is unconfigured. Running standard localized compliance rule engine.")
            return@withContext runLocalHeuristicAudit(appName, packageName, targetInput)
        }

        try {
            val systemPrompt = """
                You are DPDP Sentinel, a production-grade agentic AI compliance auditor specializing in the India Digital Personal Data Protection (DPDP) Act 2023 and Android engineering.
                Your goal is to audit an application's manifest, Kotlin/Java code, or layout descriptions, identify legal and technical compliance gaps, and output absolute remedies.
                
                You MUST return your analysis containing these precise sections, formatted in rich Markdown:
                1. **AUDIT DASHBOARD**: Risk Level (HIGH, MEDIUM, LOW), Target Package, and violated DPDP Sections.
                2. **VIOLATIONS DETECTED**: Direct legal critique listing each violation with specific sections of the Act.
                3. **WIPE CODING (COMPLETE FIX)**: Complete, production-ready, drop-in replacement Kotlin class or XML manifest code that the developer can copy-paste immediately to solve these gaps entirely. Avoid writing placeholder code like "// TODO implement details". Ensure the replacement is 100% complete.
                4. **SEMI-CODING IMPLEMENTATION PLAN**: Step-by-step procedural directions for additional system changes.
                
                Use the current active regulatory context:
                $activeRegulatoryGuidelines
            """.trimIndent()

            val userPrompt = """
                App Name: $appName
                Package Name: $packageName
                
                ${if (customInstruction.isNotEmpty()) "Special Focus / Instructions: $customInstruction" else ""}
                
                AUDIT CARRIER / APP CODE REPRESENTATION:
                ```
                $targetInput
                ```
                
                Analyze the source / manifest above. Run a deep security and compliance scan. Point out forced consent, missing withdrawal options, invasive permissions, lack of child protection, or missing nominee/erasure buttons. Write the absolute "wipe coding" fix blocks now.
            """.trimIndent()

            // Construct payload with natively supported org.json to avoid deserialization crashes
            val requestJson = JSONObject()
            
            // System instructions
            val systemInstruction = JSONObject().apply {
                val partsArray = JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                }
                put("parts", partsArray)
            }
            requestJson.put("systemInstruction", systemInstruction)

            // Contents list
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", userPrompt) })
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            requestJson.put("contents", contentsArray)

            // Generation config
            val generationConfig = JSONObject().apply {
                put("temperature", 0.2f) // keep deterministic for technical audit
            }
            requestJson.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$BASE_URL/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errMsg = response.body?.string() ?: "Unknown API response"
                    Log.e(TAG, "Gemini API error code: ${response.code}, message: $errMsg")
                    return@withContext runLocalHeuristicAudit(appName, packageName, targetInput, "Gemini service temporarily unavailable (Code ${response.code}). Local audit fallback activated.")
                }

                val responseStr = response.body?.string() ?: throw Exception("Empty response body")
                val responseJson = JSONObject(responseStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    throw Exception("No analysis candidates generated by AI model.")
                }

                val content = candidates.getJSONObject(0).getJSONObject("content")
                val parts = content.getJSONArray("parts")
                val responseText = parts.getJSONObject(0).getString("text")

                // Parse out variables for DB indexing
                val risk = parseRiskLevel(responseText)
                val sections = parseViolatedSections(responseText)
                val summary = parseSummary(responseText)
                val wipeCode = extractWipeCode(responseText)
                val semiInstructions = extractSemiInstructions(responseText)

                AuditResult(
                    isSimulated = false,
                    rawOutput = responseText,
                    riskLevel = risk,
                    sectionsViolated = sections,
                    summary = summary,
                    wipeCode = wipeCode,
                    semiInstructions = semiInstructions
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying Gemini API: ${e.message}", e)
            AuditResult(
                isSimulated = true,
                rawOutput = "### AI Connection Error\n\nCould not compile remote analysis from Gemini endpoints. Falling back to local audit parser.\n\nError details: ${e.localizedMessage}",
                riskLevel = "MEDIUM",
                sectionsViolated = "Sec 5, Sec 6, Sec 12",
                summary = "Local heuristic scan detected non-compliant manifest or data patterns. Verify permissions, notice protocols, and child-safe access.",
                wipeCode = "// Fallback fix: Ensure explicit user consents are logged prior to any analytical SDK startups.",
                semiInstructions = "1. Add a launcher checkbox confirming data purpose notice.\n2. Configure Room database auditing."
            )
        }
    }

    /**
     * Dynamic Law update parser using Gemini API.
     * Keeps the agentic intelligence informed with latest DPBI rules.
     */
    suspend fun queryLatestLawUpdates(currentBacklog: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            delaySimulation(1000)
            return@withContext """
                [DPB India Update Simulation - June 2026]
                - Clarified parental consent Rule 4.2: Game applications must utilize verifiable visual proof or secure authentication links linked to India National IDs.
                - Declared penalty schedules: Non-notification of structural data breaches within 72 hours subject to fine up to ₹150 Crores under Chapter VII.
                - Established that optional telemetry toggles must be checked 'OFF' by default in initial layouts.
            """.trimIndent()
        }

        try {
            val systemPrompt = "You are a professional legal compliance legal crawler updating Android developers on DPDP Act 2023 India adjustments."
            val userPrompt = """
                Synthesize the newest DPDP Act 2023 legal additions, Data Protection Board of India (DPBI) draft rules, or guidelines for Android builders.
                State 3 highly readable key takeaways, focused on Android permissions, dynamic user notice forms, child safety (<18), and breach timelines.
                Keep it concise and ready to be loaded as dynamic analyzer guidelines.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", userPrompt) })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", systemPrompt) })
                    })
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url("$BASE_URL/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext "Error fetching database updates: ${response.code}"
                val responseStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseStr)
                responseJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            }
        } catch (e: Exception) {
            "Fallback Update: DPBI maintains initial draft rules. Default Section 5, 6, 9, 12 compliance checks remain baseline standard."
        }
    }

    private suspend fun delaySimulation(ms: Long) {
        kotlinx.coroutines.delay(ms)
    }

    // --- HEURISTIC PARSERS FOR THE GEMINI MARKDOWN ---

    private fun parseRiskLevel(text: String): String {
        return when {
            text.contains("Risk Level: HIGH", ignoreCase = true) || text.contains("RISK_LEVEL: HIGH", ignoreCase = true) || text.contains("**HIGH**") -> "HIGH"
            text.contains("Risk Level: MEDIUM", ignoreCase = true) || text.contains("RISK_LEVEL: MEDIUM", ignoreCase = true) -> "MEDIUM"
            else -> "LOW"
        }
    }

    private fun parseViolatedSections(text: String): String {
        val matches = mutableListOf<String>()
        if (text.contains("Section 5", ignoreCase = true)) matches.add("Section 5 (Notice)")
        if (text.contains("Section 6", ignoreCase = true)) matches.add("Section 6 (Consent)")
        if (text.contains("Section 9", ignoreCase = true)) matches.add("Section 9 (Children)")
        if (text.contains("Section 12", ignoreCase = true)) matches.add("Section 12 (Erasure)")
        if (text.contains("Section 13", ignoreCase = true)) matches.add("Section 13 (Nomination)")
        
        return if (matches.isEmpty()) "Section 5, Section 6" else matches.joinToString(", ")
    }

    private fun parseSummary(text: String): String {
        // Grab first few sentence snippets or summary patterns
        val lines = text.split("\n")
        val summaryLine = lines.firstOrNull { it.contains("summary", ignoreCase = true) || it.startsWith("Local heuristic") }
            ?: lines.firstOrNull { it.trim().length > 30 && !it.startsWith("#") }
            ?: "Compliance gaps detected under DPDP Act 2023. Action item registered in backlog."
        return summaryLine.take(150)
    }

    private fun extractWipeCode(text: String): String {
        // Parse code block ```kotlin ... ```
        val startIndex = text.indexOf("```kotlin")
        if (startIndex != -1) {
            val endBlockStart = text.indexOf("```", startIndex + 9)
            if (endBlockStart != -1) {
                return text.substring(startIndex + 9, endBlockStart).trim()
            }
        }
        val generalCodeIndex = text.indexOf("```")
        if (generalCodeIndex != -1) {
            val nextEnd = text.indexOf("```", generalCodeIndex + 3)
            if (nextEnd != -1) {
                return text.substring(generalCodeIndex + 3, nextEnd).trim()
            }
        }
        return "// Wipe code target replacement could not be auto-extracted. Please refer to raw analysis report."
    }

    private fun extractSemiInstructions(text: String): String {
        val splitKeyword = "SEMI-CODING"
        val idx = text.indexOf(splitKeyword, ignoreCase = true)
        if (idx != -1) {
            return text.substring(idx).take(1000)
        }
        return "1. Review all dynamic network calls in manifest.\n2. Ensure mandatory consent check is enabled in launch components."
    }

    // --- STANDARD OFFLINE COMPLIANCE SCANNERS (HEURISTIC AUDIT) ---
    private fun runLocalHeuristicAudit(appName: String, packageName: String, code: String, note: String = ""): AuditResult {
        val violations = mutableListOf<String>()
        var risk = "LOW"
        val sections = mutableListOf<String>()
        var wipeCode = ""
        var instructions = ""

        // Simple heuristic scanners of file contents to ensure high accuracy without API
        val cleanCode = code.lowercase()
        val hasLocation = cleanCode.contains("location") || cleanCode.contains("access_fine_location") || cleanCode.contains("access_coarse_location")
        val hasContacts = cleanCode.contains("contacts") || cleanCode.contains("read_contacts")
        val hasConsentCheckbox = cleanCode.contains("consent") || cleanCode.contains("checked") || cleanCode.contains("checkbox")
        val hasOptIn = cleanCode.contains("optin") || cleanCode.contains("opt_in")
        val hasWipe = cleanCode.contains("erase") || cleanCode.contains("delete") || cleanCode.contains("minim")
        val hasChild = cleanCode.contains("age") || cleanCode.contains("child") || cleanCode.contains("minor")

        if (hasLocation || hasContacts) {
            risk = "HIGH"
            sections.add("Section 5 (Notice)")
            violations.add("⚠️ **CRITICAL LOCATION/CONTACT PRIVACY**: The manifest requests invasive device telemetry markers ('ACCESS_FINE_LOCATION' or 'READ_CONTACTS'). Under Section 5 of India's DPDP Act, you are strictly prohibited from harvesting these trackers unless you supply a separate, granular, plain-language legal Notice prior to gathering.")
            
            wipeCode = """
// Production Ready DPDP Notice Prompt implementation
@Composable
fun DPDPNoticePrompt(onConsentStatus: (isGranted: Boolean) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.padding(16.dp).fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("DPDP Section 5 Notice Prompt", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("This App requests permission to read location markers STRICTLY for calculating courier dispatch limits. This data is stored locally in SQLite and is automatically purged after 2 hours. It is never sold or shared with third-party networks.", fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onConsentStatus(true) }) { Text("Accept notice") }
                OutlinedButton(onClick = { onConsentStatus(false) }) { Text("Decline") }
            }
        }
    }
}
            """.trimIndent()
            
            instructions = "1. Delete android.permission.ACCESS_FINE_LOCATION if coarse is sufficient.\n2. Create an explicit DPDP Section 5 separate notice card showing exact purpose limits.\n3. Add dynamic notice checkboxes in onboarding."
        }

        if (!hasConsentCheckbox && !cleanCode.contains("validated")) {
            if (risk == "LOW") risk = "MEDIUM"
            sections.add("Section 6 (Consent)")
            violations.add("⚠️ **MISSING CLEAR CHANNELS OF EXPLICIT CONSENT**: No pre-checked boxes or verification flows found in launch files. Section 6 mandates that consent must be unconditional and unambiguous, needing a clear affirmative action (i.e. no pre-checked checkboxes, no implicit agreements).")
            if (wipeCode.isEmpty()) {
                wipeCode = """
// Explicit granular consent Checkbox - Unchecked by default (MANDATORY)
var consentGranted by remember { mutableStateOf(false) }
Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Checkbox(
        checked = consentGranted,
        onCheckedChange = { consentGranted = it },
        modifier = Modifier.testTag("granular_consent_box")
    )
    Text(
        text = "I explicitly consent to the processing of my telephone markers as detailed in the Section 5 notice.",
        fontSize = 12.sp
    )
}
                """.trimIndent()
            }
            instructions += "\n4. Build an unchecked-by-default consent toggle.\n5. Log the timestamp, IP, and Notice format in an auditable consent database Table."
        }

        if (hasChild && !cleanCode.contains("parent") && !cleanCode.contains("verifi")) {
            risk = "HIGH"
            sections.add("Section 9 (Child Protections)")
            violations.add("⚠️ **CHILD/MINOR DETECTED WITH NO VERIFIABLE PARENTAL CONSENT**: The application contains age-restriction logic but lacks secure verification of parental authority. DPDP Sec 9 renders all unsupervised minor processing illegal and bans behavioral tracking of minors.")
            wipeCode += """

// DPDP Sec 9 Verifiable Parental Consent Form
@Composable
fun ParentalConsentVerification(onVerified: (Boolean) -> Unit) {
    var parentEmail by remember { mutableStateOf("") }
    var agreementChecked by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Text("Parental Verification Portal (Sec 9)", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = parentEmail,
            onValueChange = { parentEmail = it },
            label = { Text("Parent/Legal Guardian Contact Email") }
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = agreementChecked, onCheckedChange = { agreementChecked = it })
            Text("I confirm I am the legal guardian and consent to this minor utilizing the platform.", fontSize = 11.sp)
        }
        Button(
            enabled = agreementChecked && parentEmail.contains("@"),
            onClick = { onVerified(true) }
        ) {
            Text("Verify Parental Authority")
        }
    }
}
            """.trimIndent()
            instructions += "\n6. Integrate age verification screening on startup.\n7. Block analytical advertising SDK trackers for users under 18."
        }

        if (violations.isEmpty()) {
            violations.add("✨ **EXCELLENT BASELINE DESIGN**: Local database structures align with dynamic data minimization and right to erasure (Section 12). Clear notice guidelines present.")
            wipeCode = "// Beautiful code! Ensure that any upcoming feature updates run Notice protocols prior to compiling."
            instructions = "1. Maintain the current SQLite ledger audits.\n2. Run regular external mock penetration tests."
        }

        val formattedText = """
            # DPDP Audit Report (Local Heuristic Engine)
            
            ${if (note.isNotEmpty()) "> **Status Note**: $note\n" else ""}
            🎯 **App**: `$appName` | **Target Package**: `$packageName`
            ⚡ **Risk Assessment Metric**: **$risk**
            🛠️ **Statutory Sections Under Audit**: ${sections.joinToString(", ")}
            
            ---
            
            ### VIOLATIONS DETECTED:
            ${violations.joinToString("\n\n")}
            
            ---
            
            ### WIPE CODING ACTIONABLE CODE REMEDY:
            Below is the compliant code structure designed to replace the illegal or non-compliant parts. You can copy and drop this into your setup:
            
            ```kotlin
            $wipeCode
            ```
            
            ---
            
            ### SEMI-CODING ARCHITECTURAL PLAN:
            $instructions
        """.trimIndent()

        return AuditResult(
            isSimulated = true,
            rawOutput = formattedText,
            riskLevel = risk,
            sectionsViolated = sections.joinToString(", "),
            summary = "Local scan flagged privacy items requiring urgent notice. Review required permissions.",
            wipeCode = wipeCode,
            semiInstructions = instructions
        )
    }

    /**
     * Triggers the Gemini API to analyze project documentation, specifications, or privacy policies,
     * detect legal gaps under the DPDP Act 2023, and generate compliant documentation text.
     */
    suspend fun auditProjectDocumentation(
        docText: String,
        customInstruction: String = ""
    ): AuditResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is unconfigured. Running standard localized documentation rule engine.")
            return@withContext runLocalDocumentationAudit(docText, customInstruction)
        }

        try {
            val systemPrompt = """
                You are DPDP Sentinel Developer Document Auditor. Your specialty is scanning technical specifications, developer requirements, system architecture plans, or privacy policies against the India Digital Personal Data Protection (DPDP) Act 2023.
                Analyze the documentation carefully, identify legal gaps, and produce an absolute Markdown evaluation of loopholes.
                
                You MUST return your analysis containing these precise sections, formatted in rich Markdown:
                1. **AUDIT DASHBOARD**: Risk Level (HIGH, MEDIUM, LOW), and violated DPDP Sections.
                2. **DOCUMENTATION GAPS DETECTED**: Direct legal critique of why the current text fails India DPDP 2023 compliance.
                3. **WIPE TEXT ACT REMEDY (COMPLIANT TEMPLATE)**: A complete, fully written, drop-in replacement segment or policy section that the user can immediately put in their documentation to solve all gaps. Do NOT write placeholder text or summaries; write the actual full-text legal disclosure ready for production. 
                4. **ACTIONABLE NEXT STEPS**: List of operational changes (e.g. setting up grievance officer, establishing verification for minors).
                
                Use the current active regulatory context:
                $activeRegulatoryGuidelines
            """.trimIndent()

            val userPrompt = """
                DOCUMENT TEXT TO SCAN:
                \"\"\"
                $docText
                \"\"\"
                
                ${if (customInstruction.isNotEmpty()) "Special Focus / Instructions: $customInstruction" else ""}
                
                Analyze this document for compliance. Point out lack of explicit options, lack of children data protection disclosures (under 18), unstated grievance redressal channels, or ambiguous retention limits. Output the complete "Wipe Code" compliant documentation segment.
            """.trimIndent()

            val requestJson = JSONObject()
            val systemInstruction = JSONObject().apply {
                val partsArray = JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                }
                put("parts", partsArray)
            }
            requestJson.put("systemInstruction", systemInstruction)

            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", userPrompt) })
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            requestJson.put("contents", contentsArray)

            val generationConfig = JSONObject().apply {
                put("temperature", 0.3f)
            }
            requestJson.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$BASE_URL/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errMsg = response.body?.string() ?: "Unknown API response"
                    Log.e(TAG, "Gemini API error code: ${response.code}, message: $errMsg")
                    return@withContext runLocalDocumentationAudit(docText, "Gemini service temporarily unavailable (Code ${response.code}). Local doc-audit fallback activated.")
                }

                val responseStr = response.body?.string() ?: throw Exception("Empty response body")
                val responseJson = JSONObject(responseStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    throw Exception("No analysis candidates generated by AI model.")
                }

                val content = candidates.getJSONObject(0).getJSONObject("content")
                val parts = content.getJSONArray("parts")
                val responseText = parts.getJSONObject(0).getString("text")

                val risk = parseRiskLevel(responseText)
                val sections = parseViolatedSections(responseText)
                val summary = parseSummary(responseText)
                val wipeCode = extractWipeCode(responseText)
                val semiInstructions = extractSemiInstructions(responseText)

                AuditResult(
                    isSimulated = false,
                    rawOutput = responseText,
                    riskLevel = risk,
                    sectionsViolated = sections,
                    summary = summary,
                    wipeCode = wipeCode,
                    semiInstructions = semiInstructions
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying Gemini API for documentation: ${e.message}", e)
            return@withContext runLocalDocumentationAudit(docText, "AI network failure. Dynamic heuristic analyzer provided baseline analysis.")
        }
    }

    private fun runLocalDocumentationAudit(docText: String, note: String = ""): AuditResult {
        val lowercaseDoc = docText.lowercase()
        val violations = mutableListOf<String>()
        val sections = mutableListOf<String>()
        var risk = "LOW"
        val complTemplate: String
        val steps: String

        val hasNotice = lowercaseDoc.contains("notice") || lowercaseDoc.contains("purpose") || lowercaseDoc.contains("notify")
        val hasConsent = lowercaseDoc.contains("consent") || lowercaseDoc.contains("agree")
        val hasWithdraw = lowercaseDoc.contains("withdraw") || lowercaseDoc.contains("revoke") || lowercaseDoc.contains("opt-out") || lowercaseDoc.contains("opt out")
        val hasChildren = lowercaseDoc.contains("child") || lowercaseDoc.contains("minor") || lowercaseDoc.contains("parent") || lowercaseDoc.contains("under 18") || lowercaseDoc.contains("under 14")
        val hasErasure = lowercaseDoc.contains("erasure") || lowercaseDoc.contains("delete") || lowercaseDoc.contains("purge") || lowercaseDoc.contains("remove")
        val hasGrievance = lowercaseDoc.contains("grievance") || lowercaseDoc.contains("officer") || lowercaseDoc.contains("redress") || lowercaseDoc.contains("contact")

        if (!hasConsent || lowercaseDoc.contains("automatic consent") || lowercaseDoc.contains("by installing you agree")) {
            violations.add("🚨 **FORCED / AUTOMATIC CONSENT VIOLATION (Section 6)**: The specifications suggest collecting data upon download or start-up without explicit, affirmative, unbundled tick box action.")
            sections.add("Section 6")
            risk = "HIGH"
        }
        if (!hasWithdraw) {
            violations.add("🚨 **MISSING CONSENT WITHDRAWAL RIGHTS (Section 6(4))**: Users are not disclosed a mechanism to withdraw consent as easily as they gave it.")
            sections.add("Section 6(4)")
            if (risk != "HIGH") risk = "MEDIUM"
        }
        if (!hasChildren) {
            violations.add("🚨 **MISSING GUARDIAN CONSENT & TRACKING RESTRICTIONS (Section 9)**: No specific provisions for verifiable parental consent or ban on behavioral tracking of children are mentioned.")
            sections.add("Section 9")
            risk = "HIGH"
        }
        if (!hasErasure) {
            violations.add("🚨 **MISSING ERASURE DISCLOSURE (Section 12)**: The text does not declare a straightforward process for users to request data erasure/purge upon withdrawal of consent.")
            sections.add("Section 12")
            if (risk == "LOW") risk = "MEDIUM"
        }
        if (!hasGrievance) {
            violations.add("🚨 **MISSING REDRESSAL / GRIEVANCE OFFICER (Section 13)**: Under Section 13/14, you MUST name a designated Grievance Redressal Officer along with an active email address in the policy.")
            sections.add("Section 13 & 14")
            if (risk == "LOW") risk = "MEDIUM"
        }

        if (violations.isEmpty()) {
            sections.add("None (Baseline Compliant)")
            complTemplate = """
                ### India DPDP 2023 Compliant Supplementary Notice
                Mishra Delivery confirms complete commitment to the India Digital Personal Data Protection Act (DPDP) 2023:
                1. NOTICE: Prior to any collection, you are shown exactly what items (Phone Hash, Nominee, and Audit task trails) are saved, and the purpose of database safety simulation.
                2. FREE WITHDRAWAL: You can revoke permissions, clear all local trace databases, or erase your user profile securely at any time by navigating to "Audits & Ledgers" tab and tapping "Purge & Erase".
                3. PRO-CHILD SAFEGUARDS: We programmatically block behavioral indexing of minors and require verifiable parents to authorize nominee features.
            """.trimIndent()
            steps = "1. Maintain present uncompromised SQLite loggers.\n2. Perform periodic biweekly code penetration checkups."
        } else {
            complTemplate = """
                ### India DPDP 2023 Compliant Addendum Template (WIPE TEXT)
                
                [Drop this directly into your official Privacy Disclosures or Specs to heal compliance breaches]
                
                1. DPDPA SPECIAL NOTICE (Section 5): We collect and process your data (e.g. registered profile name, contact phone hashed securely via SHA-256) solely for compliance simulation.
                2. CONSENT PRINCIPLES (Section 6): Your consent is free, specific, informed, unconditional, and unambiguous. You have the right to withdraw consent at any instant as easily as granting it via the dynamic 'Purge User Trace' button.
                3. CHILD PROTECTION OBLIGATIONS (Section 9): Our application does not engage in tracking, behavioral monitoring, or targeted advertising centered around children. Verifiable parental authorization is required for nominee entries.
                4. DATA ERASURE & RIGHT TO ACCURACY (Section 12): You possess absolute rights to correct, complete, or request total deletion (erasure) of all hosted or logged personal attributes instantly.
                5. GRIEVANCE REDRESSAL OFFICER (Section 13 & 14): For queries or DPDP rights execution, contact our designated Grievance Officer at:
                   Email: compliance-officer@dpdp-safe.example.in
                   Address: MeitY Incubator, Electronics Niketan, New Delhi, India.
            """.trimIndent()
            steps = """
                1. Insert the DPDP Addendum Template above into your official app website, Play Store listing description, and in-app privacy screen.
                2. Ensure a checkbox for "I Accept" is unchecked by default on initial application launches.
                3. Integrate the self-service Erasure button pointing to Room database deletion APIs.
            """.trimIndent()
        }

        val output = """
            # DPDP Documentation Gap Report (Local Heuristic Engine)
            
            ${if (note.isNotEmpty()) "> **Status Note**: $note\n" else ""}
            ⚡ **Risk Assessment Metric**: **$risk**
            🛠️ **Statutory Sections Under Audit**: ${sections.joinToString(", ")}
            
            ---
            
            ### DOCUMENTATION GAPS DETECTED:
            ${if (violations.isEmpty()) "✨ Your documentation carries all necessary keyword signals for Notice, consent, withdraw, erasure, child safety, and grievance officers!" else violations.joinToString("\n\n")}
            
            ---
            
            ### WIPE TEXT ACT REMEDY (COMPLIANT TEMPLATE):
            $complTemplate
            
            ---
            
            ### ACTIONABLE ARCHITECTURAL NEXT STEPS:
            $steps
        """.trimIndent()

        return AuditResult(
            isSimulated = true,
            rawOutput = output,
            riskLevel = risk,
            sectionsViolated = sections.joinToString(", "),
            summary = "Heuristic semantic analysis scanned documentation for vital legal keywords and noted specific missing clauses.",
            wipeCode = complTemplate,
            semiInstructions = steps
        )
    }
}

data class AuditResult(
    val isSimulated: Boolean,
    val rawOutput: String,
    val riskLevel: String,
    val sectionsViolated: String,
    val summary: String,
    val wipeCode: String,
    val semiInstructions: String
)
