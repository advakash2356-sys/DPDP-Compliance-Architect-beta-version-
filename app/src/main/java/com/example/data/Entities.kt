package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "laws")
data class LawEntity(
    @PrimaryKey val lawId: String, // UUID
    val title: String,
    val shortName: String,
    val actNumber: String?,
    val enactedDate: Long?,
    val status: String, // ENACTED, DRAFT
    val officialSourceUrl: String,
    val lastVerifiedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "law_sections",
    foreignKeys = [
        ForeignKey(
            entity = LawEntity::class,
            parentColumns = ["lawId"],
            childColumns = ["lawId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lawId")]
)
data class LawSectionEntity(
    @PrimaryKey val sectionId: String, // UUID
    val lawId: String,
    val sectionNumber: String,
    val sectionTitle: String,
    val sectionText: String,
    val penaltyProvision: String?,
    val sourceUrl: String,
    val lastVerifiedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "legal_updates")
data class LegalUpdateEntity(
    @PrimaryKey val id: String, // UUID
    val title: String,
    val description: String,
    val sourceUrl: String,
    val datePosted: Long,
    val lawReference: String,
    val severity: String // CRITICAL, HIGH, MEDIUM, LOW
)@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val uid: String = "local_user",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val hasAccountConsent: Boolean = false,
    val hasMarketingConsent: Boolean = false,
    val isOptedInOptionalTelemetry: Boolean = true,
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "consent_logs")
data class ConsentLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val consentType: String, // e.g., "ACCOUNT_CREATION", "MARKETING_OPT_IN", "TELEMETRY_OPT_OUT", "DPDP_RIGHTS_EXERCISE"
    val action: String,      // e.g., "GRANTED", "REVOKED", "ERASED"
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "consent_records")
data class ConsentRecord(
    @PrimaryKey(autoGenerate = true) val recordId: Long = 0,
    val dataPrincipalId: String = "local_user",
    val consentType: String, // e.g. "DATA_PROCESSING", "TELEMETRY", "THIRD_PARTY_SHARING", "MARKETING"
    val purposeDescription: String,
    val noticeVersion: String = "DPDP-2023-v1.0",
    val isGranted: Boolean,
    val grantedAt: Long = System.currentTimeMillis(),
    val revokedAt: Long? = null,
    val statutoryBasis: String = "DPDP Act 2023 Sec 6(1)"
)

@Entity(tableName = "nominees")
data class Nominee(
    @PrimaryKey val id: Int = 1, // Single active nominee representation
    val nomineeName: String,
    val relationship: String,
    val contactEmail: String,
    val contactPhone: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "grievance_reports")
data class GrievanceReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reporterName: String,
    val reporterContact: String,
    val grievanceSubject: String,
    val grievanceDetails: String,
    val status: String = "SUBMITTED", // SUBMITTED, UNDER_REVIEW, RESOLVED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_tasks")
data class AuditTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val packageName: String,
    val violationSummary: String,
    val wipeCode: String,
    val semiCodeInstructions: String,
    val sectionsViolated: String, // e.g. "Section 6, Section 9"
    val riskLevel: String, // HIGH, MEDIUM, LOW
    val isResolved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "compliance_checklist_items")
data class ComplianceChecklistItem(
    @PrimaryKey val id: String, // Unique identifier e.g., "dpdpa_sec5_notice"
    val actName: String,     // e.g., "Digital Personal Data Protection Act 2023", "Proposed Digital India Act (DIA)", etc.
    val sectionName: String, // e.g., "Section 5 - Notice prior to collection", "SPDI Rules (Sec 4) - Privacy Policy"
    val description: String, // Description of compliance standard
    val scheduleDay: String,  // e.g., "Monday", "Tuesday", "Wednesday", etc.
    val isCompleted: Boolean = false,
    val penaltyDetails: String = "", // e.g., "Up to ₹250 Crores for data breach"
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * AuditLog entity providing an immutable, tamper-evident record of all
 * compliance-related activities, rights exercises, consent modifications,
 * and data lifecycle events under the DPDP Act 2023.
 */
@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val actionType: String, // e.g. "CONSENT_RECORDED", "CONSENT_REVOKED", "DATA_ERASED", "RIGHTS_EXERCISE", "VULNERABILITY_AUDIT", "GRIEVANCE_LOGGED"
    val entityAffected: String, // e.g. "ConsentRecord", "UserProfile", "Nominee", "DataVault"
    val actor: String = "DATA_PRINCIPAL", // e.g. "DATA_PRINCIPAL", "DATA_FIDUCIARY", "COMPLIANCE_OFFICER", "SYSTEM"
    val description: String,
    val statutoryReference: String = "DPDP Act 2023", // e.g. "Section 5", "Section 6", "Section 12(3)", "Section 14"
    val auditHash: String, // Cryptographic verification token or digest for tamper-evidence
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Entity representing an authorized Google user in the private access control layer.
 * A valid Google account alone does not grant access; it must exist here with authorized = true and status = ACTIVE.
 */
@Entity(
    tableName = "authorized_users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["googleSubjectId"])
    ]
)
data class AuthorizedUserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val googleSubjectId: String? = null,
    val email: String, // canonical lowercase email address
    val displayName: String = "",
    val profilePhotoUrl: String? = null,
    val role: String = "USER", // "ADMIN" or "USER"
    val status: String = "ACTIVE", // "ACTIVE", "INACTIVE", "REVOKED"
    val authorized: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null,
    val createdBy: String = "SYSTEM_BOOTSTRAP"
)

/**
 * Immutable audit log recording administrative and authentication access events.
 */
@Entity(tableName = "user_audit_logs")
data class UserAuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actorUserId: String, // email or sub ID of who executed the action
    val targetUserId: String, // email or id of the affected user
    val action: String, // "USER_ADDED", "USER_DEACTIVATED", "USER_REACTIVATED", "ROLE_CHANGED", "USER_REMOVED", "LOGIN", "UNAUTHORIZED_ATTEMPT"
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: String = ""
)


