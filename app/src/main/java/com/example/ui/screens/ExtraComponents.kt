package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DataProcessingSandbox() {
    var originalPurpose by remember { mutableStateOf("") }
    var currentProcessingPurpose by remember { mutableStateOf("") }
    
    var retentionPeriod by remember { mutableStateOf("") }
    var isAnonymized by remember { mutableStateOf(false) }

    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isPass by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("data_processing_sandbox"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Build, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("DPDP Workflow Test Sandbox", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text("Test 'Purpose' and 'Storage' limitations under DPDP Act 2023", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Text("1. Purpose Limitation", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = originalPurpose,
                    onValueChange = { originalPurpose = it },
                    label = { Text("Apparent Purpose") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("e.g. KYC Verification") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = currentProcessingPurpose,
                    onValueChange = { currentProcessingPurpose = it },
                    label = { Text("Actual Processing") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("e.g. Marketing Profile") },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("2. Storage Limitation", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            
            OutlinedTextField(
                value = retentionPeriod,
                onValueChange = { retentionPeriod = it },
                label = { Text("Target Retention Period") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 6 months, Indefinite, 5 Years") },
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isAnonymized = !isAnonymized }
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isAnonymized, onCheckedChange = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Irreversibly anonymized after primary purpose is met?", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            Button(
                onClick = { 
                    val purposeMatch = originalPurpose.equals(currentProcessingPurpose, ignoreCase = true) && originalPurpose.isNotBlank()
                    val isIndefinite = retentionPeriod.contains("indefinite", ignoreCase = true) || retentionPeriod.contains("forever", ignoreCase = true) || retentionPeriod.contains("unlimited", ignoreCase = true)
                    val retentionValid = !isIndefinite && retentionPeriod.isNotBlank()

                    if (!purposeMatch) {
                        isPass = false
                        resultMessage = "FAILED: Purpose mismatch! Under DPDP Sec 8(1), divergent processing breaches consent."
                    } else if (isIndefinite && !isAnonymized) {
                        isPass = false
                        resultMessage = "FAILED: Storage violation! Under DPDP Sec 8(7), data cannot be retained indefinitely unless irreversibly anonymized."
                    } else if (!retentionValid) {
                        isPass = false
                        resultMessage = "WARNING: Ambiguous retention. Define exact timeframe to comply with data minimization."
                    } else {
                        isPass = true
                        resultMessage = "PASSED: Workflow aligns with DPDP Purpose & Storage constraints."
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Validate Legal Constraints", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            if (resultMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (isPass) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                    border = BorderStroke(1.dp, if (isPass) Color(0xFF81C784) else Color(0xFFE57373)),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPass) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                            contentDescription = null,
                            tint = if (isPass) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = resultMessage!!,
                            color = if (isPass) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegalTerminologyDrawer(
    term: String,
    explanation: String,
    onDismiss: () -> Unit
) {
    // Simple implementation as a dialog for toddler-friendly interaction
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Legal Terminology", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text("Formal Term:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(term, fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Toddler-Friendly Translation:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(explanation, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it!", fontWeight = FontWeight.Bold)
            }
        }
    )
}
