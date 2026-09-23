package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ConsentRecord

// Color Tokens: Meditation Teal & Midnight Slate Palette
val ConsentMidnightSlate = Color(0xFF0C121A)
val ConsentDeepObsidian = Color(0xFF050505)
val ConsentMeditationTeal = Color(0xFF2DD4BF)
val ConsentMutedText = Color(0xFF94A3B8)
val ConsentCardBorder = Color(0xFF1E293B)
val ConsentAlertAmber = Color(0xFFF59E0B)

/**
 * Reusable ConsentDisclaimer Composable displaying mandatory legal disclosures
 * as required under Section 5 & Section 6 of the Digital Personal Data Protection (DPDP) Act, 2023.
 */
@Composable
fun ConsentDisclaimer(
    title: String = "DPDP Act 2023 Mandatory Notice",
    purpose: String = "Personal data is processed strictly on-device for digital asceticism and focus preservation.",
    statutoryBasis: String = "Digital Personal Data Protection Act, 2023 (Sec 5 & Sec 6)",
    isAcknowledged: Boolean = false,
    onAccept: (record: ConsentRecord) -> Unit = {},
    onDecline: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("consent_disclaimer_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ConsentDeepObsidian),
        border = BorderStroke(1.dp, SolidColor(ConsentMeditationTeal.copy(alpha = 0.35f)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Legal badge and icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "DPDP Security",
                        tint = ConsentMeditationTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LEGAL DISCLOSURE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ConsentMeditationTeal,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ConsentMeditationTeal.copy(alpha = 0.12f),
                    border = BorderStroke(0.5.dp, ConsentMeditationTeal.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "SEC 5/6 COMPLIANT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = ConsentMeditationTeal,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notice Title & Description
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = purpose,
                fontSize = 13.sp,
                color = ConsentMutedText,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Statutory Reference Banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ConsentMidnightSlate, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = ConsentMeditationTeal.copy(alpha = 0.8f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = statutoryBasis,
                    fontSize = 11.sp,
                    color = ConsentMeditationTeal,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Itemized Disclosures
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LegalBulletItem(
                    label = "Purpose Limitation",
                    description = "Data is never shared with third parties or advertisers."
                )
                LegalBulletItem(
                    label = "Right to Erasure (Sec 12)",
                    description = "You can permanently purge all records instantly at any time."
                )
                LegalBulletItem(
                    label = "Nomination (Sec 14)",
                    description = "Appoint a trusted nominee to manage your data rights."
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            if (!isAcknowledged) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("consent_decline_button"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF475569)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ConsentMutedText)
                    ) {
                        Text(
                            text = "Decline",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            val record = ConsentRecord(
                                consentType = "DPDP_GENERAL_PROCESSING",
                                purposeDescription = purpose,
                                isGranted = true,
                                grantedAt = System.currentTimeMillis(),
                                statutoryBasis = statutoryBasis
                            )
                            onAccept(record)
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("consent_accept_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ConsentMeditationTeal)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = ConsentDeepObsidian,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Acknowledge & Consent",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ConsentDeepObsidian
                        )
                    }
                }
            } else {
                // Acknowledged Badge
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = ConsentMeditationTeal.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ConsentMeditationTeal.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Consent Active",
                            tint = ConsentMeditationTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Consent Verified & Logged (DPDP Compliant)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ConsentMeditationTeal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegalBulletItem(label: String, description: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp, end = 8.dp)
                .size(4.dp)
                .background(ConsentMeditationTeal, RoundedCornerShape(2.dp))
        )
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = ConsentMutedText
            )
        }
    }
}
