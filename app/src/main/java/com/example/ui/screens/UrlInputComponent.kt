package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UrlInputComponent(
    isHindi: Boolean,
    onScanTriggered: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var url by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = if (isHindi) "🔍 अपनी वेबसाइट का लिंक जांचें" else "🔍 Scan Your Website for Legal Loopopholes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (isHindi) "यह पता लगाएगा कि क्या आपकी वेबसाइट पर कोई कानूनी कमियां हैं जिससे आपको जुर्माना लग सकता है।" else "Find out if your website has security leaks, consent loopholes, or violates Indian privacy laws.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = url,
            onValueChange = {
                url = it
                isError = it.isNotEmpty() && !it.startsWith("http")
            },
            label = { Text(if (isHindi) "वेबसाइट का लिंक (जैसे: https://my-website.com)" else "Website URL (e.g., https://my-website.com)") },
            placeholder = { Text("https://...") },
            isError = isError,
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError) {
            Text(
                text = if (isHindi) "कृपया सही लिंक डालें (https:// ज़रूर लिखें)" else "Please enter a valid URL (must start with https://)",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                if (url.isNotEmpty() && !isError) {
                    onScanTriggered(url)
                }
            },
            enabled = url.isNotEmpty() && !isError,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(
                text = if (isHindi) "जाँच शुरू करें (Start Compliance Scan)" else "Run Compliance Scan",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
