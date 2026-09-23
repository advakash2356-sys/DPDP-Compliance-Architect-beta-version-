package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ComplianceViewModel

@Composable
fun IntakeFormCard(
    viewModel: ComplianceViewModel,
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(1) } // 1 = Choose Funnel, 2 = Asset Details, 3 = Interactive Checklist, 4 = Certified Success
    var selectedFunnel by remember { mutableStateOf("") } // "API" or "WEBSITE"

    // Funnel A: API Integration Form States
    var apiAppName by remember { mutableStateOf("Mishra Delivery Pro") }
    var apiPackageName by remember { mutableStateOf("com.mishra.delivery") }
    var apiTargetSdk by remember { mutableStateOf("Android 14 (API 34)") }

    // Funnel B: Website Audit Form States
    var webUrl by remember { mutableStateOf("https://meity.gov.in/content/digital-personal-data-protection-act-2023") }
    var webCategory by remember { mutableStateOf("E-Commerce Portal") }

    // Sequenced Checklist completion state - strictly toddler-proof (must check in sequence!)
    var apiChecklist by remember { mutableStateOf(listOf(false, false, false, false)) }
    var webChecklist by remember { mutableStateOf(listOf(false, false, false, false)) }

    val activeStepText = when (currentStep) {
        1 -> "⚡ STEP 1: CHOOSE YOUR STATUTORY SHIELD CHANNEL"
        2 -> "📝 STEP 2: DEFINE YOUR AUDIT TARGETS"
        3 -> "📋 STEP 3: PERFORM IDIOT-PROOF GUIDED IMMUNITY CHECKS"
        else -> "🏆 STEP 4: GENERATED ACTIVE DPDPA COMPLIANCE IMMUNITY"
    }

    var showTooltip by remember { mutableStateOf<String?>(null) }
    
    if (showTooltip != null) {
        AlertDialog(
            onDismissRequest = { showTooltip = null },
            icon = { Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Plain English Explanation", fontWeight = FontWeight.Bold) },
            text = { Text(showTooltip!!) },
            confirmButton = {
                TextButton(onClick = { showTooltip = null }) {
                    Text("Got it!", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Feature 3: Visual Progress Tracker with sweet spring animation
    val animatedProgress by animateFloatAsState(
        targetValue = currentStep.toFloat() / 4f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "ProgressTracker"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("multi_step_guided_intake_form"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // High-Contrast Interactive HUD Header with sequential dots
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🛡️ IMMUNITY SHIELD INTAKE PIPELINE",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Feature 2: Hover-tooltip trigger for the whole pipeline
                            IconButton(
                                onClick = { 
                                    showTooltip = "Simply put: This tool walks you through making your app/website bulletproof against local internet laws step-by-step. No lawyers required." 
                                },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(Icons.Filled.Info, contentDescription = "Help", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                        Text(
                            text = activeStepText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    // Numeric Step badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "STEP $currentStep OF 4",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Outer sequential visual pipeline progress bar (animated)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = animatedProgress)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "IntakeStepsAnimation"
                ) { step ->
                    when (step) {
                        1 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Are you deploying/integrating a backend API channel or auditing a live public website for legal compliance?",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { 
                                            showTooltip = "Simply put: Are we checking the secret code behind your mobile app (API Integration)? Or are we checking the pages people read on the internet (Website Audit)?" 
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Filled.Info, contentDescription = "Help", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Funnel A: API Integration option card
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedFunnel = "API"
                                                currentStep = 2
                                            }
                                            .border(
                                                width = if (selectedFunnel == "API") 3.dp else 1.dp,
                                                color = if (selectedFunnel == "API") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .testTag("select_api_funnel_card"),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedFunnel == "API") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) 
                                                             else MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Share,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            
                                            Text(
                                                text = "API INTEGRATION",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Center
                                            )
                                            
                                            Text(
                                                text = "SDK telemetry, unbundled keyscopes & secure backend token callbacks",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center,
                                                minLines = 3
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(MaterialTheme.colorScheme.primary)
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "CHOOSE API",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }

                                    // Funnel B: Website Audit option card
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedFunnel = "WEBSITE"
                                                currentStep = 2
                                            }
                                            .border(
                                                width = if (selectedFunnel == "WEBSITE") 3.dp else 1.dp,
                                                color = if (selectedFunnel == "WEBSITE") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .testTag("select_web_funnel_card"),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedFunnel == "WEBSITE") MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                                                             else MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Build,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.secondary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            
                                            Text(
                                                text = "WEBSITE AUDIT",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.secondary,
                                                textAlign = TextAlign.Center
                                            )
                                            
                                            Text(
                                                text = "Notice pages, privacy rules, crawl details & dynamic cookie banner disclosure checks",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center,
                                                minLines = 3
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(MaterialTheme.colorScheme.secondary)
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "CHOOSE WEBPAGE",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Configure details for your targeted active legal insulation scan so we can customized appropriate sequential checkboxes:",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { 
                                            showTooltip = "Simply put: Tell us the exact name of your App or Website so we know what we are protecting. This helps auto-fill the legal paperwork."
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Filled.Info, contentDescription = "Help", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                if (selectedFunnel == "API") {
                                    // Render API input form fields
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = apiAppName,
                                            onValueChange = { apiAppName = it },
                                            label = { Text("Application Custom Title") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("api_app_name_input"),
                                            placeholder = { Text("e.g. PayWealth Fintech") }
                                        )
                                        OutlinedTextField(
                                            value = apiPackageName,
                                            onValueChange = { apiPackageName = it },
                                            label = { Text("App Package Identifier") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("api_package_name_input"),
                                            placeholder = { Text("e.g. com.paywealth.india") }
                                        )
                                        OutlinedTextField(
                                            value = apiTargetSdk,
                                            onValueChange = { apiTargetSdk = it },
                                            label = { Text("Target Deployment Model") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("api_target_sdk_input")
                                        )
                                    }
                                } else {
                                    // Website scan details
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = webUrl,
                                            onValueChange = { webUrl = it },
                                            label = { Text("Privacy Policy / Webpage Domain URL") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("web_url_input")
                                        )
                                        OutlinedTextField(
                                            value = webCategory,
                                            onValueChange = { webCategory = it },
                                            label = { Text("Business Domain Niche") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("web_category_input"),
                                            placeholder = { Text("e.g. Health-Tech App, SaaS Enterprise") }
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TextButton(onClick = { currentStep = 1 }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Re-Select Channel")
                                    }

                                    Button(
                                        onClick = { currentStep = 3 },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text("Forward to Audits")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                    }
                                }
                            }
                        }

                        3 -> {
                            val activeList = if (selectedFunnel == "API") apiChecklist else webChecklist
                            val checklistLabels = if (selectedFunnel == "API") {
                                listOf(
                                    "Isolate SDK data parameters safely into separate consent logs",
                                    "Encrypt sensitive payload keys prior to sync/cache writes",
                                    "Build robust 1-tap user opt-out callbacks inside account settings",
                                    "Pre-verify unbundled consent disclosures on app opening states"
                                )
                            } else {
                                listOf(
                                    "Publish visible disclaimer notice in English, Hindi & localized terms",
                                    "Provide clear contact email of active Grievance Responding Officer",
                                    "Construct a responsive Cookie Banner callback popup script",
                                    "Remove redundant trackers & force strict SSL transport security"
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(
                                        text = "👉 STRICT SEQUENCE DIRECTIVE: Tap fields in precise order starting from step 1 back-to-back to generate statutory insulation proof. Even a toddler can complete this!",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { 
                                            showTooltip = "Simply put: Cross these items off one-by-one to officially log that you've completed your legal homework. Once all boxes are checked, you are legally protected."
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Filled.Info, contentDescription = "Help", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    checklistLabels.forEachIndexed { index, label ->
                                        // Toddler-proof execution: checklist items must be checked sequentially!
                                        // You can check index `i` only if indices `< i` are checked.
                                        val isPreviousChecked = index == 0 || activeList[index - 1]
                                        val isChecked = activeList[index]

                                        // Highlight current active step to guide user
                                        val isHighlighted = isPreviousChecked && !isChecked
                                        
                                        val cardBorderColor = if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) 
                                                             else if (isHighlighted) MaterialTheme.colorScheme.primary 
                                                             else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        
                                        val cardBg = if (isChecked) MaterialTheme.colorScheme.primaryContainer 
                                                     else if (isHighlighted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) 
                                                     else Color.Transparent

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(cardBg)
                                                .border(1.5.dp, cardBorderColor, RoundedCornerShape(8.dp))
                                                .clickable(enabled = isPreviousChecked) {
                                                    val newList = activeList.toMutableList()
                                                    newList[index] = !newList[index]
                                                    
                                                    // If unchecking, uncheck all subsequent items to preserve sequential pattern
                                                    if (!newList[index]) {
                                                        for (laterIdx in (index + 1)..3) {
                                                            newList[laterIdx] = false
                                                        }
                                                    }
                                                    
                                                    if (selectedFunnel == "API") {
                                                        apiChecklist = newList
                                                    } else {
                                                        webChecklist = newList
                                                    }
                                                }
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            // Circular indicator badge
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (isChecked) MaterialTheme.colorScheme.primary 
                                                        else if (isHighlighted) MaterialTheme.colorScheme.primary 
                                                        else MaterialTheme.colorScheme.outlineVariant
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isChecked) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                } else {
                                                    Text(
                                                        text = (index + 1).toString(),
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "CLAUSE CHECK ${index + 1}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp,
                                                    color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = label,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isChecked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = null, // Controlled by row click
                                                enabled = isPreviousChecked,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = { currentStep = 2 }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Change Inputs")
                                    }

                                    val listAllCompiled = activeList.all { it }

                                    Button(
                                        onClick = { 
                                            if (listAllCompiled) {
                                                currentStep = 4 
                                            }
                                        },
                                        enabled = listAllCompiled,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            disabledContainerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Text("Submit Compliance Proof (+500 XP)")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        else -> {
                            val badgeTitle = if (selectedFunnel == "API") "🛡️ MASTER API SHIELD OF 2026" else "🌐 SUPREME WEB COMPLIANCE SENTINEL"
                            val rewardText = if (selectedFunnel == "API") {
                                "The specified API structure for $apiAppName ($apiPackageName) running on $apiTargetSdk has been cleared and checked against DPDP Section 6 obligations."
                            } else {
                                "The public-facing terms at $webUrl in the $webCategory niche have been parsed of systemic regulatory liabilities."
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(32.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = badgeTitle,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { showTooltip = "Simply put: You've officially secured your project! It is now recorded that you successfully implemented all required government data safety checks." },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(Icons.Filled.Info, contentDescription = "Help", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "REGULATORY IMMUNITY STATUS: CERTIFIED PASS",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = rewardText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }

                                Text(
                                    text = "Ready to submit these inputs back into the AI Agentic Auditing engine for automatic patch file verification? Choose a dispatch target below to instantly populate your scanner configurations:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            // Prepopulate Gemini auditor states for API Scan
                                            if (selectedFunnel == "API") {
                                                viewModel.onAuditorAppNameChanged(apiAppName)
                                                viewModel.onAuditorPackageNameChanged(apiPackageName)
                                                viewModel.onAuditedCodeChanged("""
// Mishra Delivery App Core - Dangerous Manifest Location Exposure
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="$apiPackageName">
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />
    <application android:label="$apiAppName">
        <service android:name=".LocationUpdateService" android:exported="true" />
    </application>
</manifest>
                                                """.trimIndent())
                                            } else {
                                                viewModel.onAuditorAppNameChanged("Crawl Web Portal")
                                                viewModel.onAuditorPackageNameChanged("org.web.crawler")
                                            }
                                            
                                            // Reset intake step & switch tab to "AI Agent Auditor" (tab index 2)
                                            currentStep = 1
                                            apiChecklist = listOf(false, false, false, false)
                                            webChecklist = listOf(false, false, false, false)
                                            onNavigateToTab(2)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Run Build Audit Scan", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = {
                                            // Reset intake step & switch tab to "Specifications" (tab index 3)
                                            currentStep = 1
                                            apiChecklist = listOf(false, false, false, false)
                                            webChecklist = listOf(false, false, false, false)
                                            onNavigateToTab(3)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary
                                        )
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rule Specifications", fontSize = 11.sp)
                                    }
                                }

                                TextButton(
                                    onClick = { 
                                        currentStep = 1
                                        apiChecklist = listOf(false, false, false, false)
                                        webChecklist = listOf(false, false, false, false)
                                    }
                                ) {
                                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Re-Run Intake Pipeline", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
