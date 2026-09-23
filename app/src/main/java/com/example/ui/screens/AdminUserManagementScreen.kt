package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AuthorizedUserEntity
import com.example.data.UserAuditLogEntity
import com.example.ui.ComplianceViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Admin User Management Screen.
 * Strictly protected for users with ADMIN authorization.
 * Allows viewing users, adding Gmail accounts, changing roles, deactivating,
 * reactivating, and inspecting the administrative audit trail.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserManagementScreen(
    viewModel: ComplianceViewModel,
    modifier: Modifier = Modifier
) {
    val users by viewModel.allAuthorizedUsers.collectAsStateWithLifecycle()
    val auditLogs by viewModel.allUserAuditLogs.collectAsStateWithLifecycle()

    var emailInput by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("USER") }
    var roleMenuExpanded by remember { mutableStateOf(false) }
    var activeSubTab by remember { mutableStateOf(0) } // 0: Users, 1: Audit Trail

    val activeCount = users.count { it.status == "ACTIVE" }
    val adminCount = users.count { it.role == "ADMIN" && it.status == "ACTIVE" }
    val inactiveCount = users.count { it.status != "ACTIVE" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("admin_header_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.AdminPanelSettings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Admin Access Control Panel",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Private whitelist management & security audit trail",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Summary Statistics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Users",
                    count = users.size.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Filled.People,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Active Admins",
                    count = adminCount.toString(),
                    color = MaterialTheme.colorScheme.tertiary,
                    icon = Icons.Filled.Security,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Inactive/Revoked",
                    count = inactiveCount.toString(),
                    color = MaterialTheme.colorScheme.error,
                    icon = Icons.Filled.Block,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Add Authorized User Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth().testTag("add_user_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PersonAdd,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Add Authorized User",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Text(
                        text = "Pre-approve a Google/Gmail account to grant immediate access upon sign-in.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Gmail / Google Account") },
                        placeholder = { Text("colleague@gmail.com") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_user_email_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Role Dropdown Selector
                        ExposedDropdownMenuBox(
                            expanded = roleMenuExpanded,
                            onExpandedChange = { roleMenuExpanded = !roleMenuExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = "Role: $selectedRole",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleMenuExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("role_dropdown_trigger")
                            )
                            ExposedDropdownMenu(
                                expanded = roleMenuExpanded,
                                onDismissRequest = { roleMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("USER (Standard Access)") },
                                    onClick = {
                                        selectedRole = "USER"
                                        roleMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("ADMIN (Full Management Access)") },
                                    onClick = {
                                        selectedRole = "ADMIN"
                                        roleMenuExpanded = false
                                    }
                                )
                            }
                        }

                        // Authorize User Button
                        Button(
                            onClick = {
                                if (emailInput.isNotBlank()) {
                                    viewModel.addAuthorizedUser(emailInput, selectedRole)
                                    emailInput = ""
                                }
                            },
                            enabled = emailInput.contains("@"),
                            modifier = Modifier.height(54.dp).testTag("authorize_user_submit_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Authorize User")
                        }
                    }
                }
            }
        }

        // Sub-Tabs: Users vs Audit Trail
        item {
            TabRow(selectedTabIndex = activeSubTab) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = { Text("Authorized Directory (${users.size})") },
                    icon = { Icon(Icons.Filled.People, contentDescription = null) }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = { Text("Audit Trail (${auditLogs.size})") },
                    icon = { Icon(Icons.Filled.History, contentDescription = null) }
                )
            }
        }

        if (activeSubTab == 0) {
            // User List Section
            if (users.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No authorized users registered yet.")
                    }
                }
            } else {
                items(users, key = { it.id }) { user ->
                    UserRowCard(
                        user = user,
                        onToggleStatus = { viewModel.toggleUserStatus(user.id) },
                        onChangeRole = { newRole -> viewModel.changeUserRole(user.id, newRole) },
                        onRevokeAccess = { viewModel.revokeUserAccess(user.id) }
                    )
                }
            }
        } else {
            // Audit Log Section
            if (auditLogs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No administrative audit events recorded yet.")
                    }
                }
            } else {
                items(auditLogs, key = { it.id }) { log ->
                    AuditLogRowCard(log = log)
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = count,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
private fun UserRowCard(
    user: AuthorizedUserEntity,
    onToggleStatus: () -> Unit,
    onChangeRole: (String) -> Unit,
    onRevokeAccess: () -> Unit
) {
    val isBootstrapAdmin = user.email.equals("adv.akash2356@gmail.com", ignoreCase = true)
    var showActionDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.status == "ACTIVE") MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            1.dp,
            if (isBootstrapAdmin) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth().testTag("user_row_${user.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (user.role == "ADMIN") MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = user.displayName.ifBlank { user.email }.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = if (user.role == "ADMIN") MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = user.displayName.ifBlank { user.email.substringBefore("@") },
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (isBootstrapAdmin) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "BOOTSTRAP",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Black),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { showActionDialog = true },
                    modifier = Modifier.testTag("user_action_menu_${user.id}")
                ) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Manage User")
                }
            }

            // Status & Role Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Role Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (user.role == "ADMIN") MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = user.role,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (user.role == "ADMIN") MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (user.status == "ACTIVE") Color(0xFFE6F4EA) else Color(0xFFFCE8E6)
                ) {
                    Text(
                        text = user.status,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (user.status == "ACTIVE") Color(0xFF137333) else Color(0xFFC5221F),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Last Login or Date Added
                val timeFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
                val lastLoginText = user.lastLoginAt?.let { "Login: ${timeFormat.format(Date(it))}" } ?: "Never logged in"
                Text(
                    text = lastLoginText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Google Subject ID (if linked)
            user.googleSubjectId?.let { subId ->
                Text(
                    text = "Google Sub ID: $subId",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }

    if (showActionDialog) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Manage User: ${user.email}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isBootstrapAdmin) {
                        Text(
                            "This account is the primary bootstrap administrator. It cannot be deactivated, demoted, or removed to protect system availability.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Select an administrative action:")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showActionDialog = false }) {
                    Text("Close")
                }
            },
            dismissButton = {
                if (!isBootstrapAdmin) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = {
                                val nextRole = if (user.role == "ADMIN") "USER" else "ADMIN"
                                onChangeRole(nextRole)
                                showActionDialog = false
                            }
                        ) {
                            Text(if (user.role == "ADMIN") "Demote to USER" else "Promote to ADMIN")
                        }
                        TextButton(
                            onClick = {
                                onToggleStatus()
                                showActionDialog = false
                            }
                        ) {
                            Text(if (user.status == "ACTIVE") "Deactivate" else "Reactivate")
                        }
                        TextButton(
                            onClick = {
                                onRevokeAccess()
                                showActionDialog = false
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Revoke Access")
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun AuditLogRowCard(log: UserAuditLogEntity) {
    val timeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().testTag("audit_log_${log.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val icon: androidx.compose.ui.graphics.vector.ImageVector = when (log.action) {
                "USER_ADDED" -> Icons.Filled.PersonAdd
                "USER_DEACTIVATED" -> Icons.Filled.PersonOff
                "USER_REACTIVATED" -> Icons.Filled.Person
                "USER_REMOVED" -> Icons.Filled.Delete
                "LOGIN" -> Icons.Filled.CheckCircle
                else -> Icons.Filled.Info
            }
            val color: Color = when (log.action) {
                "USER_ADDED" -> MaterialTheme.colorScheme.primary
                "USER_DEACTIVATED", "USER_REMOVED" -> MaterialTheme.colorScheme.error
                "USER_REACTIVATED", "LOGIN" -> Color(0xFF137333)
                else -> MaterialTheme.colorScheme.secondary
            }

            Surface(shape = CircleShape, color = color.copy(alpha = 0.15f), modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.action,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = color
                    )
                    Text(
                        text = timeFormat.format(Date(log.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Actor: ${log.actorUserId} → Target: ${log.targetUserId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (log.metadata.isNotBlank()) {
                    Text(
                        text = log.metadata,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
