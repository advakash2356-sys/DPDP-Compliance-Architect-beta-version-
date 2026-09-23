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

object ComplianceGuideService {
    private const val TAG = "ComplianceGuideService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com"
    private const val MODEL_NAME = "gemini-3.5-flash"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Executes an AI-powered search with Google Search Grounding to fetch the latest
     * official Gazette notifications, circulars, and judicial interpretations for DPDP 2023,
     * UIDAI (Aadhaar), and RBI regulations.
     */
    suspend fun queryGroundedLegislativeUpdates(
        query: String,
        domain: RegulationDomain
    ): GroundedGuideResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is unconfigured. Returning curated statutory offline intelligence with live Gazette citations.")
            return@withContext generateCuratedFallback(query, domain)
        }

        try {
            val systemPrompt = """
                You are Data Rakshak's Statutory Regulatory Legal Sentinel and Engineering Counsel.
                You specialize in India's regulatory frameworks:
                1. Digital Personal Data Protection (DPDP) Act 2023 & MeitY notifications.
                2. UIDAI Aadhaar Regulations (Masked Aadhaar, Aadhaar Data Vault, offline e-KYC, zero-plaintext storage).
                3. RBI Master Directions (Digital Lending DLAs/LSPs guidelines, Card-on-File Tokenization CoFT, 2018 Payment Data Localization, Penny-Drop verification).
                4. CERT-In 6-hour cybersecurity breach reporting directives.

                Provide accurate, up-to-date, grounded legislative information.
                Structure your answer into:
                - **Statutory Authority & Official Citation**
                - **Key Statutory Requirements for Developers & Fiduciaries**
                - **Technical Implementation & Architecture Rules**
                - **Penalties & Enforcement Provisions**
                
                Always cite official Gazettes, Circular numbers, or Section clauses.
            """.trimIndent()

            val domainContext = when (domain) {
                RegulationDomain.ALL -> "Focus on DPDP Act 2023, UIDAI Aadhaar rules, and RBI Master Directions."
                RegulationDomain.DPDP_ACT_2023 -> "Focus specifically on the Digital Personal Data Protection (DPDP) Act 2023 and MeitY rules."
                RegulationDomain.AADHAAR_UIDAI -> "Focus on UIDAI Aadhaar Regulations, Masked Aadhaar, and Aadhaar Act Section 29."
                RegulationDomain.RBI_REGULATIONS -> "Focus on Reserve Bank of India (RBI) Master Directions, Digital Lending, and Payment System Data."
                RegulationDomain.CROSS_STATUTORY -> "Focus on cross-regulatory alignment between DPDP Act 2023, UIDAI, and RBI."
            }

            val userPrompt = """
                Statutory Domain: $domainContext
                User Legislative Query: "$query"

                Please search Google for the latest official Indian Government Gazette notifications, circulars, MeitY / UIDAI / RBI announcements, and statutory directives regarding this query. Provide an authoritative, grounded analysis for developers and legal auditors.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                // Contents
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", userPrompt) })
                        })
                    })
                })

                // Google Search Grounding Tool
                put("tools", JSONArray().apply {
                    put(JSONObject().apply {
                        put("googleSearch", JSONObject())
                    })
                })

                // System Instruction
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", systemPrompt) })
                    })
                })

                // Generation Config
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2f)
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$BASE_URL/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string() ?: ""
                    Log.e(TAG, "Gemini Grounding API returned error code ${response.code}: $errorBody")
                    return@withContext generateCuratedFallback(query, domain, "Remote search error (Code ${response.code}). Curated legal intelligence provided.")
                }

                val responseStr = response.body?.string() ?: throw Exception("Empty response body from Gemini API")
                val responseJson = JSONObject(responseStr)
                val candidates = responseJson.optJSONArray("candidates")

                if (candidates == null || candidates.length() == 0) {
                    throw Exception("No candidates returned from Gemini API")
                }

                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val answerText = parts?.optJSONObject(0)?.optString("text") ?: "No synthesized text available."

                // Parse Grounding Metadata
                val sourcesList = mutableListOf<GroundingSource>()
                val groundingMetadata = candidate.optJSONObject("groundingMetadata")
                if (groundingMetadata != null) {
                    // Extract web search queries
                    val searchQueries = groundingMetadata.optJSONArray("webSearchQueries")
                    val querySnippet = if (searchQueries != null && searchQueries.length() > 0) {
                        "Searched: " + (0 until searchQueries.length()).map { searchQueries.getString(it) }.joinToString(" | ")
                    } else ""

                    // Extract grounding chunks
                    val groundingChunks = groundingMetadata.optJSONArray("groundingChunks")
                    if (groundingChunks != null) {
                        for (i in 0 until groundingChunks.length()) {
                            val chunk = groundingChunks.optJSONObject(i)
                            val web = chunk?.optJSONObject("web")
                            if (web != null) {
                                val uri = web.optString("uri", "")
                                val title = web.optString("title", "Official Gazette / Government Source")
                                if (uri.isNotEmpty()) {
                                    sourcesList.add(GroundingSource(title = title, uri = uri, snippet = querySnippet))
                                }
                            }
                        }
                    }
                }

                // If no grounding sources extracted, provide official regulatory portals
                if (sourcesList.isEmpty()) {
                    sourcesList.addAll(getDefaultOfficialSources(domain))
                }

                GroundedGuideResult(
                    query = query,
                    synthesizedText = answerText,
                    domain = domain,
                    sources = sourcesList.distinctBy { it.uri },
                    timestamp = System.currentTimeMillis(),
                    isSimulatedOrFallback = false
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Grounded Legislative Search: ${e.message}", e)
            return@withContext generateCuratedFallback(
                query,
                domain,
                "Live network query failed: ${e.localizedMessage}. Using authoritative local legal knowledge base."
            )
        }
    }

    private fun getDefaultOfficialSources(domain: RegulationDomain): List<GroundingSource> {
        return when (domain) {
            RegulationDomain.DPDP_ACT_2023 -> listOf(
                GroundingSource("Ministry of Electronics and Information Technology (MeitY)", "https://www.meity.gov.in/content/digital-personal-data-protection-act-2023"),
                GroundingSource("The Gazette of India - DPDP Act 2023 (No. 22 of 2023)", "https://egazette.gov.in/"),
                GroundingSource("Data Protection Board of India Advisory Portal", "https://www.dpbi.gov.in/")
            )
            RegulationDomain.AADHAAR_UIDAI -> listOf(
                GroundingSource("UIDAI Aadhaar Data Vault & Masked Aadhaar Guidelines", "https://uidai.gov.in/en/ecosystem/authentication-ecosystem/aadhaar-data-vault.html"),
                GroundingSource("UIDAI Circular on Masked Aadhaar & e-KYC", "https://uidai.gov.in/"),
                GroundingSource("Aadhaar Act 2016 & Amendments", "https://www.indiacode.nic.in/handle/123456789/2112")
            )
            RegulationDomain.RBI_REGULATIONS -> listOf(
                GroundingSource("Reserve Bank of India - Guidelines on Digital Lending", "https://www.rbi.org.in/Scripts/NotificationUser.aspx?Id=12382&Mode=0"),
                GroundingSource("RBI Directive on Storage of Payment System Data (Data Localization)", "https://www.rbi.org.in/scripts/FS_Notification.aspx?Id=11244&fn=9&Mode=0"),
                GroundingSource("RBI Directives on Card-on-File Tokenisation (CoFT)", "https://www.rbi.org.in/scripts/FS_Notification.aspx?Id=11449&fn=9&Mode=0")
            )
            else -> listOf(
                GroundingSource("MeitY DPDP Act 2023 Central Portal", "https://www.meity.gov.in/"),
                GroundingSource("UIDAI Official Regulations Repository", "https://uidai.gov.in/"),
                GroundingSource("RBI Master Directions Database", "https://www.rbi.org.in/"),
                GroundingSource("CERT-In Cyber Security Directions (Sec 70B)", "https://www.cert-in.org.in/")
            )
        }
    }

    private fun generateCuratedFallback(query: String, domain: RegulationDomain, statusNote: String = ""): GroundedGuideResult {
        val qLower = query.lowercase()
        val synthesizedText = when {
            qLower.contains("aadhaar") || qLower.contains("uidai") || qLower.contains("mask") || qLower.contains("vault") -> """
### UIDAI Aadhaar Regulations & Masked Aadhaar Mandate

**Official Statutory Authority**: Unique Identification Authority of India (UIDAI) Circular F. No. 13012/171/2017-UIDAI & Aadhaar Act 2016 Section 29.

#### 1. Core Statutory Mandates
- **Prohibition of Plaintext Storage**: Saving raw 12-digit Aadhaar numbers in databases, Room entities, flat files, or debug logs is strictly prohibited for non-AUA entities.
- **Masked Aadhaar Protocol**: Only the last 4 digits of Aadhaar may be stored or displayed (e.g., `XXXXXXXX3849`). The first 8 digits must be redacted with 'X'.
- **Aadhaar Data Vault (ADV)**: Licensed AUAs/KUAs that are legally permitted to store Aadhaar must isolate numbers in a dedicated, FIPS 140-2 Level 3 HSM-encrypted database using Reference Keys (GUIDs).
- **Offline Paperless e-KYC**: Applications must use password-protected Offline XML or QR Code verification and purge the raw file immediately after identity extraction.

#### 2. Android Developer Implementation
```kotlin
// UIDAI Compliance: In-Memory Masking & Sanitization
fun sanitizeAadhaar(raw: String): String {
    val clean = raw.replace(" ", "").replace("-", "")
    return if (clean.length == 12) "XXXXXXXX" + clean.takeLast(4) else "XXXXXXXXXXXX"
}
```

#### 3. Statutory Penalties
- Up to 3 years imprisonment and fines up to ₹1,00,000 per day under Section 38–42 of Aadhaar Act 2016.
- Immediate cancellation of AUA/KUA licenses.
            """.trimIndent()

            qLower.contains("rbi") || qLower.contains("lending") || qLower.contains("localization") || qLower.contains("token") || qLower.contains("penny") -> """
### RBI Master Directions: Digital Lending, Data Localization & Tokenization

**Official Statutory Authority**: Reserve Bank of India (RBI) Master Directions DOR.CRE.REC.66/21.07.001/2022-23 & DPSS Directives.

#### 1. Digital Lending Apps (DLAs) / Lending Service Providers (LSPs)
- **Zero Contact/Media Access**: DLAs cannot request or access mobile phone resources such as Contacts, Call Logs, Media/Photos, or internal storage.
- **One-time e-KYC Access**: Camera, microphone, and location access is strictly allowed on a one-time basis for KYC/onboarding with explicit consent.
- **Right to be Forgotten**: Borrowers have the right to revoke consent and request data deletion from DLAs and LSPs.

#### 2. Data Localization Mandate (2018 Directive)
- All payment system providers (UPI, Card Gateways, Wallets) must store end-to-end payment data exclusively on servers physically located within India (e.g. AWS Mumbai, GCP Delhi).

#### 3. Card-on-File Tokenization (CoFT)
- Merchants and payment gateways are banned from storing actual 16-digit card numbers and CVVs. Transactions must use network-issued CoFT tokens.

#### 4. Developer Rule
```kotlin
// RBI Compliance: Filter prohibited permissions
val isViolating = declaredPermissions.any { 
    it in listOf("android.permission.READ_CONTACTS", "android.permission.READ_CALL_LOG") 
}
```
            """.trimIndent()

            qLower.contains("erasure") || qLower.contains("delete") || qLower.contains("wipe") || qLower.contains("sec 12") || qLower.contains("section 12") -> """
### DPDP Act 2023 Section 12: Right to Erasure & Data Minimization

**Official Statutory Authority**: The Gazette of India Extraordinary, Act No. 22 of 2023, Section 12(3).

#### 1. Statutory Mandate
- Data Principals have a statutory right to request erasure of their personal data upon withdrawal of consent or when the purpose is complete.
- Data Fiduciaries must execute cascading deletion across all active databases, caches, and third-party Data Processors.

#### 2. Technical Requirements for Mobile Apps
- Provide a single-tap in-app self-service "Purge & Erase" feature.
- Generate an immutable, cryptographic audit token for every deletion event and record it in the statutory audit log.
- Stop background sync and telemetry upon erasure confirmation.

#### 3. Penalties
- Up to ₹50 Crore under Schedule 1 for failing to fulfill Data Principal rights.
            """.trimIndent()

            else -> """
### Comprehensive Indian Digital Regulatory Framework (DPDP 2023 + UIDAI + RBI)

**Official Statutory Reference**: DPDP Act 2023 (Act 22 of 2023), UIDAI Directives, RBI Master Directions, CERT-In Sec 70B.

#### 1. DPDP Act 2023 (Ministry of Electronics and Information Technology)
- **Section 5**: Mandatory unbundled, itemized notice preceding consent in plain language.
- **Section 6**: Affirmative, unbundled consent with effortless one-click withdrawal mechanism.
- **Section 9**: Verifiable parental consent and complete ban on behavioral tracking / targeted ads for minors (<18).
- **Section 12**: Mandatory self-service data erasure and correction workflows.
- **Section 14**: Right to nominate legal representatives in the event of death or incapacity.

#### 2. UIDAI Regulations (Aadhaar)
- **Masked Aadhaar**: Complete ban on storing 12-digit plaintext Aadhaar in local databases. Only `XXXXXXXX1234` allowed.
- **Aadhaar Data Vault**: Hardware Security Module (HSM) isolation with encrypted reference tokens.

#### 3. RBI Master Directions (FinTech & Lending)
- **Digital Lending**: Total prohibition on accessing phone contacts, call logs, and device storage.
- **Data Localization**: Storage of payment system data strictly inside Indian cloud regions.
- **CoFT**: Tokenized payment methods with zero CVV/PAN storage.

${if (statusNote.isNotEmpty()) "\n> ℹ️ **Status Note**: $statusNote" else ""}
            """.trimIndent()
        }

        return GroundedGuideResult(
            query = query,
            synthesizedText = synthesizedText,
            domain = domain,
            sources = getDefaultOfficialSources(domain),
            timestamp = System.currentTimeMillis(),
            isSimulatedOrFallback = true
        )
    }
}
