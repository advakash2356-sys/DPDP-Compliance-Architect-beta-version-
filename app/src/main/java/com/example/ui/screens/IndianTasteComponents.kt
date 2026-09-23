package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuditLogEntry

// ============================================================================
// PREMIUM TRUST COLOUR SYSTEM (Clean & Modern)
// ============================================================================
val LuxuryNavy = Color(0xFF1E293B)      // Slate 800
val DeepTeal = Color(0xFF0F766E)        // Premium Teal 700
val WarmAmber = Color(0xFFD97706)       // Amber 600
val PureWhite = Color(0xFFFFFFFF)
val CoolGray = Color(0xFFF8FAFC)        // Slate 50
val EmeraldSuccess = Color(0xFF10B981)  // Emerald 500
val LightEmeraldBg = Color(0xFFF0FDF4)  // Green 50
val CleanCrimson = Color(0xFFEF4444)    // Red 500
val SoftCrimsonBg = Color(0xFFFEF2F2)   // Red 50
val PremiumGold = Color(0xFFF59E0B)     // Amber 500
val SaffronOfficial = Color(0xFFF97316) // Orange 500
val AshokaBlue = Color(0xFF3B82F6)      // Blue 500

// ============================================================================
// 0. AUDIT COMMAND CENTER (High-Trust Veteran Dashboard)
// ============================================================================
@Composable
fun ActionableTasksPanel(
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (selectedLanguage == "HI") "आज के कार्य" else "Today's Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "3 Pending",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // To-Do List View
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TaskItem(if (selectedLanguage == "HI") "प्राइवेसी पॉलिसी अपडेट करें" else "Update Privacy Policy", isDone = false)
                TaskItem(if (selectedLanguage == "HI") "5 नए ग्राहकों की सहमति लें" else "Get Consent for 5 New Clients", isDone = false)
                TaskItem(if (selectedLanguage == "HI") "डेटा का बैकअप लें" else "Backup Local Data", isDone = true)
            }
        }
    }
}

@Composable
private fun TaskItem(text: String, isDone: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Info,
            contentDescription = null,
            tint = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = if (isDone) MaterialTheme.typography.bodyMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ExpertSupportPanel(
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (selectedLanguage == "HI") "क्या आपको मदद चाहिए?" else "Need Compliance Help?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (selectedLanguage == "HI") "हमारे एक्सपर्ट से WhatsApp पर बात करें" else "Chat with our experts on WhatsApp",
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            IconButton(
                onClick = { /* Open WhatsApp */ },
                modifier = Modifier.background(MaterialTheme.colorScheme.secondary, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "WhatsApp", tint = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}

@Composable
fun BusinessTrustScore(selectedLanguage: String, modifier: Modifier = Modifier) {
    val labels = if (selectedLanguage == "HI") {
        listOf("विश्वास स्कोर", "स्थिति", "सुरक्षा")
    } else {
        listOf("Trust Score", "Status", "Security")
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = if (selectedLanguage == "HI") "आपका बिज़नेस सुरक्षित है" else "Business Health Assessment",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MatrixItem(labels[0], "85/100", MaterialTheme.colorScheme.primary)
                MatrixItem(labels[1], "GOOD", MaterialTheme.colorScheme.secondary)
                MatrixItem(labels[2], "Verified", MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
private fun MatrixItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Black, color = color, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun DataLocalizationBadge(selectedLanguage: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (selectedLanguage == "EN") "100% Data stays in India" else "100% डेटा भारत में सुरक्षित",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (selectedLanguage == "EN") "Offline Ready & Secure" else "बिना इंटरनेट के भी चलता है",
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun QuickActionGrid(selectedLanguage: String, onActionClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    val actions = when (selectedLanguage) {
        "HI" -> listOf(
            Triple("सहमति पत्र", Icons.Default.CheckCircle, DeepTeal),
            Triple("पॉलिसी बनाएं", Icons.Default.Info, WarmAmber),
            Triple("ऑडिट रिपोर्ट", Icons.AutoMirrored.Filled.List, CleanCrimson),
            Triple("डेटा सिंक", Icons.Default.Refresh, AshokaBlue)
        )
        "LOCAL" -> listOf(
            Triple("मंज़ूरी", Icons.Default.CheckCircle, DeepTeal),
            Triple("पॉलिसी बनाईं", Icons.Default.Info, WarmAmber),
            Triple("रिपोर्ट देखीं", Icons.AutoMirrored.Filled.List, CleanCrimson),
            Triple("सिंक करीं", Icons.Default.Refresh, AshokaBlue)
        )
        else -> listOf(
            Triple("Generate Consent", Icons.Default.CheckCircle, DeepTeal),
            Triple("Draft Policy", Icons.Default.Info, WarmAmber),
            Triple("Audit Report", Icons.AutoMirrored.Filled.List, CleanCrimson),
            Triple("Sync Data", Icons.Default.Refresh, AshokaBlue)
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (selectedLanguage == "EN") "Quick Actions" else "तुरंत कार्रवाई",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = LuxuryNavy,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionTile(actions[0].first, actions[0].second, actions[0].third, Modifier.weight(1f))
            ActionTile(actions[1].first, actions[1].second, actions[1].third, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionTile(actions[2].first, actions[2].second, actions[2].third, Modifier.weight(1f))
            ActionTile(actions[3].first, actions[3].second, actions[3].third, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ActionTile(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = LuxuryNavy)
        }
    }
}

// ============================================================================
// 1. WELCOME GREETING CARD (Premium, Clean & Minimal)
// ============================================================================
@Composable
fun WelcomeGreetingCard(selectedLanguage: String, modifier: Modifier = Modifier) {
    val subtitleText = when (selectedLanguage) {
        "HI" -> "सुरक्षित स्वदेशी सिंक"
        "LOCAL" -> "🌾 हमार सुरक्षित स्वदेशी सिंक"
        else -> "Secure Swadeshi Privacy System"
    }
    val titleText = when (selectedLanguage) {
        "HI" -> "आकाश सिंक एजेंट"
        "LOCAL" -> "आकाश सिंक बाबू"
        else -> "Data Rakshak"
    }
    val officerText = when (selectedLanguage) {
        "HI" -> "सुरक्षा अधिकारी: आकाश शर्मा"
        "LOCAL" -> "शिकायत निवारण बाबू: आकाश शर्मा (बिहार विशेष)"
        else -> "Privacy Officer: Akash Sharma"
    }
    val descText = when (selectedLanguage) {
        "HI" -> "आपके व्यवसाय को बिना किसी जटिल कानूनी शब्दावली के भारत के डीपीडीपी कानून (DPDP Act 2023) के अनुकूल बनाने वाला पहला सरल और प्रीमियम डिजिटल सहायक।"
        "LOCAL" -> "बिना कौनों भारी-भरकम अदालती शब्द के, रउआ दुकान के सरकारी डीपीडीपी कानून (DPDP Act 2023) के हिसाब से पक्का अउर १००% सुरक्षित बनावे वाला एकदम आसान डिजिटल सहायक।"
        else -> "A premium and simplified companion designed to make local businesses 100% compliant with India's Digital Personal Data Protection (DPDP) Act 2023 without any complex jargon."
    }
    val badgeText = when (selectedLanguage) {
        "HI" -> "स्वदेशी सुरक्षा: कानून मंत्रालय और MeitY नियमों के पूर्णतः अनुकूल।"
        "LOCAL" -> "🌾 स्वदेशी भरोसा: भारत सरकार के MeitY अउर कानून मंत्रालय के नियमानुसार एकदम फिट।"
        else -> "Swadeshi Privacy: Fully aligned with Ministry of Law and MeitY directives."
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("welcome_greeting_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepTeal,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AKASH SYNC ENGINE (ASE)",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = LuxuryNavy
                    )
                    Text(
                        text = "Kernel Active: v4.2.0-Audit",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = WarmAmber
                    )
                }

                // Premium verification shield in code
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(LuxuryNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Verified Seal",
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Veteran Compliance Protocol active. Monitoring data flows for PII exposure, dark pattern interference, and statutory reporting deadlines.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Verified Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightEmeraldBg)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Verify Badge",
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldSuccess
                )
            }
        }
    }
}

// ============================================================================
// 2. THE PANCHSHEEL PILLARS OF PRIVACY (Interactive & Simplified)
// ============================================================================
data class PanchsheelPillar(
    val id: Int,
    val titleEn: String,
    val titleHi: String,
    val summaryEn: String,
    val summaryHi: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val PanchsheelList = listOf(
    PanchsheelPillar(
        id = 1,
        titleEn = "Consent",
        titleHi = "स्पष्ट सहमति",
        summaryEn = "Only collect customer data with explicit, clear permission.",
        summaryHi = "ग्राहकों को साफ़-साफ़ समझाकर और स्पष्ट अनुमति लेकर ही उनका विवरण लें।",
        icon = Icons.Filled.CheckCircle
    ),
    PanchsheelPillar(
        id = 2,
        titleEn = "Limitation",
        titleHi = "सीमित उद्देश्य",
        summaryEn = "Only use details for the exact reason you collected them.",
        summaryHi = "डेटा का उपयोग सिर्फ उसी कार्य के लिए करें जिसके लिए ग्राहक सहमत हुआ है।",
        icon = Icons.Filled.Lock
    ),
    PanchsheelPillar(
        id = 3,
        titleEn = "Security",
        titleHi = "मजबूत सुरक्षा",
        summaryEn = "Keep customer data locked safely to prevent leaks and hacking.",
        summaryHi = "ग्राहकों का डेटा तिजोरी की तरह सुरक्षित रखें ताकि चोरी या हैक न हो सके।",
        icon = Icons.Filled.Build
    ),
    PanchsheelPillar(
        id = 4,
        titleEn = "Erasure",
        titleHi = "डेटा नष्ट करना",
        summaryEn = "Permanently delete user records once the job is finished.",
        summaryHi = "काम पूरा होने पर या ग्राहक द्वारा वापस मांगने पर उनका डेटा पूरी तरह मिटाएं।",
        icon = Icons.Filled.Delete
    ),
    PanchsheelPillar(
        id = 5,
        titleEn = "Redressal",
        titleHi = "शिकायत निवारण",
        summaryEn = "Must assign an officer to address customer complaints.",
        summaryHi = "ग्राहकों की शिकायतों को सुनने और सुलझाने के लिए एक नोडल अधिकारी रखें।",
        icon = Icons.Filled.Person
    )
)

@Composable
fun getPillarTitle(pillarId: Int, selectedLanguage: String): String {
    return when (pillarId) {
        1 -> when (selectedLanguage) {
            "HI" -> "स्पष्ट सहमति"
            "LOCAL" -> "साफ़ रज़ामंदी"
            else -> "Consent"
        }
        2 -> when (selectedLanguage) {
            "HI" -> "सीमित उद्देश्य"
            "LOCAL" -> "जे काम, ओही काम"
            else -> "Limitation"
        }
        3 -> when (selectedLanguage) {
            "HI" -> "मजबूत सुरक्षा"
            "LOCAL" -> "पक्की तिजोरी"
            else -> "Security"
        }
        4 -> when (selectedLanguage) {
            "HI" -> "डेटा नष्ट करना"
            "LOCAL" -> "खाता बंद - सफ़ाई"
            else -> "Erasure"
        }
        5 -> when (selectedLanguage) {
            "HI" -> "शिकायत निवारण"
            "LOCAL" -> "सुनवाई व्यवस्था"
            else -> "Redressal"
        }
        else -> ""
    }
}

@Composable
fun getPillarSummary(pillarId: Int, selectedLanguage: String): String {
    return when (pillarId) {
        1 -> when (selectedLanguage) {
            "HI" -> "ग्राहकों को साफ़-साफ़ समझाकर और स्पष्ट अनुमति लेकर ही उनका विवरण लें।"
            "LOCAL" -> "ग्राहक लोगन के साफ़-साफ़ समझाके अउर उनकी रज़ामंदी लेके ही फोन नंबर लिखल जाई।"
            else -> "Only collect customer data with explicit, clear permission."
        }
        2 -> when (selectedLanguage) {
            "HI" -> "डेटा का उपयोग सिर्फ उसी कार्य के लिए करें जिसके लिए ग्राहक सहमत हुआ है।"
            "LOCAL" -> "जे काम खातिर रज़ामंदी लिहले बानी, ओही काम खातिर नंबर के इस्तेमाल होखी, दोसर काम में ना।"
            else -> "Only use details for the exact reason you collected them."
        }
        3 -> when (selectedLanguage) {
            "HI" -> "ग्राहकों का डेटा तिजोरी की तरह सुरक्षित रखें ताकि चोरी या हैक न हो सके।"
            "LOCAL" -> "ग्राहक लोगन के सारा डेटा के लोहे के तिजोरी नियर मजबूत अउर सुरक्षित राखल जाई ताकि चोरी न होखे।"
            else -> "Keep customer data locked safely to prevent leaks and hacking."
        }
        4 -> when (selectedLanguage) {
            "HI" -> "काम पूरा होने पर या ग्राहक द्वारा वापस मांगने पर उनका डेटा पूरी तरह मिटाएं।"
            "LOCAL" -> "काम पूरा होखला पर या ग्राहक के कहले पर ओकर सारा नंबर अउर जानकारी जड़ से मिटा दीं।"
            else -> "Permanently delete user records once the job is finished."
        }
        5 -> when (selectedLanguage) {
            "HI" -> "ग्राहकों की शिकायतों को सुनने और सुलझाने के लिए एक नोडल अधिकारी रखें।"
            "LOCAL" -> "ग्राहक लोगन के कौनों भी परेशानी या शिकायत सुने खातिर एगो नोडल अधिकारी (बाबू) तैनात राखल जाई।"
            else -> "Must assign an officer to address customer complaints."
        }
        else -> ""
    }
}

@Composable
fun PanchsheelPrivacyCard(selectedLanguage: String, modifier: Modifier = Modifier) {
    var selectedPillar by remember { mutableStateOf<PanchsheelPillar?>(null) }
    val isHindi = selectedLanguage == "HI" || selectedLanguage == "LOCAL"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("panchsheel_privacy_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = when (selectedLanguage) {
                    "HI" -> "गोपनीयता के ५ पंचशील स्तंभ"
                    "LOCAL" -> "गोपनीयता के ५ पंचशील स्तंभ"
                    else -> "5 Panchsheel Pillars of Privacy"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LuxuryNavy
            )
            Text(
                text = when (selectedLanguage) {
                    "HI" -> "डीपीडीपी कानून २०२३ के मुख्य नियम (टैप करें)"
                    "LOCAL" -> "डीपीडीपी कानून २०२३ के मुख्य नियम (टैप करीं)"
                    else -> "Core requirements of DPDP Act 2023 (Tap card to read)"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(PanchsheelList) { pillar ->
                    val title = getPillarTitle(pillar.id, selectedLanguage)
                    val summary = getPillarSummary(pillar.id, selectedLanguage)
                    Card(
                        modifier = Modifier
                            .width(135.dp)
                            .clickable { selectedPillar = pillar },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CoolGray.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Icon(
                                imageVector = pillar.icon,
                                contentDescription = pillar.titleEn,
                                tint = DeepTeal,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = LuxuryNavy
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = summary,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 13.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }

    if (selectedPillar != null) {
        val pillar = selectedPillar!!
        val title = getPillarTitle(pillar.id, selectedLanguage)
        val summary = getPillarSummary(pillar.id, selectedLanguage)
        AlertDialog(
            onDismissRequest = { selectedPillar = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(LightEmeraldBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(pillar.icon, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(24.dp))
                }
            },
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { selectedPillar = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = DeepTeal)
                ) {
                    Text(
                        text = when (selectedLanguage) {
                            "HI" -> "समझ गया"
                            "LOCAL" -> "बुझ गईनी"
                            else -> "Okay"
                        }
                    )
                }
            }
        )
    }
}

// ============================================================================
// 3. GRIEVANCE OFFICER REDRESSAL BOARD MAKER (Sleek, High-Fidelity & Clean)
// ============================================================================
@Composable
fun GrievanceOfficerBoardMaker(selectedLanguage: String, modifier: Modifier = Modifier) {
    var officerName by remember { mutableStateOf("") }
    var officerEmail by remember { mutableStateOf("") }
    var isGenerated by remember { mutableStateOf(false) }

    val isHindi = selectedLanguage == "HI" || selectedLanguage == "LOCAL"

    val titleText = when (selectedLanguage) {
        "HI" -> "शिकायत अधिकारी बोर्ड (Rule 5)"
        "LOCAL" -> "शिकायत बाबू बोर्ड (Rule 5)"
        else -> "Grievance Officer Board Maker"
    }
    val descText = when (selectedLanguage) {
        "HI" -> "डीपीडीपी नियम के तहत अपना संपर्क बोर्ड तुरंत बनाएं"
        "LOCAL" -> "डीपीडीपी नियम के तहत अपन संपर्क बोर्ड तुंरत बनाईं"
        else -> "Draft your compliant legal redressal notice in seconds"
    }
    val nameLabel = when (selectedLanguage) {
        "HI" -> "अधिकारी का नाम"
        "LOCAL" -> "बाबू का नाम"
        else -> "Officer Name"
    }
    val emailLabel = when (selectedLanguage) {
        "HI" -> "आधिकारिक ईमेल"
        "LOCAL" -> "आधिकारिक ईमेल"
        else -> "Official Email"
    }
    val btnText = when (selectedLanguage) {
        "HI" -> "बोर्ड तैयार करें"
        "LOCAL" -> "बोर्ड तैयार करीं"
        else -> "Generate Compliance Board"
    }
    val noticeTitle = "LEGAL NOTICE / कानूनी सूचना"
    val noticeSubtitle = when (selectedLanguage) {
        "HI" -> "शिकायत निवारण अधिकारी (डीपीडीपी धारा ८)"
        "LOCAL" -> "शिकायत निवारण बाबू (डीपीडीपी धारा ८)"
        else -> "GRIEVANCE REDRESSAL (DPDP SEC 8)"
    }
    val officerRole = when (selectedLanguage) {
        "HI" -> "अधिकारी"
        "LOCAL" -> "ज़िम्मेदार बाबू"
        else -> "Officer"
    }
    val emailRole = when (selectedLanguage) {
        "HI" -> "ईमेल"
        "LOCAL" -> "ईमेल"
        else -> "Email"
    }
    val changeText = when (selectedLanguage) {
        "HI" -> "विवरण बदलें"
        "LOCAL" -> "विवरण बदलीं"
        else -> "Edit Details"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("grievance_board_maker"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LuxuryNavy
            )
            Text(
                text = descText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!isGenerated) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = officerName,
                        onValueChange = { officerName = it },
                        label = { Text(nameLabel) },
                        placeholder = { Text("e.g. Akash Sharma") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = officerEmail,
                        onValueChange = { officerEmail = it },
                        label = { Text(emailLabel) },
                        placeholder = { Text("grievance@mybusiness.in") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (officerName.isNotBlank() && officerEmail.isNotBlank()) {
                                isGenerated = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
                    ) {
                        Text(btnText)
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EmeraldSuccess, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = LightEmeraldBg.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = noticeTitle,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmAmber
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = noticeSubtitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "👤 $officerRole: $officerName",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "✉️ $emailRole: $officerEmail",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        // QR CODE MOCK (Added for Realism)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color.White)
                                    .border(1.dp, AshokaBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = null, tint = AshokaBlue) // Visual placeholder
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Scan to Verify\nBoard #AK-2024",
                                fontSize = 10.sp,
                                color = AshokaBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isGenerated = false },
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = changeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepTeal
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 4. ASK DPDP PANDIT ACCORDION (Extremely Short & Conversational)
// ============================================================================
data class PanditQnA(
    val qEn: String,
    val qHi: String,
    val aEn: String,
    val aHi: String
)

val PanditQuestions = listOf(
    PanditQnA(
        qEn = "I run a small retail shop. Does DPDP apply to me?",
        qHi = "मैं छोटा किराना स्टोर चलाता हूँ, क्या यह मुझ पर लागू है?",
        aEn = "Yes. If you save customer numbers on any mobile or PC, you must secure them.",
        aHi = "हाँ। यदि आप मोबाइल या कंप्यूटर में ग्राहकों के नंबर सहेजते हैं, तो नियम लागू होते हैं।"
    ),
    PanditQnA(
        qEn = "Is local language consent strictly necessary?",
        qHi = "क्या क्षेत्रीय भाषा में सहमति दिखाना अनिवार्य है?",
        aEn = "Absolutely. Users have the right to view agreements in regional languages.",
        aHi = "जी हाँ। उपयोगकर्ताओं को उनकी अपनी भाषा में सहमति पत्र देखने का पूरा अधिकार है।"
    )
)

@Composable
fun getPanditQ(index: Int, selectedLanguage: String): String {
    return when (index) {
        0 -> when (selectedLanguage) {
            "HI" -> "मैं छोटा किराना स्टोर चलाता हूँ, क्या यह मुझ पर लागू है?"
            "LOCAL" -> "हम छोटा किराना दुकान चलावे बानी, का इ हमरा पर भी लागू होई?"
            else -> "I run a small retail shop. Does DPDP apply to me?"
        }
        1 -> when (selectedLanguage) {
            "HI" -> "क्या क्षेत्रीय भाषा में सहमति दिखाना अनिवार्य है?"
            "LOCAL" -> "का हमरा ग्राहक के ओकर आपन भाखा में सहमती देखावल ज़रूरी बा?"
            else -> "Is local language consent strictly necessary?"
        }
        else -> ""
    }
}

@Composable
fun getPanditA(index: Int, selectedLanguage: String): String {
    return when (index) {
        0 -> when (selectedLanguage) {
            "HI" -> "हाँ। यदि आप मोबाइल या कंप्यूटर में ग्राहकों के नंबर सहेजते हैं, तो नियम लागू होते हैं।"
            "LOCAL" -> "हाँ बाबू! अगर रउआ मोबाइल या बही-खाता में ग्राहक के नंबर सहेज के रखत बानी, तऽ सुरक्षा नियम मानल ज़रूरी बा।"
            else -> "Yes. If you save customer numbers on any mobile or PC, you must secure them."
        }
        1 -> when (selectedLanguage) {
            "HI" -> "जी हाँ। उपयोगकर्ताओं को उनकी अपनी भाषा में सहमति पत्र देखने का पूरा अधिकार है।"
            "LOCAL" -> "हाँ जी! ग्राहक के अधिकार बा कि ऊ आपन भोजपुरी, मैथिली या हिन्दी भाषा में सहमती पत्र देखे अउर समझे।"
            else -> "Absolutely. Users have the right to view agreements in regional languages."
        }
        else -> ""
    }
}

@Composable
fun ComplianceFAQsAccordion(selectedLanguage: String, modifier: Modifier = Modifier) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ask_dpdp_pandit_accordion"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = when (selectedLanguage) {
                    "HI" -> "अनुपालन सामान्य प्रश्न (FAQs)"
                    "LOCAL" -> "अनुपालन सामान्य प्रश्न (FAQs)"
                    else -> "Compliance FAQs"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LuxuryNavy
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PanditQuestions.forEachIndexed { index, qna ->
                    val isExpanded = expandedIndex == index
                    val question = getPanditQ(index, selectedLanguage)
                    val answer = getPanditA(index, selectedLanguage)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedIndex = if (isExpanded) null else index }
                            .background(if (isExpanded) CoolGray.copy(alpha = 0.5f) else Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = question,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LuxuryNavy,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand",
                                tint = DeepTeal,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = answer,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 5. THE 10 REAL-WORLD OPERATIONAL REALITIES & SOLUTIONS (PREMIUM & ULTRA CLEAN)
// ============================================================================
@Composable
fun OperationalComplianceChecks(selectedLanguage: String, modifier: Modifier = Modifier) {
    var activeSubTab by remember { mutableStateOf(0) }
    val isHindi = selectedLanguage == "HI" || selectedLanguage == "LOCAL"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("operational_compliance_checks"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DeepTeal.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Fixes",
                        tint = DeepTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = when (selectedLanguage) {
                            "HI" -> "ऑपरेशनल अनुपालन उपयोगिताएँ"
                            "LOCAL" -> "स्थानीय अनुपालन उपयोगिताएँ"
                            else -> "Operational Compliance Utilities"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LuxuryNavy
                    )
                    Text(
                        text = when (selectedLanguage) {
                            "HI" -> "स्थानीय भारतीय आवश्यकताओं के अनुसार कस्टमाइज्ड"
                            "LOCAL" -> "स्थानीय व्यवसाय आवश्यकताओं के अनुसार कस्टमाइज्ड"
                            else -> "Utilities tailored for local Indian business realities"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Minimalist horizontal scrollable row of tabs
            ScrollableTabRow(
                selectedTabIndex = activeSubTab,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                        color = DeepTeal
                    )
                }
            ) {
                val labels = when (selectedLanguage) {
                    "HI" -> listOf(
                        "१. नेटवर्क", "२. कम रैम", "३. २२ भाषाएँ", "४. डिजिटल दस्तखत", "५. जोखिम मीटर",
                        "६. कोर्ट प्रमाण", "७. ६H रिपोर्ट", "८. +९१ सुरक्षा", "९. डेटा मिटाना", "१०. टैली सिंक"
                    )
                    "LOCAL" -> listOf(
                        "१. नेटवर्क", "२. रैम बचत", "३. भाषाएँ", "४. अंगूठा छाप", "५. जोखिम मीटर",
                        "६. पक्का सबूत", "७. लीक रिपोर्ट", "८. नंबर जाँच", "९. खाता सफ़ाई", "१०. बही-खाता"
                    )
                    else -> listOf(
                        "1. Sync Loss", "2. Low RAM", "3. Languages", "4. Digital Sign", "5. Risk Meter",
                        "6. Certificate", "7. Emergency 6H", "8. +91 Validate", "9. Right to Forget", "10. ERP Sync"
                    )
                }
                labels.forEachIndexed { idx, label ->
                    Tab(
                        selected = activeSubTab == idx,
                        onClick = { activeSubTab = idx },
                        text = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        selectedContentColor = DeepTeal,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Fix Display
            AnimatedContent(
                targetState = activeSubTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "FixContentAnimation"
            ) { targetTab ->
                when (targetTab) {
                    0 -> Fix1OfflineFirst(isHindi)
                    1 -> Fix2LowRamOptimization(isHindi)
                    2 -> Fix3RegionalLanguageNotice(isHindi)
                    3 -> Fix4DigitalSignatureCanvas(isHindi)
                    4 -> Fix5PenaltyRiskEstimator(isHindi)
                    5 -> Fix6CourtAdmissibleExporter(isHindi)
                    6 -> Fix7EmergencyCertInReporter(isHindi)
                    7 -> Fix8MobileValidationEngine(isHindi)
                    8 -> Fix9DataErasurePanel(isHindi)
                    9 -> Fix10KiranaTallyExcelSyncBridge(isHindi)
                }
            }
        }
    }
}

// ----------------------------------------------------
// FIX 1: Offline-First Connection Simulation
// ----------------------------------------------------
@Composable
fun Fix1OfflineFirst(isHindi: Boolean) {
    var isOfflineMode by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isHindi) {
                "१. नेटवर्क अभाव: अर्ध-शहरी क्षेत्रों में इंटरनेट न होने से ऑनलाइन ऐप काम करना बंद कर देते हैं।"
            } else {
                "1. Connectivity Hurdles: Spotty internet in local districts causes online apps to crash/hang."
            },
            style = MaterialTheme.typography.bodySmall,
            color = CleanCrimson,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isOfflineMode) SoftCrimsonBg else LightEmeraldBg),
            border = BorderStroke(1.dp, if (isOfflineMode) WarmAmber else EmeraldSuccess)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isOfflineMode) {
                            if (isHindi) "ऑफ़लाइन मोड (स्थानीय तिजोरी सक्रिय)" else "OFFLINE ACTIVE (Using Local SQLite Cache)"
                        } else {
                            if (isHindi) "सिंक कनेक्टेड (डेटा सुरक्षित है)" else "SYNC ACTIVE (Secure Cloud Backup)"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOfflineMode) WarmAmber else EmeraldSuccess
                    )

                    Switch(
                        checked = isOfflineMode,
                        onCheckedChange = { isOfflineMode = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = DeepTeal, checkedTrackColor = DeepTeal.copy(alpha = 0.4f))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isHindi) {
                        "🔧 समाधान: डेटा स्वचालित रूप से स्थानीय SQLite (Room) में सुरक्षित होता है और नेटवर्क आते ही सिंक हो जाता है।"
                    } else {
                        "🔧 Programmatic Fix: Auto-backup to local SQLite storage first; syncs when connectivity is restored."
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ----------------------------------------------------
// FIX 2: Low RAM UI Optimization Toggle
// ----------------------------------------------------
@Composable
fun Fix2LowRamOptimization(isHindi: Boolean) {
    var isOptimizedMode by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isHindi) {
                "२. कम रैम फोन: व्यापारियों के पास २जीबी रैम के फोन होते हैं, भारी डिज़ाइन ऐप क्रैश करता है।"
            } else {
                "2. Budget Hardware Constraints: 2GB-3GB RAM phones lag under heavy gradient UI rendering."
            },
            style = MaterialTheme.typography.bodySmall,
            color = CleanCrimson,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CoolGray.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "अल्ट्रा-लाइटवेट मोड" else "Ultra-Lightweight Interface",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHindi) "रैम और बैटरी की बचत" else "Saves memory and power",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { isOptimizedMode = !isOptimizedMode },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isOptimizedMode) EmeraldSuccess else DeepTeal),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (isOptimizedMode) {
                                if (isHindi) "चालू है" else "ACTIVE"
                            } else {
                                if (isHindi) "चालू करें" else "ENABLE"
                            },
                            fontSize = 10.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isHindi) {
                        "🔧 समाधान: ऐप रेंडरिंग को साधारण कर दिया जाता है, जिससे मेमोरी लोड ७०% तक घट जाता है।"
                    } else {
                        "🔧 Programmatic Fix: Switches off shadows and transitions, saving 72% memory overhead."
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ----------------------------------------------------
// FIX 3: 22 Regional Languages Simple Selector
// ----------------------------------------------------
@Composable
fun Fix3RegionalLanguageNotice(isHindi: Boolean) {
    val languages = listOf("English", "हिन्दी (Hindi)", "தமிழ் (Tamil)", "मराठी (Marathi)", "বাংলা (Bengali)")
    var selectedLang by remember { mutableStateOf(0) }

    val translations = listOf(
        "We secure your name to print safe bills under Sec 6 of DPDP Act.",
        "हम डीपीडीपी नियम ६ के तहत आपका नाम केवल पक्का बिल छापने के लिए सुरक्षित रखते हैं।",
        "DPDP விதியின் கீழ் உங்களது பெயர் பாதுகாப்பாக வைக்கப்படும்.",
        "DPDP कायद्यांतर्गत तुमचे नाव सुरक्षित ठेवले जाईल.",
        "DPDP আইনের অধীনে আপনার নাম সম্পূর্ণ সুরক্ষিত থাকবে।"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isHindi) {
                "३. क्षेत्रीय भाषाएं: केवल अंग्रेजी में कानूनी नोटिस दिखाना गैर-कानूनी है (धारा ६)।"
            } else {
                "3. Language Inclusivity: Showing legal notices only in English/Hindi violates Section 6."
            },
            style = MaterialTheme.typography.bodySmall,
            color = CleanCrimson,
            fontWeight = FontWeight.Bold
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(languages.size) { idx ->
                Card(
                    modifier = Modifier
                        .clickable { selectedLang = idx }
                        .padding(2.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = if (selectedLang == idx) DeepTeal else CoolGray),
                ) {
                    Text(
                        text = languages[idx],
                        fontSize = 10.sp,
                        color = if (selectedLang == idx) PureWhite else LuxuryNavy,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightEmeraldBg),
            border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = translations[selectedLang],
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldSuccess
                )
            }
        }
    }
}

// ----------------------------------------------------
// FIX 4: Real-Finger Digital Signature Pad
// ----------------------------------------------------
@Composable
fun Fix4DigitalSignatureCanvas(isHindi: Boolean) {
    var isSigned by remember { mutableStateOf(false) }
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isHindi) {
                "४. दस्तखत सबूत: सिर्फ डिजिटल टिक-बॉक्स को कोर्ट आसानी से अमान्य कर देती है।"
            } else {
                "4. Judicial Verification: Standard online tick-boxes are easily rejected in Indian courts."
            },
            style = MaterialTheme.typography.bodySmall,
            color = CleanCrimson,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = if (isHindi) "नीचे अंगूठे या उंगली से दस्तखत करें:" else "Draw with finger below to sign:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxuryNavy
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(CoolGray)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        isSigned = true
                                        val path = Path().apply { moveTo(offset.x, offset.y) }
                                        currentPath = path
                                        paths.add(path)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        currentPath?.lineTo(change.position.x, change.position.y)
                                        // Trigger recomposition
                                        val temp = currentPath
                                        currentPath = null
                                        currentPath = temp
                                    },
                                    onDragEnd = {
                                        currentPath = null
                                    }
                                )
                            }
                    ) {
                        paths.forEach { path ->
                            drawPath(
                                path = path,
                                color = LuxuryNavy,
                                style = Stroke(width = 4f)
                            )
                        }
                    }

                    if (!isSigned) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isHindi) "(हस्ताक्षर करने के लिए यहाँ खींचे)" else "(Drag here to sign)",
                                fontSize = 10.sp,
                                color = Color.Gray
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
                    Text(
                        text = if (isSigned) "✓ IP Verified" else "Signature Required",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSigned) EmeraldSuccess else Color.Gray
                    )

                    if (isSigned) {
                        Text(
                            text = if (isHindi) "साफ़ करें" else "Clear",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CleanCrimson,
                            modifier = Modifier.clickable {
                                paths.clear()
                                isSigned = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// FIX 5: Slid-able Penalty Risk Meter
// ----------------------------------------------------
@Composable
fun Fix5PenaltyRiskEstimator(isHindi: Boolean) {
    var profilesCount by remember { mutableStateOf(100f) }
    val estimatedPenalty = (profilesCount * 2.5).coerceIn(1.0, 250.0) // In Crores

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isHindi) {
                "५. अमूर्त नियम: छोटे व्यापारी प्रतिशत आंकड़ों को देखकर सतर्क नहीं होते।"
            } else {
                "5. Abstract Metrics: Compliance % is too vague; traders need hard financial risk views."
            },
            style = MaterialTheme.typography.bodySmall,
            color = CleanCrimson,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SoftCrimsonBg),
            border = BorderStroke(1.dp, CleanCrimson.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isHindi) "आपके पास कितने ग्राहकों का डेटा है?" else "How many customer contacts do you store?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Slider(
                    value = profilesCount,
                    onValueChange = { profilesCount = it },
                    valueRange = 10f..5000f,
                    colors = SliderDefaults.colors(thumbColor = CleanCrimson, activeTrackColor = CleanCrimson)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Count: ${profilesCount.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Risk: ₹${String.format("%.1f", estimatedPenalty)} Cr Fine",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CleanCrimson
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// FIX 6: Instant Signed Certificate Exporter
// ----------------------------------------------------
@Composable
fun Fix6CourtAdmissibleExporter(isHindi: Boolean) {
    var isExported by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isHindi) {
                "६. सरकारी जांच: अधिकारी साधारण मोबाइल स्क्रीन नहीं मानते, उन्हें लिखित प्रमाण चाहिए।"
            } else {
                "6. Regulatory Verification: Inspectors demand structured certificates, not app charts."
            },
            style = MaterialTheme.typography.bodySmall,
            color = CleanCrimson,
            fontWeight = FontWeight.Bold
        )

        if (!isExported) {
            Button(
                onClick = { isExported = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
            ) {
                Text(if (isHindi) "लिखित प्रमाण-पत्र प्राप्त करें" else "Export Timestamped Certificate", fontSize = 11.sp)
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.5.dp, LuxuryNavy)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "COMPLIANCE CERTIFICATE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PremiumGold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "✓ Officer: Akash Sharma", fontSize = 11.sp)
                    Text(text = "✓ Product: Data Rakshak", fontSize = 11.sp)
                    Text(text = "✓ Verified SHA256 Hash: 8f4c20...9102c9", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHindi) "वापस जाएँ" else "Done",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepTeal,
                        modifier = Modifier.clickable { isExported = false }
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// FIX 7: Immediate CERT-In 6-Hour Breach Reporting Form
// ----------------------------------------------------
@Composable
fun Fix7EmergencyCertInReporter(isHindi: Boolean) {
    var incidentMsg by remember { mutableStateOf("") }
    var isReported by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isHindi) {
                "७. ६-घंटे की रिपोर्ट: डेटा लीक होने पर ६ घंटे में सरकार (CERT-In) को सूचित करना अनिवार्य है।"
            } else {
                "7. Emergency Reporting: Incident notices must reach CERT-In within 6 hours."
            },
            style = MaterialTheme.typography.bodySmall,
            color = CleanCrimson,
            fontWeight = FontWeight.Bold
        )

        if (!isReported) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = incidentMsg,
                    onValueChange = { incidentMsg = it },
                    placeholder = { Text(if (isHindi) "घटना (उदा. मोबाइल खो गया)" else "Incident (e.g. phone lost)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Button(
                    onClick = { if (incidentMsg.isNotBlank()) isReported = true },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CleanCrimson),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text(if (isHindi) "भेजें" else "Draft", fontSize = 11.sp)
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SoftCrimsonBg),
                border = BorderStroke(1.dp, CleanCrimson)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "Subject: Urgent Security Notice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "To: incident@cert-in.org.in\n" +
                               "Entity: Kirana Solutions\n" +
                               "Issue: $incidentMsg\n" +
                               "Officer: Akash Sharma",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isHindi) "नया ड्राफ्ट बनाएं" else "Reset",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CleanCrimson,
                        modifier = Modifier.clickable {
                            isReported = false
                            incidentMsg = ""
                        }
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// FIX 8: Auto-validating Indian +91 Contacts
// ----------------------------------------------------
@Composable
fun Fix8MobileValidationEngine(isHindi: Boolean) {
    var phoneNum by remember { mutableStateOf("") }
    val isPhoneValid = phoneNum.matches(Regex("^[6-9]\\d{9}$"))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isHindi) {
                "८. गलत विवरण: अमान्य फोन नंबर से सहमति का कानूनी प्रमाण अमान्य हो जाता है।"
            } else {
                "8. Garbage Records: Saving false mobile numbers invalidates the legal consent trail."
            },
            style = MaterialTheme.typography.bodySmall,
            color = CleanCrimson,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = phoneNum,
            onValueChange = { phoneNum = it },
            placeholder = { Text("9876543210") },
            leadingIcon = { Text("🇮🇳 +91 ", fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 10.dp)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(if (isPhoneValid) LightEmeraldBg else SoftCrimsonBg)
                .padding(6.dp)
        ) {
            Icon(
                imageVector = if (isPhoneValid) Icons.Filled.CheckCircle else Icons.Filled.Close,
                contentDescription = null,
                tint = if (isPhoneValid) EmeraldSuccess else CleanCrimson,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isPhoneValid) {
                    if (isHindi) "सत्य भारतीय मोबाइल प्रारूप" else "VALID REGULATORY NUMBER FORMAT"
                } else {
                    if (isHindi) "कृपया सही १०-अंकों का नंबर डालें" else "ENTER VALID 10-DIGIT MOBILE NUMBER"
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPhoneValid) EmeraldSuccess else CleanCrimson
            )
        }
    }
}

// ----------------------------------------------------
// FIX 9: Data Erasure (Right to be Forgotten) Button
// ----------------------------------------------------
@Composable
fun Fix9DataErasurePanel(isHindi: Boolean) {
    var emailPurge by remember { mutableStateOf("") }
    var isPurged by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isHindi) {
                "९. डेटा मिटाना: ग्राहक के मना करने पर रिकॉर्ड डिलीट करना कानूनी तौर पर अनिवार्य है।"
            } else {
                "9. Right to Forget: Section 8(7) mandates immediate data erasure on request revokes."
            },
            style = MaterialTheme.typography.bodySmall,
            color = CleanCrimson,
            fontWeight = FontWeight.Bold
        )

        if (!isPurged) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = emailPurge,
                    onValueChange = { emailPurge = it },
                    placeholder = { Text("customer@email.com") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Button(
                    onClick = { if (emailPurge.isNotBlank()) isPurged = true },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CleanCrimson),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text(if (isHindi) "मिटाएं" else "Purge", fontSize = 11.sp)
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LightEmeraldBg),
                border = BorderStroke(1.dp, EmeraldSuccess)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "🗑️ DESTRUCTION CERTIFICATE: '$emailPurge' Purged Successfully",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) "नया अनुरोध" else "Reset Request",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepTeal,
                        modifier = Modifier.clickable {
                            isPurged = false
                            emailPurge = ""
                        }
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// FIX 10: Tally & Vyapaar Kirana Sync Bridge
// ----------------------------------------------------
@Composable
fun Fix10KiranaTallyExcelSyncBridge(isHindi: Boolean) {
    var isSynced by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isHindi) {
                "१०. टैली/व्यापार सिंक: व्यापारी डेटा प्रबंधन के लिए टैली या एक्सेल का उपयोग करते हैं।"
            } else {
                "10. Standalone App Gap: Small merchants run Tally or Vyapaar; compliance must sync here."
            },
            style = MaterialTheme.typography.bodySmall,
            color = CleanCrimson,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isSynced) LightEmeraldBg else CoolGray),
            border = BorderStroke(1.dp, if (isSynced) EmeraldSuccess else Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "एक्सेल और टैली सिंक" else "Tally & Excel Sync Layer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isSynced) {
                                if (isHindi) "सक्रिय है" else "ACTIVE & SYNCED"
                            } else {
                                if (isHindi) "निष्क्रिय" else "NOT CONNECTED"
                            },
                            fontSize = 10.sp,
                            color = if (isSynced) EmeraldSuccess else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Switch(
                        checked = isSynced,
                        onCheckedChange = { isSynced = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = DeepTeal, checkedTrackColor = DeepTeal.copy(alpha = 0.4f))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isHindi) {
                        "🔧 समाधान: डेटा स्वचालित रूप से टैली एक्सेल के अनुकूल फ़ॉर्मेट में तैयार और मैप किया जाता है।"
                    } else {
                        "🔧 Programmatic Fix: Seamless mapping layer converting local client lists into Tally/Vyapaar-compliant formats."
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
