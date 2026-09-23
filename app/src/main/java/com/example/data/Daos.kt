package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE uid = 'local_user' LIMIT 1")
    fun getProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE uid = 'local_user' LIMIT 1")
    suspend fun getProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Query("DELETE FROM user_profiles WHERE uid = 'local_user'")
    suspend fun clearProfile()
}

@Dao
interface ConsentLogDao {
    @Query("SELECT * FROM consent_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ConsentLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ConsentLog)

    @Query("DELETE FROM consent_logs WHERE timestamp < :timestamp")
    suspend fun deleteLogsBefore(timestamp: Long): Int

    @Query("DELETE FROM consent_logs")
    suspend fun clearAllLogs()
}

@Dao
interface ConsentRecordDao {
    @Query("SELECT * FROM consent_records ORDER BY grantedAt DESC")
    fun getAllConsentRecords(): Flow<List<ConsentRecord>>

    @Query("SELECT * FROM consent_records WHERE dataPrincipalId = :userId ORDER BY grantedAt DESC")
    fun getRecordsForUser(userId: String): Flow<List<ConsentRecord>>

    @Query("SELECT * FROM consent_records WHERE consentType = :consentType AND dataPrincipalId = :userId ORDER BY grantedAt DESC LIMIT 1")
    suspend fun getLatestConsentRecord(consentType: String, userId: String = "local_user"): ConsentRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsentRecord(record: ConsentRecord): Long

    @Query("UPDATE consent_records SET isGranted = :isGranted, revokedAt = :revokedAt WHERE consentType = :consentType AND dataPrincipalId = :userId")
    suspend fun updateConsentStatus(consentType: String, isGranted: Boolean, revokedAt: Long?, userId: String = "local_user")

    @Query("DELETE FROM consent_records WHERE dataPrincipalId = :userId")
    suspend fun deleteRecordsForUser(userId: String)

    @Query("DELETE FROM consent_records")
    suspend fun clearAllRecords()
}

@Dao
interface NomineeDao {
    @Query("SELECT * FROM nominees WHERE id = 1 LIMIT 1")
    fun getNominee(): Flow<Nominee?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNominee(nominee: Nominee)

    @Query("DELETE FROM nominees WHERE id = 1")
    suspend fun clearNominee()
}

@Dao
interface GrievanceReportDao {
    @Query("SELECT * FROM grievance_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<GrievanceReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: GrievanceReport)

    @Query("DELETE FROM grievance_reports")
    suspend fun clearAllReports()
}

@Dao
interface AuditTaskDao {
    @Query("SELECT * FROM audit_tasks ORDER BY timestamp DESC")
    fun getAllAuditTasks(): Flow<List<AuditTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditTask(task: AuditTask)

    @Update
    suspend fun updateAuditTask(task: AuditTask)

    @Delete
    suspend fun deleteAuditTask(task: AuditTask)

    @Query("DELETE FROM audit_tasks")
    suspend fun clearAllAuditTasks()
}

@Dao
interface ComplianceChecklistItemDao {
    @Query("SELECT * FROM compliance_checklist_items ORDER BY id ASC")
    fun getAllItems(): Flow<List<ComplianceChecklistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ComplianceChecklistItem)

    @Update
    suspend fun updateItem(item: ComplianceChecklistItem)

    @Query("DELETE FROM compliance_checklist_items")
    suspend fun clearAll()
}

@Dao
interface LawDao {
    @Query("SELECT * FROM laws ORDER BY title ASC")
    fun getAllLaws(): Flow<List<LawEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaw(law: LawEntity)

    @Query("DELETE FROM laws")
    suspend fun clearAllLaws()
}
@Dao
interface LegalUpdateDao {
    @Query("SELECT * FROM legal_updates ORDER BY datePosted DESC")
    fun getAllLegalUpdates(): Flow<List<LegalUpdateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLegalUpdate(update: LegalUpdateEntity)

    @Query("DELETE FROM legal_updates")
    suspend fun clearAllUpdates()
}

@Dao
interface LawSectionDao {
    @Query("SELECT * FROM law_sections WHERE lawId = :lawId ORDER BY sectionNumber ASC")
    fun getSectionsForLaw(lawId: String): Flow<List<LawSectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: LawSectionEntity)

    @Query("DELETE FROM law_sections")
    suspend fun clearAllSections()
}

/**
 * AuditLogDao handles the persistence and retrieval of immutable compliance audit records
 * required for DPDP Act 2023 regulatory audits and statutory proof of compliance.
 */
@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs WHERE actionType = :actionType ORDER BY timestamp DESC")
    fun getAuditLogsByAction(actionType: String): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs WHERE statutoryReference LIKE '%' || :reference || '%' ORDER BY timestamp DESC")
    fun getAuditLogsByStatutoryRef(reference: String): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int): Flow<List<AuditLog>>

    @Query("SELECT COUNT(*) FROM audit_logs")
    fun getAuditLogCount(): Flow<Int>

    @Query("SELECT * FROM audit_logs WHERE logId = :id LIMIT 1")
    suspend fun getLogById(id: Long): AuditLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogs(logs: List<AuditLog>)

    @Query("DELETE FROM audit_logs")
    suspend fun clearAllAuditLogs()
}

@Dao
interface AuthorizedUserDao {
    @Query("SELECT * FROM authorized_users ORDER BY createdAt ASC")
    fun getAllUsers(): Flow<List<AuthorizedUserEntity>>

    @Query("SELECT * FROM authorized_users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): AuthorizedUserEntity?

    @Query("SELECT * FROM authorized_users WHERE googleSubjectId = :subjectId LIMIT 1")
    suspend fun getUserBySubjectId(subjectId: String): AuthorizedUserEntity?

    @Query("SELECT * FROM authorized_users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): AuthorizedUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AuthorizedUserEntity): Long

    @Update
    suspend fun updateUser(user: AuthorizedUserEntity)

    @Delete
    suspend fun deleteUser(user: AuthorizedUserEntity)

    @Query("DELETE FROM authorized_users WHERE id = :id")
    suspend fun deleteUserById(id: Long)

    @Query("SELECT COUNT(*) FROM authorized_users")
    suspend fun getUserCount(): Int

    @Query("SELECT COUNT(*) FROM authorized_users WHERE role = 'ADMIN' AND authorized = 1 AND status = 'ACTIVE'")
    suspend fun getActiveAdminCount(): Int
}

@Dao
interface UserAuditLogDao {
    @Query("SELECT * FROM user_audit_logs ORDER BY timestamp DESC LIMIT 250")
    fun getAllAuditLogs(): Flow<List<UserAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: UserAuditLogEntity): Long

    @Query("DELETE FROM user_audit_logs")
    suspend fun clearAllAuditLogs()
}


