package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ComplianceScore

@Composable
fun ComplianceHealthChart(scores: List<ComplianceScore>, isHindi: Boolean, modifier: Modifier = Modifier) {
    // Calculate average score
    val averageScore = if (scores.isNotEmpty()) {
        scores.map { it.score }.average().toFloat()
    } else {
        0f
    }

    val animatedOverallScore by animateFloatAsState(
        targetValue = averageScore,
        animationSpec = tween(durationMillis = 1000),
        label = "OverallScoreAnimation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHindi) "अनुपालन मेट्रिक्स" else "Compliance Metrics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHindi) "सक्रिय भारतीय DPDP 2023 नियमों पर आधारित" else "Based on active Indian DPDP 2023 regulations",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Active DB Version indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "v2026.06",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Score and Dial Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Radial Score Meter
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    val primaryColor = if (averageScore >= 80) {
                        Color(0xFF2E7D32) // Emerald Green
                    } else if (averageScore >= 50) {
                        Color(0xFFF57C00) // Vibrant Orange
                    } else {
                        MaterialTheme.colorScheme.error // Crimson Red
                    }

                    Canvas(modifier = Modifier.size(100.dp)) {
                        // Background track arc
                        drawArc(
                            color = trackColor,
                            startAngle = -220f,
                            sweepAngle = 260f,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Progress arc
                        drawArc(
                            color = primaryColor,
                            startAngle = -220f,
                            sweepAngle = (animatedOverallScore / 100f) * 260f,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${animatedOverallScore.toInt()}%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "अनुपालन" else "COMPLIANCE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Summary Text Card
                Column(modifier = Modifier.weight(1.0f)) {
                    val ratingText = if (averageScore >= 80) {
                        if (isHindi) "सुरक्षित" else "OPTIMAL"
                    } else if (averageScore >= 50) {
                        if (isHindi) "सावधान / कमजोर" else "VULNERABLE"
                    } else {
                        if (isHindi) "खतरनाक उल्लंघन" else "CRITICAL"
                    }
                    val ratingColor = if (averageScore >= 80) Color(0xFF2E7D32) else if (averageScore >= 50) Color(0xFFE65100) else MaterialTheme.colorScheme.error
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (averageScore >= 80) Icons.Default.CheckCircle else if (averageScore >= 50) Icons.Default.Info else Icons.Default.Warning,
                            contentDescription = null,
                            tint = ratingColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = ratingText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = ratingColor
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = if (averageScore >= 80) {
                            if (isHindi) "उत्कृष्ट स्थिति। सभी प्रमुख सहमति पत्र और नियम कानून के अनुरूप हैं।" else "Excellent standing. All key regulatory fiduciaries and consent forms aligned perfectly with the law."
                        } else if (averageScore >= 50) {
                            if (isHindi) "सुधार की आवश्यकता। डेटा हैंडलिंग या गोपनीयता नीति से जुड़े नियमों को तुरंत अपडेट करें।" else "Needs adjustment. Several provisions regarding data handling or privacy policy clauses require immediate updates."
                        } else {
                            if (isHindi) "गंभीर खतरा। बड़े नियम उल्लंघन मिले हैं। इन्हें ठीक न करने पर भारी जुर्माना लग सकता है।" else "High risk. Severe compliance gaps detected. Failure to address these can attract substantial statutory penalties."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(16.dp))

            // Sub-category list
            Text(
                text = if (isHindi) "नियमों का विवरण (STATUTORY BREAKDOWN)" else "STATUTORY BREAKDOWN",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            scores.forEach { item ->
                CategoryScoreRow(item, isHindi)
            }
        }
    }
}

@Composable
fun CategoryScoreRow(score: ComplianceScore, isHindi: Boolean) {
    val animatedProgress by animateFloatAsState(
        targetValue = score.score / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "CategoryProgressAnimation"
    )

    val barColor = if (score.score >= 80f) {
        Color(0xFF4CAF50)
    } else if (score.score >= 50f) {
        Color(0xFFFF9800)
    } else {
        MaterialTheme.colorScheme.error
    }

    val statusText = if (score.score >= 80f) {
        if (isHindi) "सुरक्षित" else "Satisfied"
    } else if (score.score >= 50f) {
        if (isHindi) "चेतावनी" else "Warning"
    } else {
        if (isHindi) "उल्लंघन" else "Violation"
    }
    
    val statusColor = if (score.score >= 80f) Color(0xFF2E7D32) else if (score.score >= 50f) Color(0xFFE65100) else MaterialTheme.colorScheme.error

    val categoryDisplayName = if (isHindi) {
        when (score.category) {
            "Purpose Limitation" -> "उद्देश्य सीमा (Purpose Limitation)"
            "Storage Limitation" -> "डेटा संग्रहण सीमा (Storage Limitation)"
            "Consent Granularity" -> "सहमति स्पष्टता (Consent Granularity)"
            "Data Erasure" -> "डेटा हटाने की व्यवस्था (Data Erasure)"
            else -> score.category
        }
    } else {
        score.category
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryDisplayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${score.score.toInt()}/100",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // Progress bar track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}
