package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.*
import com.example.ui.documents.ComplianceDocs
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainComplianceScreen(
    viewModel: ComplianceViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val nominee by viewModel.nominee.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()

    val preOtpConsent by viewModel.preOtpConsent.collectAsStateWithLifecycle()
    val otpStatus by viewModel.otpStatus.collectAsStateWithLifecycle()
    val otpInput by viewModel.otpInput.collectAsStateWithLifecycle()
    val phoneInput by viewModel.phoneInput.collectAsStateWithLifecycle()
    val otpSeconds by viewModel.otpRemainingSeconds.collectAsStateWithLifecycle()
    val hashedPhone by viewModel.hashedActivePhone.collectAsStateWithLifecycle()

    val isBreachActive by viewModel.isBreachDrillActive.collectAsStateWithLifecycle()
    val breachTime by viewModel.breachTimeRemaining.collectAsStateWithLifecycle()
    val breachLogs by viewModel.breachLogList.collectAsStateWithLifecycle()

    val feedback by viewModel.feedbackMessage.collectAsStateWithLifecycle()
    val isSmsSending by viewModel.isSmsSentIndicator.collectAsStateWithLifecycle()
    val isPurging by viewModel.isPurgingActive.collectAsStateWithLifecycle()
    val totalPurged by viewModel.totalPurgedCount.collectAsStateWithLifecycle()

    // Agent auditor state flow collections
    val auditTasks by viewModel.auditTasks.collectAsStateWithLifecycle()
    val auditedCodeInput by viewModel.auditedCodeInput.collectAsStateWithLifecycle()
    val isAuditingInProgress by viewModel.isAuditingInProgress.collectAsStateWithLifecycle()
    val auditResult by viewModel.auditResult.collectAsStateWithLifecycle()
    val auditorAppName by viewModel.auditorAppName.collectAsStateWithLifecycle()
    val auditorPackageName by viewModel.auditorPackageName.collectAsStateWithLifecycle()
    val activeGuidelinesText by viewModel.activeGuidelinesText.collectAsStateWithLifecycle()
    val isFaqSyncing by viewModel.isFaqSyncing.collectAsStateWithLifecycle()

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val currentUser = (authState as? com.example.data.AuthState.Authorized)?.user
    val isAdmin = currentUser?.role == "ADMIN"

    var selectedTab by remember { mutableStateOf(0) }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedback) {
        feedback?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompactWidth = maxWidth < 600.dp
        var isSidebarOpen by remember(isCompactWidth) { mutableStateOf(!isCompactWidth) }

        Scaffold(
            topBar = {
                val isHindi by viewModel.isHindiMode.collectAsStateWithLifecycle()
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Shield Indicator",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                if (isHindi) "डेटा रक्षक (Data Rakshak)" else "Data Rakshak",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { viewModel.toggleHindiMode(!isHindi) },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.padding(end = 4.dp).testTag("toggle_language_button")
                        ) {
                            Text(
                                text = if (isHindi) "🇬🇧 EN" else "🇮🇳 HI",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // User profile chip with role
                        currentUser?.let { user ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (user.role == "ADMIN") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (user.role == "ADMIN") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = user.displayName.ifBlank { user.email }.take(1).uppercase(),
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Text(
                                        text = user.role,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp),
                                        color = if (user.role == "ADMIN") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Sign Out action
                        IconButton(
                            onClick = { viewModel.signOut() },
                            modifier = Modifier.testTag("app_bar_sign_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Logout,
                                contentDescription = "Sign Out",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { isSidebarOpen = !isSidebarOpen },
                            modifier = Modifier.testTag("toggle_sidebar_button")
                        ) {
                            Icon(
                                imageVector = if (isSidebarOpen) Icons.Filled.Close else Icons.Filled.Menu,
                                contentDescription = "Toggle Compliance Checklist Sidebar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "Audit") },
                        label = { Text("Audit") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Consent") },
                        label = { Text("Consent") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Filled.Info, contentDescription = "Guide") },
                        label = { Text("Guide") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = { Icon(Icons.Filled.Build, contentDescription = "Sandbox") },
                        label = { Text("Sandbox") }
                    )
                    if (isAdmin) {
                        NavigationBarItem(
                            selected = selectedTab == 5,
                            onClick = { selectedTab = 5 },
                            icon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Admin") },
                            label = { Text("Admin") },
                            modifier = Modifier.testTag("nav_item_admin")
                        )
                    }
                    NavigationBarItem(
                        selected = if (isAdmin) selectedTab == 6 else selectedTab == 5,
                        onClick = { selectedTab = if (isAdmin) 6 else 5 },
                        icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
            // Active Security incident breach drill banner simulation container
            AnimatedVisibility(
                visible = isBreachActive,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Error warning icon",
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    "🚨 CRITICAL INCIDENT SIMULATION • DUAL STATUTORY SLA",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    "CERT-In 6-Hr Alert: 05:42:18 | DPDP Sec. 15 Board SLA: $breachTime remaining",
                                    fontSize = 11.sp,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Button(
                            onClick = { viewModel.stopBreachDrill() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("stop_drill_button")
                        ) {
                            Text("CONTAIN BREACH", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Navigation handled by bottomBar

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> DashboardTab(
                        viewModel = viewModel,
                        onNavigateToTab = { selectedTab = it }
                    )
                    1 -> ComplianceAuditTab(
                        viewModel = viewModel,
                        auditTasks = auditTasks,
                        codeBuffer = auditedCodeInput,
                        isAuditing = isAuditingInProgress,
                        result = auditResult,
                        appName = auditorAppName,
                        packageName = auditorPackageName,
                        regulations = activeGuidelinesText,
                        isSyncing = isFaqSyncing
                    )
                    2 -> ConsentManagementTab(
                        profile = profile,
                        nominee = nominee,
                        logs = logs,
                        reports = reports,
                        hashedPhone = hashedPhone
                    )
                    3 -> ComplianceGuideScreen(
                        viewModel = viewModel
                    )
                    4 -> SandboxTab(
                        profile = profile,
                        nominee = nominee,
                        preOtpConsent = preOtpConsent,
                        otpStatus = otpStatus,
                        otpInput = otpInput,
                        phoneInput = phoneInput,
                        otpSeconds = otpSeconds,
                        hashedPhone = hashedPhone,
                        isBreachActive = isBreachActive,
                        breachTime = breachTime,
                        breachLogs = breachLogs,
                        isSmsSending = isSmsSending,
                        isPurging = isPurging,
                        totalPurged = totalPurged,
                        viewModel = viewModel
                    )
                    5 -> {
                        if (isAdmin) {
                            AdminUserManagementScreen(viewModel = viewModel)
                        } else {
                            SettingsTab()
                        }
                    }
                    6 -> {
                        if (isAdmin) {
                            SettingsTab()
                        } else {
                            DashboardTab(viewModel = viewModel, onNavigateToTab = { selectedTab = it })
                        }
                    }
                    else -> DashboardTab(
                        viewModel = viewModel,
                        onNavigateToTab = { selectedTab = it }
                    )
                }
            }
        }
    }
}

        
    }
}

// ==========================================
// TAB 1: REGULATORY COMPLIANCE SANDBOX
// ==========================================
@Composable
fun SandboxTab(
    profile: UserProfile?,
    nominee: Nominee?,
    preOtpConsent: Boolean,
    otpStatus: OtpStatus,
    otpInput: String,
    phoneInput: String,
    otpSeconds: Int,
    hashedPhone: String,
    isBreachActive: Boolean,
    breachTime: String,
    breachLogs: List<String>,
    isSmsSending: Boolean,
    isPurging: Boolean,
    totalPurged: Int,
    viewModel: ComplianceViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            DataProcessingSandbox()
        }

        // Section: System State Welcome Card & MASTER COMPLIANCE KPI DASHBOARD (DPDP SEC. 5/6/9/12)
        item {
            val databaseInUse = viewModel.databaseClassInUse.collectAsState().value
            val auditTasksList = viewModel.auditTasks.collectAsState().value
            val uniqueAuditedApps = (auditTasksList.map { it.appName } + listOf("Mishra Delivery Pro", "India Grocers Express")).distinct().size
            val securedApps = auditTasksList.count { it.isResolved } + 1
            val unresolvedApps = auditTasksList.count { !it.isResolved } + 1

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("compliance_dashboard_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header welcome row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (profile != null) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                            contentDescription = "System registration status icon",
                            tint = if (profile != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(36.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "India DPDP Compliance & Auditor Hub",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Database: $databaseInUse",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        "SYSTEM REGULATION SKILLS (DPDPA 2023 CANONICAL):", 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Bold, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Grid of standard skills chip-labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("♊ AI Auditor", "🗄️ Room SQLite", "🧹 Erasure Eng", "🕸️ Apify Scraper").forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(skill, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("🔒 Zero-Trust Auth", "👁️ Notice Verify", "📅 15-Day Sync").forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(skill, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "AUDITING KPI METRIC INDEX", 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Bold, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Card 1: Apps Viewed
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("$uniqueAuditedApps", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                                Text("Apps Viewed", fontSize = 10.sp, maxLines = 1, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                        
                        // Card 2: Secured
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("$securedApps", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                                Text("Secured", fontSize = 10.sp, maxLines = 1, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        
                        // Card 3: Still Defect / Not Working
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("$unresolvedApps", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error)
                                Text("Not Secured", fontSize = 10.sp, maxLines = 1, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        // Section 1: Zero-Trust Consent & Authentication Sandbox
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScreenHeader(
                        title = "1. Zero-Trust Consent & OTP Flow",
                        icon = Icons.Filled.Lock,
                        tagline = "Mandatory prior consent checkpoint with Trail audits."
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        "Test Case: Attempting to browse phone fields or issue SMS OTP triggers prior to consent collection is programmatically forbidden to prevent background tracker collection.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Checkbox requirement for Consent
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { viewModel.onPreOtpConsentChanged(!preOtpConsent) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = preOtpConsent,
                            onCheckedChange = { viewModel.onPreOtpConsentChanged(it) },
                            modifier = Modifier.testTag("pre_otp_checkbox")
                        )
                        Text(
                            text = "Consent explicitly to having my mobile identifier and profile details stored and verified under DPDP Act protocols.",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (preOtpConsent) {
                        // Display inputs
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                "Enter Phone Number for registration:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { viewModel.onPhoneInputChanged(it) },
                                label = { Text("Mobile Number (E.g. +91 9876543210)") },
                                placeholder = { Text("+91...") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("phone_input")
                            )

                            Button(
                                onClick = { viewModel.sendOtp(phoneInput) },
                                enabled = !isSmsSending,
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .testTag("send_otp_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                if (isSmsSending) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sending...")
                                } else {
                                    Text("Request OTP Securely")
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                                .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(6.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                "🚫 Access Restricted: Explicit pre-consent check must be active before telephone identifier node triggers are enabled.",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Handle verification steps on demand
                    when (otpStatus) {
                        is OtpStatus.Sent -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "Simulated SMS Gateway Received 📲",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "Simulated OTP code sent over TRAI-compliant sender ID. Active passcode: '2356'. Expiration scheduled in $otpSeconds seconds.",
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    OutlinedTextField(
                                        value = otpInput,
                                        onValueChange = { if (it.length <= 4) viewModel.onOtpInputChanged(it) },
                                        label = { Text("Enter 4-Digit Verification OTP") },
                                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
                                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("otp_input")
                                    )

                                    Button(
                                        onClick = { viewModel.verifyOtp(otpInput) },
                                        modifier = Modifier
                                            .align(Alignment.End)
                                            .testTag("verify_otp_button")
                                    ) {
                                        Text("Verify & Bind Authorized State")
                                    }
                                }
                            }
                        }
                        OtpStatus.Verified -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        "✅ ACCOUNT CONSENT DIGITALLY SECURED!",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "Transaction successfully saved. Stored PII variables securely hashed in SQLite DB. Check audit logs timeline.",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                        OtpStatus.Expired -> {
                            Text(
                                "❌ OTP Verification request expired. Security protocol reset.",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        is OtpStatus.Error -> {
                            Text(
                                "⚠️ Error: ${(otpStatus as OtpStatus.Error).message}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        OtpStatus.Unstarted -> {}
                    }
                }
            }
        }

        // Section 2: Ruthless Data Minimization (Data Inventory)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ScreenHeader(
                        title = "2. Ruthless Data Minimization",
                        icon = Icons.Filled.Delete,
                        tagline = "Map collected data strictly to purpose & purge dynamically."
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        "An auditing view mapping active database cells to statutory justification under the law. Outdated audit traces can be minimized via the automated daemon sweep.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Real-Time Inventory Table
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        // Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Field", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Statutory Purpose", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Retention", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Device Location", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        // Rows
                        InventoryRow("Name / Msg", "Identify verified agent state", "Until deletion", "Room DB Profile")
                        InventoryRow("Email String", "Route alerts / support", "Until deletion", "Room DB Profile")
                        InventoryRow("Hashed Phone", "OTP compliance lock", "Session + 10m", "In-Memory hash")
                        InventoryRow("Nominee nodes", "Capacity Section 13", "Account life", "Room DB Nominee")
                        InventoryRow("Consent Logs", "Section 6 explicit audit trail", "Audit Sweep demo", "Room Logs ledger")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Minimized logs total: $totalPurged entries",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Retentions threshold: 30 seconds (simulation)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { viewModel.triggerAutomatedPurge() },
                            enabled = !isPurging,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.testTag("purge_button")
                        ) {
                            if (isPurging) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sweeping...")
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("Run Purge Daemon")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Enforceable User Rights Center
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScreenHeader(
                        title = "3. Enforceable User Rights Center",
                        icon = Icons.Filled.Person,
                        tagline = "Access & correction, Section 13 nomination, and Sec 12 Erasures."
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    if (profile == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "⚠️ User Registry Empty. Create an account via Section 1 OTP Flow to activate user rights exercises.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Interactive User Rights Section
                        var rightSubtab by remember { mutableStateOf(0) }

                        TabRow(
                            selectedTabIndex = rightSubtab,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                        ) {
                            Tab(selected = rightSubtab == 0, onClick = { rightSubtab = 0 }) {
                                Text("A. Correction", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                            }
                            Tab(selected = rightSubtab == 1, onClick = { rightSubtab = 1 }) {
                                Text("B. Nominate (Sec 13)", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                            }
                            Tab(selected = rightSubtab == 2, onClick = { rightSubtab = 2 }) {
                                Text("C. Erasure (Sec 12)", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        when (rightSubtab) {
                            0 -> {
                                // Dynamic Correction Form
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Self-Serve Information Correct (Under Sec 12)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                    
                                    var nameInput by remember { mutableStateOf(profile.name) }
                                    var emailInput by remember { mutableStateOf(profile.email) }
                                    var phInput by remember { mutableStateOf(profile.phone) }
                                    var mktInput by remember { mutableStateOf(profile.hasMarketingConsent) }

                                    OutlinedTextField(
                                        value = nameInput,
                                        onValueChange = { nameInput = it },
                                        label = { Text("Full Name") },
                                        modifier = Modifier.fillMaxWidth().testTag("profile_name")
                                    )
                                    OutlinedTextField(
                                        value = emailInput,
                                        onValueChange = { emailInput = it },
                                        label = { Text("Email Identity") },
                                        modifier = Modifier.fillMaxWidth().testTag("profile_email")
                                    )
                                    OutlinedTextField(
                                        value = phInput,
                                        onValueChange = { phInput = it },
                                        label = { Text("Registered Telephone") },
                                        modifier = Modifier.fillMaxWidth().testTag("profile_phone")
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                                            .padding(8.dp)
                                    ) {
                                        Switch(
                                            checked = mktInput,
                                            onCheckedChange = { mktInput = it },
                                            modifier = Modifier.testTag("marketing_switch")
                                        )
                                        Column {
                                            Text("Granular Marketing opt-in", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                            Text("Legally separated optional marketing channel consent.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Button(
                                        onClick = { viewModel.saveUserProfile(nameInput, emailInput, phInput, mktInput) },
                                        modifier = Modifier.align(Alignment.End).testTag("save_profile_button")
                                    ) {
                                        Text("Save and Auditable Log")
                                    }
                                }
                            }
                            1 -> {
                                // Right to Nominate Form
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Right to Nominate (Section 13 Mechanism)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                    Text("Engineering compliance requires registering legal designees to exercise user rights in case of death or legal incapacity.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                    var nName by remember { mutableStateOf(nominee?.nomineeName ?: "") }
                                    var nRel by remember { mutableStateOf(nominee?.relationship ?: "") }
                                    var nEmail by remember { mutableStateOf(nominee?.contactEmail ?: "") }
                                    var nPhone by remember { mutableStateOf(nominee?.contactPhone ?: "") }

                                    OutlinedTextField(
                                        value = nName,
                                        onValueChange = { nName = it },
                                        label = { Text("Nominee Full Name") },
                                        modifier = Modifier.fillMaxWidth().testTag("nominee_name")
                                    )
                                    OutlinedTextField(
                                        value = nRel,
                                        onValueChange = { nRel = it },
                                        label = { Text("Relationship to User") },
                                        modifier = Modifier.fillMaxWidth().testTag("nominee_relation")
                                    )
                                    OutlinedTextField(
                                        value = nEmail,
                                        onValueChange = { nEmail = it },
                                        label = { Text("Nominee Email") },
                                        modifier = Modifier.fillMaxWidth().testTag("nominee_email")
                                    )
                                    OutlinedTextField(
                                        value = nPhone,
                                        onValueChange = { nPhone = it },
                                        label = { Text("Nominee Phone") },
                                        modifier = Modifier.fillMaxWidth().testTag("nominee_phone")
                                    )

                                    Button(
                                        onClick = { viewModel.saveNominee(nName, nRel, nEmail, nPhone) },
                                        modifier = Modifier.align(Alignment.End).testTag("save_nominee_button")
                                    ) {
                                        Text("Save Legal Delegate")
                                    }
                                }
                            }
                            2 -> {
                                // One click erasure Danger zone
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .border(2.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                        .padding(16.dp)
                                ) {
                                    Text("☢️ DANGER ZONE: EXERCISE SECTION 12 RIGHT TO ERASURE", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                                    Text("Clicking below triggers instant, complete cryptographic wiping of your profile records, nominee keys, grievance report collections, and active session histories. Operation is immediate and logged legally.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)

                                    Button(
                                        onClick = { viewModel.deleteMyAccountAndData() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        ),
                                        modifier = Modifier.fillMaxWidth().testTag("erasure_button")
                                    ) {
                                        Text("Delete My Account & Data", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Third Party SDK Restrictions & Toggles
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScreenHeader(
                        title = "4. Third-Party Vendor Audits",
                        icon = Icons.Filled.Settings,
                        tagline = "Register Data Processor Agreements (DPAs) and disable optional tracking."
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        "Every analytical or hosting subprocess in active production needs associated signed DPAs to lock shared liability. Disabling optional tracing shuts down analytical trackers dynamically.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Vendor checklist
                    VendorItem("Cloud Database Relational Node (Primary Hosting)", "Compliant", "Valid DPA Executed (June 2026)")
                    VendorItem("Transactional SMS Gateway Node", "Compliant", "Valid DPA Executed (TRAI Registered)")
                    VendorItem("Analytical Telemetry Tracker (Optional)", "Configurable Out-Out", "Consent Revocation Enforced")

                    // Toggle to disable telemetry
                    if (profile != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Optional Analytical Telemetry", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                Text("Opting out stops mock analytical packets from dispatching.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = profile.isOptedInOptionalTelemetry,
                                onCheckedChange = { viewModel.updateOptionalTelemetry(it) },
                                modifier = Modifier.testTag("telemetry_switch")
                            )
                        }
                    }
                }
            }
        }

        // Section 5: Grievances & Breach Simulation Countdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScreenHeader(
                        title = "5. Grievances & Breach Drills",
                        icon = Icons.Filled.Warning,
                        tagline = "Filing grievances directly & simulated 72-hour notifications."
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Sub-navigator for Grievance vs Breach
                    var subSectionTab by remember { mutableStateOf(0) }
                    TabRow(
                        selectedTabIndex = subSectionTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    ) {
                        Tab(selected = subSectionTab == 0, onClick = { subSectionTab = 0 }) {
                            Text("A. Grievance Form", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                        }
                        Tab(selected = subSectionTab == 1, onClick = { subSectionTab = 1 }) {
                            Text("B. 72h Incident Drill", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (subSectionTab == 0) {
                        // Grievance filing
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Officer card callout
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("🇮🇳 DESIGNATED GRIEVANCE AND PROTECTION OFFICER", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    Text("Name: Mrs. Priya Sharma, DPO", style = MaterialTheme.typography.labelSmall)
                                    Text("Regulatory Address: Sector 4, Dwarka, New Delhi", style = MaterialTheme.typography.labelSmall)
                                    Text("Contact Gateway: grievance@dpdpsafeguard.in", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            var ripName by remember { mutableStateOf(profile?.name ?: "") }
                            var ripContact by remember { mutableStateOf(profile?.email ?: "") }
                            var ripSub by remember { mutableStateOf("") }
                            var ripDet by remember { mutableStateOf("") }

                            OutlinedTextField(
                                value = ripName,
                                onValueChange = { ripName = it },
                                label = { Text("Your Name") },
                                modifier = Modifier.fillMaxWidth().testTag("g_reporter_name")
                            )
                            OutlinedTextField(
                                value = ripContact,
                                onValueChange = { ripContact = it },
                                label = { Text("Contact Email / Phone") },
                                modifier = Modifier.fillMaxWidth().testTag("g_reporter_contact")
                            )
                            OutlinedTextField(
                                value = ripSub,
                                onValueChange = { ripSub = it },
                                label = { Text("Subject of Grievance Breach") },
                                modifier = Modifier.fillMaxWidth().testTag("g_subject")
                            )
                            OutlinedTextField(
                                value = ripDet,
                                onValueChange = { ripDet = it },
                                label = { Text("Meticulous Details") },
                                modifier = Modifier.fillMaxWidth().testTag("g_details")
                            )

                            Button(
                                onClick = {
                                    viewModel.submitGrievance(ripName, ripContact, ripSub, ripDet)
                                    ripSub = ""
                                    ripDet = ""
                                },
                                modifier = Modifier.align(Alignment.End).testTag("save_grievance_button")
                            ) {
                                Text("File Form Securely")
                            }
                        }
                    } else {
                        // 72 Hour Incident Drill Simulator
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Regulatory Breach Incident Response Simulator", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                            Text("Sec 15 mandates that on confirmation of any device file or database server leakage, both notifications to the Data Protection Board and alerts to impacted citizens must launch immediately.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            if (!isBreachActive) {
                                Button(
                                    onClick = { viewModel.startBreachDrill() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("start_drill_button")
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Filled.Warning, contentDescription = null)
                                        Text("Launch 72h Emergency Drill Simulation", fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                        .padding(12.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Filled.Warning, contentDescription = "Error status", tint = MaterialTheme.colorScheme.error)
                                            Text("SIMULATED EMERGENCY STATE: BREACH ACTIVE", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                        }

                                        Text("Countdown to Regulatory Breach Fine cutoff: $breachTime (Simulated Speed 3000x)", style = MaterialTheme.typography.labelSmall)

                                        Text("Simulated Incident Response Logs:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color.White)
                                                .padding(8.dp)
                                        ) {
                                            breachLogs.forEach { logItem ->
                                                Text("• $logItem", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                            }
                                        }

                                        Button(
                                            onClick = { viewModel.stopBreachDrill() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier.fillMaxWidth().testTag("sandbox_stop_drill")
                                        ) {
                                            Text("Resolve Incident, Seal Breach & Close Drill")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenHeader(title: String, icon: ImageVector, tagline: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Text(tagline, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun InventoryRow(item: String, purpose: String, retention: String, loc: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(item, modifier = Modifier.weight(1.2f), fontSize = 11.sp)
            Text(purpose, modifier = Modifier.weight(2f), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(retention, modifier = Modifier.weight(1.2f), fontSize = 11.sp)
            Text(loc, modifier = Modifier.weight(1f), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun VendorItem(title: String, status: String, contract: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            Text(contract, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = status,
            fontWeight = FontWeight.Bold,
            color = if (status == "Compliant") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (status == "Compliant") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun InteractiveStepperHUD(
    activeFunnel: Int,
    appName: String,
    packageName: String,
    codeBuffer: String,
    isAuditing: Boolean,
    result: AuditResult?,
    viewModel: ComplianceViewModel,
    onSelectFunnel: (Int) -> Unit
) {
    val scrapeUrlVal = viewModel.scrapeUrl.collectAsState().value
    val isScrapingVal = viewModel.isScrapingInProgress.collectAsState().value
    val docScanResultVal = viewModel.docScanResult.collectAsState().value
    val isScanningDocVal = viewModel.isDocScanningInProgress.collectAsState().value

    // Auto compute active step 1 to 4
    val step = when {
        result != null || docScanResultVal != null -> 4
        isAuditing || isScrapingVal || isScanningDocVal -> 3
        codeBuffer.length > 30 || (activeFunnel == 1 && scrapeUrlVal.startsWith("http")) -> 2
        else -> 1
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("interactive_toddler_stepper_hud"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with Rank Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🛡️ STEP-BY-STEP IMMUNITY PIPELINE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "RANK: SENIOR SENTINEL (${if (step == 4) "100" else (step - 1) * 25}/100 XP)",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Text(
                text = "Our idiot-proof checklist sequence simplifies India DPDPA compliance so radically that anyone can obtain active statutory immunity. Just click the highlighted targets below!",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Step Progress Timeline (A -> B -> C -> D)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val stepData = listOf(
                    Triple("A", "Set Target", "Define app inputs"),
                    Triple("B", "Set Pipeline", "Set scanner channel"),
                    Triple("C", "Run Scan", "Verify compliance"),
                    Triple("D", "Shield Secured", "Copy Secure Fixes")
                )

                stepData.forEachIndexed { i, (stepLetter, label, shortDesc) ->
                    val sNum = i + 1
                    val isCurr = step == sNum
                    val isPast = step > sNum
                    
                    val bColor = if (isCurr) MaterialTheme.colorScheme.primaryContainer 
                                 else if (isPast) MaterialTheme.colorScheme.primaryContainer 
                                 else MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                    val tColor = if (isCurr) MaterialTheme.colorScheme.primary 
                                 else if (isPast) MaterialTheme.colorScheme.primary 
                                 else MaterialTheme.colorScheme.onSurfaceVariant
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bColor)
                                .border(
                                    1.dp,
                                    if (isCurr) MaterialTheme.colorScheme.primary else if (isPast) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPast) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            } else {
                                Text(
                                    text = stepLetter,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = tColor
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isCurr) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isCurr) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    if (i < 3) {
                        Box(
                            modifier = Modifier
                                .weight(0.12f)
                                .height(2.dp)
                                .background(if (step > sNum) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                }
            }

            // Interactive HUD subpanel for toddler-proof actions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                when (step) {
                    1 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Text("👉 STEP A: TAP ANY TARGET BELOW TO LOAD FAILURE SAMPLES", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                "To run compliance scanning immediately, just click any of the preloaded compliance fail projects below to set up target assets in a single tap:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onSelectFunnel(0)
                                        viewModel.onAuditorAppNameChanged("Mishra Delivery Pro")
                                        viewModel.onAuditorPackageNameChanged("com.mishra.delivery")
                                        viewModel.onAuditedCodeChanged("""
// Mishra Delivery App Core - Dangerous Manifest Location Exposure
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.mishra.delivery">
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />
    <application android:label="Mishra Delivery">
        <service android:name=".LocationUpdateService" android:exported="true" />
    </application>
</manifest>
                                        """.trimIndent())
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("🚚 Location Leak", fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                
                                Button(
                                    onClick = {
                                        onSelectFunnel(0)
                                        viewModel.onAuditorAppNameChanged("PayWealth Finance")
                                        viewModel.onAuditorPackageNameChanged("com.paywealth.india")
                                        viewModel.onAuditedCodeChanged("""
// PayWealth Fintech - Sudden Forced Bulk Consent Agreement
fun startFintechFlow() {
    val termChecked = true // forces joint unbundled agreement
    val userNoticeAccepted = true
    // Sharing credit status data with 45 partner insurance brands
    println("Consent collected without optional specific toggles.")
}
                                        """.trimIndent())
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("🪙 fintech Joint", fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        onSelectFunnel(1)
                                        viewModel.setScrapeUrl("https://meity.gov.in/content/digital-personal-data-protection-act-2023")
                                        viewModel.onDocumentationTextChanged("""
All user specifications are cached automatically in external datalakes. We do not support user account removal or data erasure. No designated grievance redressal officer is active in our company. Optional telemetry is active and hardwired to 'ON' for maximum tracking performance.
                                        """.trimIndent())
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("🕸️ Policy Crawler", fontSize = 9.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    2 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Filled.Build, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                Text("📂 STEP B: PIPELINE READY — CONFIRM SELECTION", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                            Text(
                                "Your target asset has been loaded into memory! Switch funnels using the badges below to redirect the compliance scanning channel:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                FilterChip(
                                    selected = activeFunnel == 0,
                                    onClick = { onSelectFunnel(0) },
                                    label = { Text("Developer Build Scan (${appName.take(15)})") }
                                )
                                FilterChip(
                                    selected = activeFunnel == 1,
                                    onClick = { onSelectFunnel(1) },
                                    label = { Text("Web Crawler Scan (URL matches)") }
                                )
                            }
                        }
                    }
                    3 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
                                Text("⚙️ STEP C: RUN INTERACTIVE COMPLIANCE VERIFICATION", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary)
                            }
                            Text(
                                "Asset setup is fully completed! Let's scan. Scroll to the highlighted scan button of your active funnel to run AI audit scanners immediately.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(4.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    4 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Text("🏆 STEP D: COMPLIANCE IMMUNITY ACTIVE!", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                "Scan complete! Under MeitY Gazette mandates of DPDPA 2023, you have generated solid protection. Your liability status is cleared:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                                    Column {
                                        Text("🏆 CITIZEN IMMUNITY COMPLIENCE RATING: SECURED (100 XP)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                        Text("Scroll down to copy the compiled DPDPA 2023 Wipe-Code block and neutralize regulatory liabilities.", fontSize = 9.sp, color = Color(0xFF1B5E20))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComplianceAuditTab(
    auditTasks: List<AuditTask>,
    codeBuffer: String,
    isAuditing: Boolean,
    result: AuditResult?,
    appName: String,
    packageName: String,
    regulations: String,
    isSyncing: Boolean,
    viewModel: ComplianceViewModel
) {
    val checklistItems by viewModel.checklistItems.collectAsStateWithLifecycle()
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    var simApkLoading by remember { mutableStateOf(false) }
    var activeFunnel by remember { mutableStateOf(0) } // 0 = Funnel A: Developer Codebase & SDK API, 1 = Funnel B: Website Policy & Crawler
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text("DPDP 2023 Readiness Checklist", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Track your DPDP Act 2023 compliance status. Resolve issues to secure your application.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
        }

        val completedCount = checklistItems.count { it.isCompleted }
        val totalCount = checklistItems.size
        
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Overall Readiness", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("$completedCount / $totalCount", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { if (totalCount == 0) 0f else completedCount.toFloat() / totalCount },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(checklistItems) { item ->
            ActionItemCard(item = item, isResolved = item.isCompleted, isHindi = false, onToggle = { viewModel.toggleChecklistItem(item) })
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        }
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ScreenHeader(
                        title = "DPDP Agentic Auditor & Backlog",
                        icon = Icons.Filled.Lock,
                        tagline = "India DPDP Act 2023 Compliance Assistant & Wipe-Code Generator"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Use our specialized Indian Law compliance pipelines to audit digital personal assets. Select Funnel A to scan developer builds and decompile manifests. Select Funnel B to crawl live website copies and analyze disclosure terms.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section 1.5: Step-by-Step Gamified Timeline HUD - Radically easy toddler compliance
        item {
            InteractiveStepperHUD(
                activeFunnel = activeFunnel,
                appName = appName,
                packageName = packageName,
                codeBuffer = codeBuffer,
                isAuditing = isAuditing,
                result = result,
                viewModel = viewModel,
                onSelectFunnel = { activeFunnel = it }
            )
        }

        // Section 2: Law Database & 15-Day Auto Sync Indicator (DPDP SEC. 5/6/9)
        item {
            val lastSynced = viewModel.lastSyncedTime.collectAsState().value
            val nextScheduled = viewModel.nextScheduledTime.collectAsState().value
            val isDownloading = viewModel.isActDownloading.collectAsState().value
            val databaseInUse = viewModel.databaseClassInUse.collectAsState().value
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth().testTag("law_sync_ledger_card")
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Text("Law DB Accountability Ledger (15D Sync)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        }
                        
                        Button(
                            onClick = { viewModel.simulateForcedActDownload() },
                            enabled = !isDownloading,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("force_15day_upgrade_button")
                        ) {
                            if (isDownloading) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text("FORCE 15-DAY UPGRADE", fontSize = 9.sp)
                            }
                        }
                    }
                    
                    Text(
                        text = "Statutes automatically fetch and overwrite local SQLLite store layers every 15 days from Ministry of Electronics and IT (MeitY) verified central publication portals to ensure active compliance.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Database in Use:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(databaseInUse, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                        }
                        Column {
                            Text("Last Verified Pull:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(lastSynced, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                            Text("Next Auto Upgrade:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(nextScheduled, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    
                    if (isDownloading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp))
                        Text("Verifying digital signature key of Government Gazette releases...", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("ACTIVE COMPLIANCE AUDITING GUIDELINES BASELINE IN DATABASE:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text(
                        text = regulations,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                            .padding(8.dp)
                    )
                }
            }
        }

        // --- TWO DEDICATED COGNITIVE FUNNEL TAB SELECTION ROW ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    onClick = { activeFunnel = 0 },
                    colors = CardDefaults.cardColors(
                        containerColor = if (activeFunnel == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.weight(1f).testTag("funnel_a_selector_card"),
                    border = BorderStroke(1.dp, if (activeFunnel == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.Build, contentDescription = null, tint = if (activeFunnel == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        Text("Developer Build Funnel", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = if (activeFunnel == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Audits App Code & API SDK Notice Gaps", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    onClick = { activeFunnel = 1 },
                    colors = CardDefaults.cardColors(
                        containerColor = if (activeFunnel == 1) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.weight(1f).testTag("funnel_b_selector_card"),
                    border = BorderStroke(1.dp, if (activeFunnel == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = if (activeFunnel == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        Text("Company Web Funnel", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = if (activeFunnel == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Crawls Web Public terms & Privacy Statements", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // ==========================================
        // RENDER ACTIVE COGNITIVE FUNNEL
        // ==========================================
        if (activeFunnel == 0) {
            // FUNNEL A: DEVELOPER BUILD & CODEBASE SDK AUDIT
            item {
                Card(elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Text("Application Audit Profile", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        }

                        OutlinedTextField(
                            value = appName,
                            onValueChange = { viewModel.onAuditorAppNameChanged(it) },
                            label = { Text("App Project Name") },
                            modifier = Modifier.fillMaxWidth().testTag("app_name_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = packageName,
                            onValueChange = { viewModel.onAuditorPackageNameChanged(it) },
                            label = { Text("Application Package ID (e.g. com.company.app)") },
                            modifier = Modifier.fillMaxWidth().testTag("package_id_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Or Select a Non-Compliant Sandbox Target Project:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                        
                        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = appName == "Mishra Delivery Pro",
                                    onClick = {
                                        viewModel.onAuditorAppNameChanged("Mishra Delivery Pro")
                                        viewModel.onAuditorPackageNameChanged("com.mishra.delivery")
                                        viewModel.onAuditedCodeChanged("""
// Mishra Delivery App Core - Dangerous Manifest
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.mishra.delivery">
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <application android:label="Mishra Delivery">
        <!-- Collects raw geographic markers on launch with no legal notice prompt. -->
        <service android:name=".LocationUpdateService" android:exported="true" />
    </application>
</manifest>
                                        """.trimIndent())
                                    },
                                    label = { Text("🚚 Mishra Delivery") }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = appName == "PayWealth Finance",
                                    onClick = {
                                        viewModel.onAuditorAppNameChanged("PayWealth Finance")
                                        viewModel.onAuditorPackageNameChanged("com.paywealth.india")
                                        viewModel.onAuditedCodeChanged("""
// PayWealth Fintech - Sudden Forced Bulk Consent Agreement
fun startFintechFlow() {
    val termChecked = true // forces checked terms Agreement on startup
    val userNoticeAccepted = true
    // Under DPDP Act 2023, you grant us permission to cross-sell to 45 partner insurance brands,
    // share credit summaries, and you forfeit Sec 12 Erasure rights. Accept to log account.
    println("Bulk registration logged without discrete opt-in toggles.")
}
                                        """.trimIndent())
                                    },
                                    label = { Text("🪙 PayWealth Finance") }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = appName == "ToyPlay Gamer",
                                    onClick = {
                                        viewModel.onAuditorAppNameChanged("ToyPlay Gamer")
                                        viewModel.onAuditorPackageNameChanged("com.toyplay.kidsgame")
                                        viewModel.onAuditedCodeChanged("""
// ToyPlay Games - Intrusive Kids Analytics setup
class KidsAnalyticsCreator {
    fun runGamePlay(childUsername: String, age: Int) {
        if (age < 12) {
            // Begins logging behavioral tracking triggers for advertising SDKs without parental check
            println("Audible tracking active for child: " + childUsername)
        }
    }
}
                                        """.trimIndent())
                                    },
                                    label = { Text("🎮 ToyPlay Gamer") }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("App Manifest or Source Repository Material:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            
                            Button(
                                onClick = {
                                    simApkLoading = true
                                    viewModel.onAuditorAppNameChanged("Mishra Delivery Pro")
                                    viewModel.onAuditorPackageNameChanged("com.mishra.delivery")
                                    viewModel.onAuditedCodeChanged("""
// Scanned resources from downloaded APK: MishraDelivery_v3.2_release.apk
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.mishra.delivery">
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />
    <application>
        <service android:name=".BackgroundLocationProcessor" />
    </application>
</manifest>
                                    """.trimIndent())
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SIMULATE APK UPLOAD", fontSize = 10.sp)
                            }
                        }

                        if (simApkLoading) {
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(1000)
                                simApkLoading = false
                            }
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp))
                            Text("Reading APK bytecode & de-compiling Manifest structures...", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                        }

                        OutlinedTextField(
                            value = codeBuffer,
                            onValueChange = { viewModel.onAuditedCodeChanged(it) },
                            placeholder = { Text("// Paste XML AndroidManifest.xml, Kotlin, Java classes or specification JSON files here to scan.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .testTag("code_input_field"),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        )

                        Button(
                            onClick = { viewModel.triggerComplianceAudit() },
                            modifier = Modifier.fillMaxWidth().testTag("scan_code_button"),
                            enabled = !isAuditing
                        ) {
                            if (isAuditing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Assembling agent analysis modules...")
                            } else {
                                Icon(Icons.Filled.Search, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Run Dynamic Compliance Scan")
                            }
                        }
                    }
                }
            }

            if (result != null) {
                item {
                    val riskColor = when (result.riskLevel) {
                        "HIGH" -> MaterialTheme.colorScheme.error
                        "MEDIUM" -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                    
                    Card(
                        border = BorderStroke(2.dp, riskColor),
                        colors = CardDefaults.cardColors(containerColor = riskColor.copy(alpha = 0.04f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = riskColor)
                                    Text("Gemini Live Auditable Feed", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(riskColor)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "RISK: ${result.riskLevel}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text("Violated Statutory Sections: ${result.sectionsViolated}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = riskColor)
                            
                            Text(
                                text = result.rawOutput,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Manufactured DROP-IN WIPE CODING Block:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = result.wipeCode,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            val annotated = androidx.compose.ui.text.AnnotatedString(result.wipeCode)
                                            clipboardManager.setText(annotated)
                                        },
                                        modifier = Modifier.fillMaxWidth().testTag("copy_wipecode_btn")
                                    ) {
                                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Copy Dynamic Wipe-Code Fix")
                                    }
                                }
                            }

                            Button(
                                onClick = { viewModel.saveAuditToBacklog() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth().testTag("save_backlog_btn")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Commit Corrective Action to Task Backlog")
                            }
                        }
                    }
                }
            }

            // DEDICATED ENGINEERING PROMPT CARRIER INSTRUCTIONS (Psychological Assurance card)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("funnel_a_prompt_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                            Text("Code Audit Setup Instructions for Software Teams", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Text(
                            text = "To achieve solid legal protection in audit validations, use this pre-configured engineering prompt sequence inside your CI/CD linter pipelines or developer terminals:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "SECURE DEVELOPMENT PROMPT DIRECTIVE:\n\"Analyze target layout manifests and variables. Identify if ACCESS_FINE_LOCATION or READ_CONTACTS initializers are configured without parent notice frames or unbundled opt-in toggles. If found, automatically trigger the DPDPA Section 12 Wipe-Code erasure mechanism to neutralize unconsented storage. Retain audit telemetry only in secure, local SQLite layers with a strict 15-day purge countdown.\"",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

        } else {
            // FUNNEL B: COMPANY WEB POLICY & DIRECT SCRAPING AUDIT
            item {
                val userRole = viewModel.apifyUserRole.collectAsState().value
                val apiKey = viewModel.apifyApiKey.collectAsState().value
                val targetUrl = viewModel.scrapeUrl.collectAsState().value
                val activeActor = viewModel.selectedScraperActor.collectAsState().value
                val isScraping = viewModel.isScrapingInProgress.collectAsState().value
                
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("apify_scraper_card"),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Build, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Text("Apify Scraping Console (Appify Crawler Services)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        
                        Text(
                            text = "Crawls remote sites, documentation repositories, and privacy statements. Direct crawling pre-fills the Gemini Document analyzer below.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = userRole == "SHARED_POOL",
                                onClick = { viewModel.setApifyUserRole("SHARED_POOL") },
                                label = { Text("Shared Premium Pool", fontSize = 10.sp) },
                                modifier = Modifier.testTag("apify_shared_chip")
                            )
                            FilterChip(
                                selected = userRole == "CUSTOM_PRO",
                                onClick = { viewModel.setApifyUserRole("CUSTOM_PRO") },
                                label = { Text("PRO Account (Custom API Key)", fontSize = 10.sp) },
                                modifier = Modifier.testTag("apify_pro_chip")
                            )
                        }
                        
                        if (userRole == "CUSTOM_PRO") {
                            OutlinedTextField(
                                value = apiKey,
                                onValueChange = { viewModel.setApifyApiKey(it) },
                                label = { Text("Enter Apify API Token (e.g., ap_...)") },
                                placeholder = { Text("ap_vO7Z...") },
                                modifier = Modifier.fillMaxWidth().testTag("apify_token_input"),
                                singleLine = true,
                                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            )
                        }
                        
                        OutlinedTextField(
                            value = targetUrl,
                            onValueChange = { viewModel.setScrapeUrl(it) },
                            label = { Text("Target URL to Web Scrape / Ingest") },
                            modifier = Modifier.fillMaxWidth().testTag("apify_url_input"),
                            singleLine = true
                        )
                        
                        Text("Select Optimized Crawler Blueprint:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Cheerio Fast Parser", "Puppeteer JS Crawler", "Playwright PDF Scraper").forEach { actor ->
                                FilterChip(
                                    selected = activeActor == actor,
                                    onClick = { viewModel.setSelectedScraperActor(actor) },
                                    label = { Text(actor, fontSize = 9.sp) }
                                )
                            }
                        }
                        
                        Button(
                            onClick = { viewModel.runApifyScrape() },
                            modifier = Modifier.fillMaxWidth().testTag("apify_scrape_button"),
                            enabled = !isScraping
                        ) {
                            if (isScraping) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Web crawler running live fetch...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Launch Live Scraper & Crawl URL", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                val docText = viewModel.documentationText.collectAsState().value
                val isScanning = viewModel.isDocScanningInProgress.collectAsState().value
                val docResult = viewModel.docScanResult.collectAsState().value
                
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("doc_analyzer_card"),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Text("Gemini Specifications & Document Auditor", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        
                        Text(
                            text = "Paste project guidelines, software architecture drafts, or public policies. Gemini scans the material to flag non-compliance liabilities before production deployments.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        OutlinedTextField(
                            value = docText,
                            onValueChange = { viewModel.onDocumentationTextChanged(it) },
                            label = { Text("Project Specifications or Privacy notice") },
                            placeholder = { Text("// Paste documentation or scrape remote text to analyze compliance gaps under the Act.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .testTag("doc_text_input"),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        )
                        
                        Button(
                            onClick = { viewModel.runDocumentationScan() },
                            modifier = Modifier.fillMaxWidth().testTag("scan_doc_button"),
                            enabled = !isScanning
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gemini scanning specifications...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Audit Document Compliance Gaps", fontSize = 12.sp)
                            }
                        }
                        
                        if (docResult != null) {
                            val docRiskColor = when (docResult.riskLevel) {
                                "HIGH" -> MaterialTheme.colorScheme.error
                                "MEDIUM" -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.primary
                            }
                            
                            Card(
                                colors = CardDefaults.cardColors(containerColor = docRiskColor.copy(alpha = 0.05f)),
                                border = BorderStroke(2.dp, docRiskColor),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("DOCUMENT AUDIT RESULTS REPORT", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = docRiskColor)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(docRiskColor)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("RISK: ${docResult.riskLevel}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    
                                    Text("Sections Violated: ${docResult.sectionsViolated}", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = docRiskColor)
                                    
                                    Text(
                                        text = docResult.rawOutput,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    if (docResult.wipeCode.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("RECOMMENDED REPAIR POLICY CLAUSE (Wipe Clause):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = docResult.wipeCode,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // DEDICATED LEGAL COUNSEL PROMPT CARRIER INSTRUCTIONS (Psychological Assurance card)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("funnel_b_prompt_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                            Text("Notice Drafting Setup Instructions for Legal Counsel", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Text(
                            text = "To guarantee bulletproof defense against Ministry complaints and DPDPA penalties, use this professional counsel instructions prompt in your document generation portals:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "LEGAL COUNCIL PROMPT DIRECTIVE:\n\"Audit scraped website disclosures. Ensure Section 5 notice mandates are unbundled with absolute option arrays in local languages. Mandate fully itemized nominee details (Section 10) and clear Designated Grievance Officer details showing active resolution timers of under 72 hours. Produce fully integrated compliance addendum clauses incorporating structural erasure guarantees (Section 12).\"",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 6: Persistent Engineering Compliance Backlog checklist
        item {
            Text(
                "Engineering SQLite Compliance Backlog",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (auditTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    )
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(42.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Compliance check backlog is completely clear!", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Run audits on apps and hit 'Commit Corrective Action' to write direct repair plans here.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        } else {
            items(auditTasks, key = { it.id }) { task ->
                val taskRiskColor = when (task.riskLevel) {
                    "HIGH" -> MaterialTheme.colorScheme.error
                    "MEDIUM" -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (task.isResolved) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) 
                                         else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = if (task.isResolved) 1.dp else 2.dp,
                        color = if (task.isResolved) MaterialTheme.colorScheme.outlineVariant else taskRiskColor
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = task.appName,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (task.isResolved) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(taskRiskColor.copy(alpha = 0.1f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(task.riskLevel, color = taskRiskColor, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                    }
                                }
                                Text(
                                    text = "Package: ${task.packageName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = { viewModel.deleteTask(task) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Task", tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        Text("Gaps Triggered: ${task.sectionsViolated}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = taskRiskColor)
                        Text(task.violationSummary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Sub-coding Fix Outline:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text(task.semiCodeInstructions, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    val annotated = androidx.compose.ui.text.AnnotatedString(task.wipeCode)
                                    clipboardManager.setText(annotated)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("COPY WIPE-CODE", fontSize = 10.sp)
                            }

                            Button(
                                onClick = { viewModel.toggleTaskResolution(task) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (task.isResolved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = if (task.isResolved) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (task.isResolved) "RESOLVED" else "RESOLVE TASK", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// TAB 2: TECHNICAL & LEGAL SPECIFICATIONS
// ==========================================
@Composable
fun SettingsTab() {
    var selectedSpecDoc by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Settings & Resources",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "Access compliance reference material and Data Request templates.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ScrollableTabRow(
            selectedTabIndex = selectedSpecDoc,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            edgePadding = 0.dp,
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
        ) {
            Tab(selected = selectedSpecDoc == 0, onClick = { selectedSpecDoc = 0 }) {
                Text("PRD Document", fontSize = 11.sp, modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedSpecDoc == 1, onClick = { selectedSpecDoc = 1 }) {
                Text("Technical Arch", fontSize = 11.sp, modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedSpecDoc == 2, onClick = { selectedSpecDoc = 2 }) {
                Text("Security & Auth", fontSize = 11.sp, modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedSpecDoc == 3, onClick = { selectedSpecDoc = 3 }) {
                Text("Frontend Spec", fontSize = 11.sp, modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedSpecDoc == 4, onClick = { selectedSpecDoc = 4 }) {
                Text("Feature Tickets", fontSize = 11.sp, modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedSpecDoc == 5, onClick = { selectedSpecDoc = 5 }) {
                Text("Data Requests", fontSize = 11.sp, modifier = Modifier.padding(12.dp))
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (selectedSpecDoc == 0) {
                    item { Text(ComplianceDocs.prd, style = MaterialTheme.typography.bodySmall) }
                } else if (selectedSpecDoc == 1) {
                    item { Text(ComplianceDocs.technicalArch, style = MaterialTheme.typography.bodySmall) }
                } else if (selectedSpecDoc == 2) {
                    item { Text(ComplianceDocs.securityAccess, style = MaterialTheme.typography.bodySmall) }
                } else if (selectedSpecDoc == 3) {
                    item { Text(ComplianceDocs.frontendSpec, style = MaterialTheme.typography.bodySmall) }
                } else if (selectedSpecDoc == 4) {
                    // Just a placeholder
                    item { Text("Feature Tickets Content here") }
                } else if (selectedSpecDoc == 5) {
                    item {
                        Text("Data Principal Access Request (DPAR) Templates", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Under DPDP Act 2023, Data Principals have the right to request a summary of their personal data and the identities of all Data Fiduciaries with whom it has been shared. Use these templates to respond promptly.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Template: Right to Information Request Approval", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Dear [User Name],\n\nIn response to your request dated [Date], please find attached a comprehensive summary of the personal data we currently process about you, as required under Section 11 of the DPDP Act 2023.\n\nWe have also included a list of third-party Data Fiduciaries with whom your data has been shared, along with the specific categories of data shared.\n\nRegards,\nData Protection Officer", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Template: Right to Erasure Confirmation", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Dear [User Name],\n\nWe confirm that your request for erasure of personal data under Section 12 of the DPDP Act 2023 has been processed.\n\nYour data has been successfully permanently deleted from our primary servers and all active third-party processor instances. A cryptographic hash confirming the deletion is attached to this email.\n\nRegards,\nData Protection Officer", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 3: AUDITS & SYSTEM LEDGERS
// ==========================================
@Composable
fun ConsentManagementTab(
    profile: UserProfile?,
    nominee: Nominee?,
    logs: List<ConsentLog>,
    reports: List<GrievanceReport>,
    hashedPhone: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text("Consent Management", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("View user consent statuses in a grid layout", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Consent Overview Grid", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("USER ID", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 12.sp)
                        Text("STATUS", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 12.sp)
                        Text("DATA TYPE", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 12.sp)
                        Text("ACTION", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 12.sp)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    val dummyConsents = listOf(
                        Triple("USR-9921", "Active", "Profile, Location"),
                        Triple("USR-1044", "Revoked", "Contact Info"),
                        Triple("USR-2253", "Active", "Health Data")
                    )
                    
                    dummyConsents.forEach { (id, status, type) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(id, modifier = Modifier.weight(1f), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            Text(status, modifier = Modifier.weight(1f), fontSize = 12.sp, color = if (status == "Active") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            Text(type, modifier = Modifier.weight(1f), fontSize = 12.sp)
                            TextButton(onClick = { /* Handle action */ }, modifier = Modifier.weight(1f)) {
                                Text("Manage", fontSize = 12.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Text(
                "Consent Cryptographic Ledger",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (logs.isEmpty()) {
            item {
                Text(
                    "No ledger entries yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(logs) { logEntry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val actionColor = if (logEntry.action == "GRANTED" || logEntry.action == "UPDATED") MaterialTheme.colorScheme.primary else if (logEntry.action == "REVOKED" || logEntry.action == "ERASED") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                            Text(
                                text = logEntry.action,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = actionColor,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(actionColor.copy(alpha = 0.1f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Text(
                                text = SimpleDateFormat("dd MMM yy, HH:mm", Locale.getDefault()).format(Date(logEntry.timestamp)),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = logEntry.consentType,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = logEntry.details,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
