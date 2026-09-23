package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.ComplianceViewModel

// Curated Indian GovTech Palette
private val GovTeal = Color(0xFF0D9488)
private val GovNavy = Color(0xFF0F172A)
private val GovAmber = Color(0xFFD97706)
private val GovBlue = Color(0xFF2563EB)
private val GovPurple = Color(0xFF7C3AED)
private val GovCrimson = Color(0xFFDC2626)
private val GovEmerald = Color(0xFF059669)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplianceGuideScreen(
    viewModel: ComplianceViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDomain by viewModel.selectedGuideDomain.collectAsStateWithLifecycle()
    val searchQuery by viewModel.guideSearchQuery.collectAsStateWithLifecycle()
    val groundedResult by viewModel.groundedSearchResult.collectAsStateWithLifecycle()
    val isSearching by viewModel.isGroundingSearching.collectAsStateWithLifecycle()
    val searchError by viewModel.groundingSearchError.collectAsStateWithLifecycle()
    val bookmarkedIds by viewModel.bookmarkedGuideIds.collectAsStateWithLifecycle()
    val isHindi by viewModel.isHindiMode.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    val focusManager = LocalFocusManager.current

    var activeQuickFilter by remember { mutableStateOf("ALL") } // ALL, BOOKMARKS, CRITICAL, CODE
    var showDetailDialog by remember { mutableStateOf(false) }
    var activeDetailItem by remember { mutableStateOf<ComplianceGuideItem?>(null) }
    var showMatrixView by remember { mutableStateOf(false) }

    val allGuideItems = remember { ComplianceGuideKnowledgeBase.items }

    // Filtered items based on domain, query, and quick filters
    val filteredItems = remember(selectedDomain, searchQuery, activeQuickFilter, bookmarkedIds) {
        allGuideItems.filter { item ->
            val matchesDomain = selectedDomain == RegulationDomain.ALL || item.domain == selectedDomain
            val matchesQuery = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.sectionRef.contains(searchQuery, ignoreCase = true) ||
                    item.executiveSummary.contains(searchQuery, ignoreCase = true) ||
                    item.tags.any { it.contains(searchQuery, ignoreCase = true) }
            
            val matchesQuickFilter = when (activeQuickFilter) {
                "BOOKMARKS" -> bookmarkedIds.contains(item.id)
                "CRITICAL" -> item.severity == ComplianceSeverity.CRITICAL
                "CODE" -> item.technicalImplementationCode.isNotBlank()
                else -> true
            }

            matchesDomain && matchesQuery && matchesQuickFilter
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("compliance_guide_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ====================================================================
        // 1. HERO HEADER & LEGISLATIVE GROUNDING BADGE
        // ====================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compliance_guide_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GovTeal.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, GovTeal.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Grounded",
                                    tint = GovTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isHindi) "गूगल सर्च ग्राउंडिंग सक्रिय" else "Google Search Grounded | Live Gazette Sync",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GovTeal
                                )
                            }
                        }

                        IconButton(
                            onClick = { showMatrixView = !showMatrixView },
                            modifier = Modifier.testTag("toggle_matrix_view_button")
                        ) {
                            Icon(
                                imageVector = if (showMatrixView) Icons.Default.Close else Icons.Default.Info,
                                contentDescription = "Toggle Cross-Regulatory Matrix",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isHindi) "संविधिक अनुपालन गाइड (DPDP, आधार व RBI)" else "Interactive Compliance Guide & Legal Sentinel",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = if (isHindi) 
                            "DPDP अधिनियम 2023, UIDAI आधार नियम और RBI मास्टर निर्देशों के लिए रीयल-टाइम सांविधिक मार्गदर्शन व ड्रॉप-इन कोड।" 
                        else 
                            "Statutory documentation, implementation rules & live Gazette intelligence across DPDP Act 2023, UIDAI Aadhaar, and RBI Directives.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Key Metric Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatutoryStatBadge(
                            label = "DPDP 2023",
                            subtext = "44 Sections",
                            color = GovBlue,
                            modifier = Modifier.weight(1f)
                        )
                        StatutoryStatBadge(
                            label = "UIDAI Aadhaar",
                            subtext = "Vault & Masking",
                            color = GovAmber,
                            modifier = Modifier.weight(1f)
                        )
                        StatutoryStatBadge(
                            label = "RBI Directives",
                            subtext = "Lending & CoFT",
                            color = GovPurple,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // ====================================================================
        // 2. GOOGLE SEARCH GROUNDING REAL-TIME LEGISLATIVE SEARCH BAR
        // ====================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("grounded_search_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Search Gazette",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isHindi) "लाइव गजट व नियम खोजें (AI ग्राउंडिंग)" else "Ask Legal Sentinel (Real-Time Search Grounding)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setGuideSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("compliance_guide_search_input"),
                        placeholder = {
                            Text(
                                if (isHindi) "उदा. 'UIDAI मास्क आधार नियम', 'RBI डिजिटल लेंडिंग', 'DPDP धारा 12'..." 
                                else "e.g. 'Masked Aadhaar storage rules', 'RBI Digital Lending contacts ban', 'DPDP Sec 12 erasure'..."
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setGuideSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                viewModel.searchGroundedLegislativeUpdates(searchQuery, selectedDomain)
                            }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSearching) "Connecting to Google Search Grounding..." else "Queries verified against official Government Gazettes",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.searchGroundedLegislativeUpdates(searchQuery, selectedDomain)
                            },
                            enabled = !isSearching,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GovTeal),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("grounded_search_button")
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Searching...", fontSize = 12.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Live Grounding", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Quick Prompt Chips
                    Text(
                        text = "Quick Statutory Queries:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QuickPromptChip("MeitY DPDP 2026 Rules") {
                            viewModel.setGuideSearchQuery("MeitY DPDP Act 2023 rules and implementation notifications")
                            viewModel.searchGroundedLegislativeUpdates("MeitY DPDP Act 2023 rules and implementation notifications", RegulationDomain.DPDP_ACT_2023)
                        }
                        QuickPromptChip("UIDAI Masked Aadhaar") {
                            viewModel.setGuideSearchQuery("UIDAI Masked Aadhaar storage guidelines and Aadhaar Act Section 29")
                            viewModel.searchGroundedLegislativeUpdates("UIDAI Masked Aadhaar storage guidelines and Aadhaar Act Section 29", RegulationDomain.AADHAAR_UIDAI)
                        }
                        QuickPromptChip("RBI Digital Lending Ban") {
                            viewModel.setGuideSearchQuery("RBI Master Direction on Digital Lending DLAs prohibition on contacts and media")
                            viewModel.searchGroundedLegislativeUpdates("RBI Master Direction on Digital Lending DLAs prohibition on contacts and media", RegulationDomain.RBI_REGULATIONS)
                        }
                        QuickPromptChip("Aadhaar Data Vault") {
                            viewModel.setGuideSearchQuery("UIDAI Aadhaar Data Vault reference key HSM requirements")
                            viewModel.searchGroundedLegislativeUpdates("UIDAI Aadhaar Data Vault reference key HSM requirements", RegulationDomain.AADHAAR_UIDAI)
                        }
                        QuickPromptChip("DPDP Sec 12 Erasure") {
                            viewModel.setGuideSearchQuery("DPDP Act 2023 Section 12 Right to Erasure cascading wipe requirements")
                            viewModel.searchGroundedLegislativeUpdates("DPDP Act 2023 Section 12 Right to Erasure cascading wipe requirements", RegulationDomain.DPDP_ACT_2023)
                        }
                        QuickPromptChip("RBI Data Localization") {
                            viewModel.setGuideSearchQuery("RBI 2018 Storage of Payment System Data Indian server localization")
                            viewModel.searchGroundedLegislativeUpdates("RBI 2018 Storage of Payment System Data Indian server localization", RegulationDomain.RBI_REGULATIONS)
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 3. LIVE GROUNDED AI RESPONSE CARD (IF SEARCH EXECUTED)
        // ====================================================================
        if (isSearching || groundedResult != null || searchError != null) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("grounded_ai_result_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = BorderStroke(1.5.dp, GovTeal)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (isSearching) GovAmber else GovEmerald)
                                    )
                                    Text(
                                        text = if (isSearching) "Searching Government Repositories..." else "Grounded Statutory Intelligence",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (groundedResult != null) {
                                    IconButton(
                                        onClick = { viewModel.clearGroundedSearchResult() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Dismiss grounded result",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            if (isSearching) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(color = GovTeal)
                                    Text(
                                        text = "Querying live MeitY, UIDAI, and RBI gazettes with Google Search Grounding...",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else if (searchError != null) {
                                Text(
                                    text = "⚠️ " + searchError,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else if (groundedResult != null) {
                                val result = groundedResult!!
                                
                                Text(
                                    text = "Query: \"" + result.query + "\"",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = result.synthesizedText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                // Grounding Citations & Web Sources
                                if (result.sources.isNotEmpty()) {
                                    Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                                    
                                    Text(
                                        text = "Official Gazette Citations & Web Sources:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        result.sources.take(4).forEach { source ->
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.surface,
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        try {
                                                            uriHandler.openUri(source.uri)
                                                        } catch (e: Exception) {
                                                            Toast.makeText(context, "Could not open URL: " + source.uri, Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Info,
                                                            contentDescription = null,
                                                            tint = GovTeal,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Text(
                                                            text = source.title,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }

                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                        contentDescription = "Open Source",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(result.synthesizedText))
                                            Toast.makeText(context, "Grounded guidance copied to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("copy_grounded_result_button")
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy Guidance", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 4. STATUTORY DOMAIN SELECTOR TABS & QUICK FILTER CHIPS
        // ====================================================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Regulatory Domain Tabs
                ScrollableTabRow(
                    selectedTabIndex = RegulationDomain.values().indexOf(selectedDomain),
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.background,
                    divider = {}
                ) {
                    RegulationDomain.values().forEach { domain ->
                        Tab(
                            selected = selectedDomain == domain,
                            onClick = { viewModel.setGuideDomain(domain) },
                            text = {
                                Text(
                                    text = domain.displayName,
                                    fontWeight = if (selectedDomain == domain) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.testTag("domain_tab_${domain.name}")
                        )
                    }
                }

                // Quick Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activeQuickFilter == "ALL",
                        onClick = { activeQuickFilter = "ALL" },
                        label = { Text("All Provisions (${filteredItems.size})", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                    FilterChip(
                        selected = activeQuickFilter == "CRITICAL",
                        onClick = { activeQuickFilter = if (activeQuickFilter == "CRITICAL") "ALL" else "CRITICAL" },
                        label = { Text("Mandatory / Statutory", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = GovCrimson, modifier = Modifier.size(14.dp)) }
                    )
                    FilterChip(
                        selected = activeQuickFilter == "CODE",
                        onClick = { activeQuickFilter = if (activeQuickFilter == "CODE") "ALL" else "CODE" },
                        label = { Text("With Drop-in Code", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = GovPurple, modifier = Modifier.size(14.dp)) }
                    )
                    FilterChip(
                        selected = activeQuickFilter == "BOOKMARKS",
                        onClick = { activeQuickFilter = if (activeQuickFilter == "BOOKMARKS") "ALL" else "BOOKMARKS" },
                        label = { Text("Saved (${bookmarkedIds.size})", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = GovAmber, modifier = Modifier.size(14.dp)) }
                    )
                }
            }
        }

        // ====================================================================
        // 5. CROSS-REGULATORY COMPARISON MATRIX (IF TOGGLED)
        // ====================================================================
        if (showMatrixView || selectedDomain == RegulationDomain.CROSS_STATUTORY) {
            item {
                CrossRegulatoryMatrixCard()
            }
        }

        // ====================================================================
        // 6. INTERACTIVE STATUTORY PROVISIONS LIST
        // ====================================================================
        if (filteredItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No statutory provisions match your filter",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Try clearing search keywords or run a live Google Search Grounding query above.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = {
                                viewModel.setGuideSearchQuery("")
                                activeQuickFilter = "ALL"
                                viewModel.setGuideDomain(RegulationDomain.ALL)
                            }
                        ) {
                            Text("Reset Filters")
                        }
                    }
                }
            }
        } else {
            items(filteredItems, key = { it.id }) { item ->
                ComplianceGuideItemCard(
                    item = item,
                    isBookmarked = bookmarkedIds.contains(item.id),
                    onBookmarkToggle = { viewModel.toggleGuideBookmark(item.id) },
                    onInspectDeepDive = {
                        activeDetailItem = item
                        showDetailDialog = true
                    },
                    onCopyCode = { code ->
                        clipboardManager.setText(AnnotatedString(code))
                        Toast.makeText(context, "Implementation code copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    onOpenUrl = { url ->
                        try {
                            uriHandler.openUri(url)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open: " + url, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    // ====================================================================
    // 7. IN-DEPTH STATUTORY DETAIL BOTTOM SHEET / DIALOG
    // ====================================================================
    if (showDetailDialog && activeDetailItem != null) {
        val item = activeDetailItem!!
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            confirmButton = {
                Button(
                    onClick = { showDetailDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GovTeal)
                ) {
                    Text("Close")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        try {
                            uriHandler.openUri(item.officialUrl)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Official Gazette")
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(item.domain.badgeColorHex)
                    )
                    Text(
                        text = item.sectionRef,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Penalty Warning",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = item.statutoryPenalty,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Executive Summary:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.executiveSummary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    item {
                        Text(
                            text = "Statutory Requirements for Fiduciaries:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            item.detailedRequirements.forEach { req ->
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("•", color = GovTeal, fontWeight = FontWeight.Bold)
                                    Text(req, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    if (item.technicalImplementationCode.isNotBlank()) {
                        item {
                            Text(
                                text = "Drop-in Kotlin / Android Implementation:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GovNavy,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Kotlin 2.2 / Jetpack Compose", color = Color.LightGray, fontSize = 10.sp)
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(item.technicalImplementationCode))
                                                Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Copy code", tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text(
                                        text = item.technicalImplementationCode,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Gazette Citation: " + item.officialGazetteRef,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ====================================================================
// SUB-COMPONENTS & CARDS
// ====================================================================

@Composable
fun StatutoryStatBadge(
    label: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickPromptChip(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = GovAmber, modifier = Modifier.size(12.dp))
            Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ComplianceGuideItemCard(
    item: ComplianceGuideItem,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onInspectDeepDive: () -> Unit,
    onCopyCode: (String) -> Unit,
    onOpenUrl: (String) -> Unit
) {
    var isCodeExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guide_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Section Badge + Severity + Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(item.domain.badgeColorHex).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(item.domain.badgeColorHex).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = item.sectionRef,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(item.domain.badgeColorHex),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(item.severity.badgeColorHex).copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = item.severity.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(item.severity.badgeColorHex),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("bookmark_button_${item.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) GovAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Title
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Executive Summary
            Text(
                text = item.executiveSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Statutory Penalty Callout
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Penalty",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Penalty: " + item.statutoryPenalty,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Expandable Code Section
            if (item.technicalImplementationCode.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCodeExpanded = !isCodeExpanded },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = GovPurple, modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (isCodeExpanded) "Hide Implementation Code" else "View Drop-in Kotlin/Android Code",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GovPurple
                                )
                            }
                            Icon(
                                imageVector = if (isCodeExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = GovPurple
                            )
                        }

                        if (isCodeExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GovNavy,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = { onCopyCode(item.technicalImplementationCode) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Copy code", tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text(
                                        text = item.technicalImplementationCode,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tags Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item.tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "#" + tag,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onOpenUrl(item.officialUrl) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gazette Link", fontSize = 11.sp)
                }

                Button(
                    onClick = onInspectDeepDive,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("inspect_button_${item.id}")
                ) {
                    Text("Deep Dive", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CrossRegulatoryMatrixCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cross_regulatory_matrix_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, GovEmerald)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = GovEmerald)
                Text(
                    text = "Statutory Cross-Comparison Matrix",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Key architectural differences across DPDP Act 2023, UIDAI Aadhaar, and RBI Master Directions:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Matrix Rows
            MatrixRow(
                dimension = "Consent Requirement",
                dpdpRule = "Sec. 6: Unbundled, affirmative opt-in, effortless withdrawal",
                uidaiRule = "Explicit purpose disclosure for authentication / e-KYC",
                rbiRule = "Explicit consent + strict prohibition on contact/media access"
            )

            MatrixRow(
                dimension = "Storage & Masking",
                dpdpRule = "Purpose limitation & data minimization (Sec 8)",
                uidaiRule = "Strict plaintext ban. Masked XXXXXXXX1234 or Vault only",
                rbiRule = "Payment data localization in India + CoFT card tokenization"
            )

            MatrixRow(
                dimension = "Right to Erasure",
                dpdpRule = "Sec. 12: Cascading wipe across all active tables & processors",
                uidaiRule = "Immediate purge of raw XML/QR bytes after e-KYC",
                rbiRule = "Right to be Forgotten for borrowers from DLAs/LSPs"
            )

            MatrixRow(
                dimension = "Breach Reporting",
                dpdpRule = "Sec. 15: Prompt notice to DPBI and affected users",
                uidaiRule = "Immediate reporting of biometric/auth leak to UIDAI",
                rbiRule = "CERT-In 6-hour reporting mandate + RBI Board notification"
            )
        }
    }
}

@Composable
fun MatrixRow(
    dimension: String,
    dpdpRule: String,
    uidaiRule: String,
    rbiRule: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = dimension, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("🔵 DPDP:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovBlue)
                Text(dpdpRule, fontSize = 11.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("🟠 UIDAI:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovAmber)
                Text(uidaiRule, fontSize = 11.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("🟣 RBI:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovPurple)
                Text(rbiRule, fontSize = 11.sp)
            }
        }
    }
}
