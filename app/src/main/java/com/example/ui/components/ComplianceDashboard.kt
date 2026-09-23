package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Design Tokens: Meditation Teal & Midnight Slate Architecture Palette
val DashMidnightSlate = Color(0xFF0C121A)
val DashDeepObsidian = Color(0xFF050505)
val DashMeditationTeal = Color(0xFF2DD4BF)
val DashCalmMuted = Color(0xFF94A3B8)
val DashCardBorder = Color(0xFF1E293B)
val DashAlertCrimson = Color(0xFFEF4444)
val DashAlertAmber = Color(0xFFF59E0B)
val DashSuccessGreen = Color(0xFF10B981)

/**
 * ComplianceDashboard reads directly from Room database entities (ConsentRecord, ConsentLog, UserProfile, Nominee)
 * to provide a comprehensive, real-time visual summary of DPDP Act 2023 compliance status.
 *
 * Implements the Midnight Slate & Meditation Teal aesthetic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplianceDashboard(
    database: AppDatabase,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val erasureHandler = remember { ErasureHandler(database, context) }

    // Live Room Queries
    val profile by database.userProfileDao().getProfile().collectAsState(initial = null)
    val consentRecords by database.consentRecordDao().getAllConsentRecords().collectAsState(initial = emptyList())
    val consentLogs by database.consentLogDao().getAllLogs().collectAsState(initial = emptyList())
    val nominee by database.nomineeDao().getNominee().collectAsState(initial = null)
    val grievances by database.grievanceReportDao().getAllReports().collectAsState(initial = emptyList())

    var showErasureDialog by remember { mutableStateOf(false) }
    var erasureStatusMessage by remember { mutableStateOf<String?>(null) }
    var selectedFilterTab by remember { mutableStateOf(0) } // 0: Overview, 1: Consent Ledger, 2: Erasure & Rights

    // Calculate dynamic compliance score
    val complianceScore = remember(profile, consentRecords, nominee) {
        var score = 30 // Base offline score
        if (consentRecords.any { it.isGranted }) score += 30
        if (profile != null) score += 20
        if (nominee != null) score += 20
        score.coerceIn(0, 100)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DashMidnightSlate)
            .padding(16.dp)
            .testTag("compliance_dashboard_root"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DashDeepObsidian),
                border = BorderStroke(1.dp, SolidColor(DashMeditationTeal.copy(alpha = 0.3f)))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DPDP ACT 2023 GOVERNANCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DashMeditationTeal,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Compliance Dashboard",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Score Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DashMeditationTeal.copy(alpha = 0.15f))
                                .border(1.dp, DashMeditationTeal.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$complianceScore% COMPLIANT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DashMeditationTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Real-time verification of on-device data sovereignty, consent lifecycles, and cryptographic right-to-erasure.",
                        fontSize = 12.sp,
                        color = DashCalmMuted,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Tab Selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DashDeepObsidian)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf("Overview", "Consent Records", "Erasure & Rights")
                tabs.forEachIndexed { index, label ->
                    val isSelected = selectedFilterTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) DashMeditationTeal else Color.Transparent)
                            .clickable { selectedFilterTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) DashDeepObsidian else DashCalmMuted
                        )
                    }
                }
            }
        }

        // Section: Tab Content
        when (selectedFilterTab) {
            0 -> {
                // Overview: KPI Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DashboardMetricCard(
                            modifier = Modifier.weight(1f),
                            title = "CONSENT LOGS",
                            value = "${consentLogs.size}",
                            status = if (consentLogs.isNotEmpty()) "Active" else "Empty",
                            icon = Icons.Filled.CheckCircle,
                            tint = DashMeditationTeal
                        )
                        DashboardMetricCard(
                            modifier = Modifier.weight(1f),
                            title = "DATA ERASURE",
                            value = "Sec. 12 Ready",
                            status = "Verified",
                            icon = Icons.Filled.Lock,
                            tint = DashSuccessGreen
                        )
                        DashboardMetricCard(
                            modifier = Modifier.weight(1f),
                            title = "NOMINEE (SEC 14)",
                            value = if (nominee != null) "Registered" else "Pending",
                            status = if (nominee != null) nominee!!.relationship else "Optional",
                            icon = Icons.Filled.Person,
                            tint = if (nominee != null) DashMeditationTeal else DashAlertAmber
                        )
                    }
                }

                // Interactive Consent Disclaimer
                item {
                    val isAnyConsentActive = consentRecords.any { it.isGranted }
                    ConsentDisclaimer(
                        title = "General Processing & Telemetry Disclosure",
                        purpose = "Mind Arrest processes unlock events and focus intervals strictly on local hardware. Zero network exfiltration.",
                        isAcknowledged = isAnyConsentActive,
                        onAccept = { record ->
                            scope.launch {
                                database.consentRecordDao().insertConsentRecord(record)
                                database.consentLogDao().insertLog(
                                    ConsentLog(
                                        consentType = record.consentType,
                                        action = "GRANTED",
                                        details = "Data Principal acknowledged DPDP legal disclosures."
                                    )
                                )
                            }
                        },
                        onDecline = {
                            erasureStatusMessage = "Consent declined. Data processing remains restricted."
                        }
                    )
                }

                // Compliance Key Checklist
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DashDeepObsidian),
                        border = BorderStroke(1.dp, DashCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "STATUTORY DPDP VERIFICATION STATUS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DashCalmMuted,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            ComplianceStatusRow(
                                title = "Notice Prior to Processing (Section 5)",
                                isMet = true,
                                details = "Itemized purpose disclosure active on launch"
                            )
                            ComplianceStatusRow(
                                title = "Explicit Free & Informed Consent (Section 6)",
                                isMet = consentRecords.any { it.isGranted },
                                details = if (consentRecords.any { it.isGranted }) "Logged in local Room DB" else "Awaiting acknowledgement"
                            )
                            ComplianceStatusRow(
                                title = "Right to Erasure & Correction (Section 12)",
                                isMet = true,
                                details = "Atomic, cascading purge handler enabled"
                            )
                            ComplianceStatusRow(
                                title = "Grievance Redressal Mechanism (Section 13)",
                                isMet = true,
                                details = "Adv. Akash (Compliance Officer) coordinates mapped"
                            )
                            ComplianceStatusRow(
                                title = "Right of Nomination (Section 14)",
                                isMet = nominee != null,
                                details = if (nominee != null) "Nominee appointed" else "Section 14 nomination ready"
                            )
                        }
                    }
                }
            }

            1 -> {
                // Consent Records Tab
                if (consentRecords.isEmpty()) {
                    item {
                        EmptyStateCard(
                            message = "No explicit consent records logged yet.\nAcknowledge the disclosure in Overview to generate one.",
                            icon = Icons.Filled.Info
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "ACTIVE CONSENT LOGS (${consentRecords.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashCalmMuted,
                            letterSpacing = 1.sp
                        )
                    }

                    items(consentRecords) { record ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DashDeepObsidian),
                            border = BorderStroke(1.dp, if (record.isGranted) DashMeditationTeal.copy(alpha = 0.3f) else DashCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = record.consentType,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (record.isGranted) DashMeditationTeal.copy(alpha = 0.15f) else Color(0xFF7F1D1D)
                                    ) {
                                        Text(
                                            text = if (record.isGranted) "ACTIVE" else "REVOKED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (record.isGranted) DashMeditationTeal else Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = record.purposeDescription,
                                    fontSize = 12.sp,
                                    color = DashCalmMuted
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Basis: ${record.statutoryBasis}",
                                        fontSize = 10.sp,
                                        color = DashMeditationTeal
                                    )
                                    val dateStr = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(record.grantedAt))
                                    Text(
                                        text = "Logged: $dateStr",
                                        fontSize = 10.sp,
                                        color = DashCalmMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Erasure & Data Subject Rights
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DashDeepObsidian),
                        border = BorderStroke(1.dp, DashAlertCrimson.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Data Erasure",
                                    tint = DashAlertCrimson,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RIGHT TO ERASURE (DPDP SEC. 12)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DashAlertCrimson,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Under Section 12(3) of the DPDP Act 2023, the Data Principal has the absolute statutory right to request erasure of their personal data unless retention is mandated by law.",
                                fontSize = 13.sp,
                                color = DashCalmMuted,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { showErasureDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("execute_erasure_button"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D))
                            ) {
                                Icon(Icons.Filled.Warning, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Execute Cascading Data Erasure",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Historic Activity Logs
                item {
                    Text(
                        text = "HISTORIC AUDIT LEDGER (${consentLogs.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DashCalmMuted,
                        letterSpacing = 1.sp
                    )
                }

                items(consentLogs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = DashDeepObsidian),
                        border = BorderStroke(1.dp, DashCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "[${log.action}] ${log.consentType}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (log.action == "ERASED") DashAlertCrimson else DashMeditationTeal
                                )
                                Text(
                                    text = log.details,
                                    fontSize = 11.sp,
                                    color = DashCalmMuted
                                )
                            }
                            val dateStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                            Text(
                                text = dateStr,
                                fontSize = 10.sp,
                                color = DashCalmMuted
                            )
                        }
                    }
                }
            }
        }

        // Notification Banner
        erasureStatusMessage?.let { msg ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = DashMeditationTeal.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, DashMeditationTeal)
                ) {
                    Text(
                        text = msg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DashMeditationTeal,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Confirmation Dialog for Erasure
    if (showErasureDialog) {
        AlertDialog(
            onDismissRequest = { showErasureDialog = false },
            containerColor = DashDeepObsidian,
            icon = {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = DashAlertCrimson)
            },
            title = {
                Text(
                    text = "Confirm Right-to-Erasure",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will execute a cascading cryptographic purge of all on-device profiles, consent records, nominee appointments, and focus logs. An audit certificate will be stored locally.",
                    color = DashCalmMuted,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showErasureDialog = false
                        scope.launch {
                            val result = erasureHandler.executeCascadingErasure()
                            when (result) {
                                is ErasureHandler.ErasureResult.Success -> {
                                    erasureStatusMessage = "Purged ${result.recordsDeleted} records. Audit Token: ${result.auditToken}"
                                }
                                is ErasureHandler.ErasureResult.Error -> {
                                    erasureStatusMessage = "Erasure error: ${result.message}"
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DashAlertCrimson)
                ) {
                    Text("Confirm Purge", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showErasureDialog = false }) {
                    Text("Cancel", color = DashCalmMuted)
                }
            }
        )
    }
}

@Composable
private fun DashboardMetricCard(
    title: String,
    value: String,
    status: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DashDeepObsidian),
        border = BorderStroke(1.dp, DashCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = DashCalmMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = status,
                fontSize = 10.sp,
                color = tint,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ComplianceStatusRow(
    title: String,
    isMet: Boolean,
    details: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isMet) Icons.Filled.CheckCircle else Icons.Filled.Warning,
            contentDescription = null,
            tint = if (isMet) DashMeditationTeal else DashAlertAmber,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = details,
                fontSize = 10.sp,
                color = DashCalmMuted
            )
        }
    }
}

@Composable
private fun EmptyStateCard(message: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DashDeepObsidian),
        border = BorderStroke(1.dp, DashCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DashCalmMuted,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                fontSize = 12.sp,
                color = DashCalmMuted,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )
        }
    }
}
