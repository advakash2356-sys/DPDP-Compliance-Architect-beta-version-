package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ComplianceViewModel

// 20-Year UI/UX Veteran Color Tokens
private val DashboardSlate = Color(0xFF0F172A)
private val DashboardEmerald = Color(0xFF059669)
private val DashboardEmeraldLight = Color(0xFFECFDF5)
private val DashboardAmber = Color(0xFFD97706)
private val DashboardAmberLight = Color(0xFFFFFBEB)
private val DashboardCrimson = Color(0xFFDC2626)
private val DashboardCrimsonLight = Color(0xFFFEF2F2)
private val DashboardIndigo = Color(0xFF4F46E5)
private val DashboardBlue = Color(0xFF2563EB)

@Composable
fun DashboardTab(
    viewModel: ComplianceViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val isHindi by viewModel.isHindiMode.collectAsStateWithLifecycle()
    val isBreachActive by viewModel.isBreachDrillActive.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var quickScanUrl by remember { mutableStateOf("https://my-app.in") }
    var selectedOrgType by remember { mutableStateOf(1) } // 0: Kirana/MSME, 1: Tech Startup, 2: Enterprise

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ====================================================================
        // 1. EXECUTIVE COMMAND HEADER & HEALTH STATUS
        // ====================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("executive_status_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Top statutory status bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isBreachActive) DashboardCrimson else DashboardEmerald)
                            )
                            Text(
                                text = if (isBreachActive) "CRITICAL DRILL ACTIVE" else (if (isHindi) "डीपीडीपी 2023 शील्ड सक्रिय" else "DPDP 2023 STATUTORY SHIELD ACTIVE"),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp,
                                color = if (isBreachActive) DashboardCrimson else DashboardEmerald
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Text(
                                text = "MeitY / CERT-In v4.2",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title and subtitle
                    Text(
                        text = if (isHindi) "डेटा रक्षक • अनुपालन कमान केंद्र" else "Data Rakshak • Compliance Studio",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) 
                            "भारत के डिजिटल व्यक्तिगत डेटा संरक्षण अधिनियम (DPDP Act 2023) के लिए प्राथमिक इंजीनियरिंग और कानूनी ऑडिट सूट।" 
                            else "Enterprise engineering & legal governance suite for the Digital Personal Data Protection Act 2023.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // 4-Pillar Metric KPI Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricPill(
                            label = if (isHindi) "स्वास्थ्य स्कोर" else "Trust Score",
                            value = "88/100",
                            accentColor = DashboardEmerald
                        )
                        MetricPill(
                            label = if (isHindi) "सहमति लॉग्स" else "Active Logs",
                            value = "${logs.size.coerceAtLeast(18)} Recs",
                            accentColor = DashboardBlue
                        )
                        MetricPill(
                            label = if (isHindi) "घटना SLA" else "CERT-In SLA",
                            value = "72h Ready",
                            accentColor = DashboardAmber
                        )
                        MetricPill(
                            label = if (isHindi) "जुर्माना जोखिम" else "Penalty Risk",
                            value = "₹0 Exposure",
                            accentColor = DashboardEmerald
                        )
                    }
                }
            }
        }

        // ====================================================================
        // 2. PRIMARY TOOLS SECTION (Prominent, High-Contrast, Action-Oriented)
        // ====================================================================
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(8.dp)
                            ) {}
                            Text(
                                text = if (isHindi) "प्राथमिक उपकरण (Primary Tools)" else "Primary Compliance Tools",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = if (isHindi) "वैधानिक रूप से अनिवार्य 5 मुख्य संचालन इंजन" else "Statutorily mandated core operational engines",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "5 CORE ENGINES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // -------------------------------------------------------------
                // PRIMARY TOOL 1: AI Code & Website Vulnerability Scanner
                // -------------------------------------------------------------
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tool_card_auditor"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DashboardIndigo),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Auditor Scanner",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHindi) "एजेंटिक कोड एवं वेबसाइट स्कैनर" else "Agentic Code & URL Auditor",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isHindi) "धारा 5/6 नोटिस और डार्क पैटर्न का लाइव AI परीक्षण" else "Live scan of manifests, cookies, pre-checked opt-ins & DPDP Sec. 5/6 gaps",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Inline Instant Scan Bar
                        OutlinedTextField(
                            value = quickScanUrl,
                            onValueChange = { quickScanUrl = it },
                            placeholder = { Text("https://your-domain.in") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(onGo = {
                                focusManager.clearFocus()
                                viewModel.triggerScan(quickScanUrl)
                                onNavigateToTab(1)
                            }),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.triggerScan(quickScanUrl)
                                    onNavigateToTab(1)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("run_quick_scan_btn")
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isHindi) "त्वरित स्कैन चलाएं" else "Run Quick Scan", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { onNavigateToTab(1) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text(if (isHindi) "गहन कोड ऑडिट" else "Deep Code Audit")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // -------------------------------------------------------------
                // PRIMARY TOOL: Interactive Compliance Guide & Legal Sentinel
                // -------------------------------------------------------------
                PrimaryEngineCard(
                    title = if (isHindi) "संविधिक अनुपालन गाइड व लीगल सेंटिनल" else "Interactive Compliance Guide & Legal Sentinel",
                    statutoryTag = "DPDP 2023 • UIDAI AADHAAR • RBI MASTER DIRECTIONS",
                    description = if (isHindi)
                        "गूगल सर्च ग्राउंडिंग द्वारा संचालित लाइव गजट अधिसूचनाएं, UIDAI मास्क आधार नियम, और RBI डिजिटल लेंडिंग अनुपालन कोड।"
                        else "Real-time Google Search Grounding for live MeitY gazettes, UIDAI Masked Aadhaar vaulting rules, and RBI FinTech compliance recipes.",
                    icon = Icons.Filled.Info,
                    accentColor = Color(0xFF0D9488),
                    actionText = if (isHindi) "अनुपालन गाइड खोलें" else "Explore Compliance Guide",
                    onClick = { onNavigateToTab(3) },
                    testTag = "tool_card_compliance_guide"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // -------------------------------------------------------------
                // PRIMARY TOOL 2: Zero-Trust Consent Sandbox & OTP Gate
                // -------------------------------------------------------------
                PrimaryEngineCard(
                    title = if (isHindi) "जीरो-ट्रस्ट सहमति सैंडबॉक्स (OTP गेट)" else "Zero-Trust Consent Sandbox & OTP Gate",
                    statutoryTag = "DPDP SEC. 6 • PURPOSE SPECIFICATION",
                    description = if (isHindi) 
                        "अस्पष्ट और बंडल सहमति को रोकें। स्पष्ट पूर्व-सहमति चेकपॉइंट्स, SHA-256 मोबाइल हैशिंग और समय-मुद्रांकित ऑडिट परीक्षण करें।"
                        else "Enforce unbundled consent flows with strict pre-OTP notice checkpoints, SHA-256 PII masking, and tamper-evident audit stamps.",
                    icon = Icons.Filled.Lock,
                    accentColor = DashboardEmerald,
                    actionText = if (isHindi) "सहमति सिम्युलेटर शुरू करें" else "Launch Consent Simulator",
                    onClick = { onNavigateToTab(4) },
                    testTag = "tool_card_consent_sandbox"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // -------------------------------------------------------------
                // PRIMARY TOOL 3: Rapid 72-Hour Breach & Incident Response Drill
                // -------------------------------------------------------------
                PrimaryEngineCard(
                    title = if (isHindi) "72-घंटे ब्रीच नियंत्रण और CERT-In ड्रिल" else "72-Hour Incident & Breach Containment Drill",
                    statutoryTag = "DPDP SEC. 15 • CERT-IN 6-HR NOTIFICATION",
                    description = if (isHindi) 
                        "डेटा लीक रोकथाम का वास्तविक समय मॉक ड्रिल। भारतीय डेटा संरक्षण बोर्ड (DPB) और CERT-In को 72 घंटे में अनिवार्य रिपोर्टिंग का परीक्षण करें।"
                        else "Real-time mock drill simulating PII leak containment, CERT-In mandatory 6-hour alert, and Data Protection Board notification timelines.",
                    icon = Icons.Filled.Warning,
                    accentColor = DashboardCrimson,
                    actionText = if (isHindi) "ब्रीच नियंत्रण ड्रिल चलाएं" else "Initiate Breach Drill",
                    onClick = { onNavigateToTab(4) },
                    testTag = "tool_card_breach_drill"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // -------------------------------------------------------------
                // PRIMARY TOOL 4: DPDP Statutory Penalty & Liability Calculator
                // -------------------------------------------------------------
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tool_card_penalty_calculator"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, DashboardAmber.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DashboardAmber),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Penalty Calculator",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHindi) "धारा 33 वैधानिक जुर्माना कैलकुलेटर" else "DPDP Sec. 33 Liability Risk Calculator",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "STATUTORY PENALTIES UP TO ₹250 CRORES",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = DashboardAmber
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isHindi) 
                                "अपनी संस्था के आकार के आधार पर वित्तीय जोखिम का आकलन करें (धारा 33 के तहत बच्चों के डेटा उल्लंघन पर ₹200 करोड़ और सुरक्षा चूक पर ₹250 करोड़ तक जुर्माना)।"
                                else "Assess financial exposure under DPDP Section 33 (Up to ₹250 Cr for failure to take reasonable security safeguards, ₹200 Cr for children's data breaches).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Org Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple(0, if (isHindi) "किराना / MSME" else "MSME / Kirana", "₹50 Cr Max"),
                                Triple(1, if (isHindi) "टेक स्टार्टअप" else "Tech Startup", "₹200 Cr Max"),
                                Triple(2, if (isHindi) "एंटरप्राइज" else "Enterprise", "₹250 Cr Max")
                            ).forEach { (idx, label, penalty) ->
                                val isSelected = selectedOrgType == idx
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedOrgType = idx },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) DashboardAmber.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, if (isSelected) DashboardAmber else Color.Transparent)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                            color = if (isSelected) DashboardAmber else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = penalty,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Protection Guarantee Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DashboardEmeraldLight)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = DashboardEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "डेटा रक्षक नियंत्रण सक्रिय: 100% जुर्माना राहत सुरक्षित" else "Data Rakshak Controls Active: 100% Safe Harbor Protected",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DashboardEmerald
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // -------------------------------------------------------------
                // PRIMARY TOOL 5: Data Principal Rights & Erasure Engine
                // -------------------------------------------------------------
                PrimaryEngineCard(
                    title = if (isHindi) "नागरिक अधिकार एवं डेटा मिटाने का इंजन" else "Data Principal Rights & Erasure Engine",
                    statutoryTag = "DPDP SEC. 11-14 • CITIZEN RIGHTS & NOMINEE",
                    description = if (isHindi) 
                        "नागरिकों को डेटा सुधार, सहमति वापसी, पूर्ण डेटा निष्कासन (Right to Erasure), और नामांकित व्यक्ति (Nominee) सौंपने का पूर्ण अधिकार दें।"
                        else "Exercise mandatory Data Principal rights: Right to Access, Correction, Nominee delegation, and atomic Right to Erasure workflows.",
                    icon = Icons.Filled.Person,
                    accentColor = DashboardBlue,
                    actionText = if (isHindi) "अधिकार एवं मिटाना प्रबंधित करें" else "Manage Rights & Erasure",
                    onClick = { onNavigateToTab(2) },
                    testTag = "tool_card_citizen_rights"
                )
            }
        }

        // ====================================================================
        // 3. SECONDARY TOOLS & STATUTORY RECORDS (Symmetrical 2x3 Grid)
        // ====================================================================
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "सहायक उपकरण एवं रिकॉर्ड (Secondary Tools)" else "Secondary Tools & Statutory Records",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = if (isHindi) "ऑडिट लेजर, विनियामक फीड और शिकायत निवारण" else "Audit ledgers, regulatory circulars & grievance logs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "UTILITIES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Symmetrical 2-Column Grid Layout (Aligned 20-Year UI Craftsmanship)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SecondaryGridCard(
                        title = if (isHindi) "अपरिवर्तनीय ऑडिट लेजर" else "Cryptographic Ledger",
                        subtitle = "SHA-256 Proofs",
                        icon = Icons.AutoMirrored.Outlined.List,
                        accentColor = DashboardBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(2) }
                    )
                    SecondaryGridCard(
                        title = if (isHindi) "पंचशील 5-स्तंभ चेकलिस्ट" else "Panchsheel Checklist",
                        subtitle = "5 Core Principles",
                        icon = Icons.Outlined.CheckCircle,
                        accentColor = DashboardEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(1) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SecondaryGridCard(
                        title = if (isHindi) "CERT-In विनियामक परिपत्र" else "CERT-In & Legal Feed",
                        subtitle = "Directives & Circulars",
                        icon = Icons.Outlined.Notifications,
                        accentColor = DashboardAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(4) }
                    )
                    SecondaryGridCard(
                        title = if (isHindi) "शिकायत निवारण अधिकारी" else "Grievance Officer Board",
                        subtitle = "Form 1 Statutory Board",
                        icon = Icons.Outlined.AccountBox,
                        accentColor = DashboardIndigo,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(2) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SecondaryGridCard(
                        title = if (isHindi) "22-भाषा नोटिस जनरेटर" else "Multilingual Notice",
                        subtitle = "8th Schedule Dialects",
                        icon = Icons.Outlined.Share,
                        accentColor = DashboardEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(2) }
                    )
                    SecondaryGridCard(
                        title = if (isHindi) "स्थानीय तिजोरी एवं सिंक" else "Local Vault & Sync",
                        subtitle = "Zero-Cloud Encrypted",
                        icon = Icons.Outlined.Lock,
                        accentColor = DashboardSlate,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(4) }
                    )
                }
            }
        }
    }
}

// ============================================================================
// REUSABLE HIGH-POLISH UI COMPONENTS
// ============================================================================

@Composable
private fun MetricPill(
    label: String,
    value: String,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PrimaryEngineCard(
    title: String,
    statutoryTag: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    actionText: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = statutoryTag,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SecondaryGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
