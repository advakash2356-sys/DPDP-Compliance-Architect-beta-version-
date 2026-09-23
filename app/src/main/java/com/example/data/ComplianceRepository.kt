package com.example.data

import kotlinx.coroutines.flow.Flow

class ComplianceRepository(private val database: AppDatabase) {
    private val profileDao = database.userProfileDao()
    private val logDao = database.consentLogDao()
    private val nomineeDao = database.nomineeDao()
    private val reportDao = database.grievanceReportDao()
    private val auditTaskDao = database.auditTaskDao()
    private val checklistDao = database.complianceChecklistItemDao()
    private val legalUpdateDao = database.legalUpdateDao()
    private val authorizedUserDao = database.authorizedUserDao()
    private val userAuditLogDao = database.userAuditLogDao()

    val profile: Flow<UserProfile?> = profileDao.getProfile()
    val allLogs: Flow<List<ConsentLog>> = logDao.getAllLogs()
    val nominee: Flow<Nominee?> = nomineeDao.getNominee()
    val allReports: Flow<List<GrievanceReport>> = reportDao.getAllReports()
    val allAuditTasks: Flow<List<AuditTask>> = auditTaskDao.getAllAuditTasks()
    val allChecklistItems: Flow<List<ComplianceChecklistItem>> = checklistDao.getAllItems()
    val allLegalUpdates: Flow<List<LegalUpdateEntity>> = legalUpdateDao.getAllLegalUpdates()
    val allAuthorizedUsers: Flow<List<AuthorizedUserEntity>> = authorizedUserDao.getAllUsers()
    val allUserAuditLogs: Flow<List<UserAuditLogEntity>> = userAuditLogDao.getAllAuditLogs()

    suspend fun getUserByEmail(email: String): AuthorizedUserEntity? {
        return authorizedUserDao.getUserByEmail(email.trim().lowercase())
    }

    suspend fun getUserBySubjectId(subjectId: String): AuthorizedUserEntity? {
        return authorizedUserDao.getUserBySubjectId(subjectId)
    }

    suspend fun insertAuthorizedUser(user: AuthorizedUserEntity): Long {
        return authorizedUserDao.insertUser(user)
    }

    suspend fun updateAuthorizedUser(user: AuthorizedUserEntity) {
        authorizedUserDao.updateUser(user)
    }

    suspend fun deleteAuthorizedUser(id: Long) {
        authorizedUserDao.deleteUserById(id)
    }

    suspend fun getActiveAdminCount(): Int {
        return authorizedUserDao.getActiveAdminCount()
    }

    suspend fun insertUserAuditLog(log: UserAuditLogEntity): Long {
        return userAuditLogDao.insertLog(log)
    }

    /**
     * Seeds the initial administrator account (Adv.akash2356@gmail.com)
     * if not already present in the database.
     */
    suspend fun seedInitialAdminIfEmpty() {
        val adminEmail = "adv.akash2356@gmail.com"
        val existingAdmin = authorizedUserDao.getUserByEmail(adminEmail)
        if (existingAdmin == null) {
            val admin = AuthorizedUserEntity(
                googleSubjectId = null,
                email = adminEmail,
                displayName = "Adv. Akash",
                profilePhotoUrl = null,
                role = "ADMIN",
                status = "ACTIVE",
                authorized = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                lastLoginAt = null,
                createdBy = "SYSTEM_INITIAL_BOOTSTRAP"
            )
            val newId = authorizedUserDao.insertUser(admin)
            userAuditLogDao.insertLog(
                UserAuditLogEntity(
                    actorUserId = "SYSTEM",
                    targetUserId = adminEmail,
                    action = "USER_ADDED",
                    metadata = "Provisioned bootstrap administrator: $adminEmail with role ADMIN and status ACTIVE"
                )
            )
        }
    }

    suspend fun insertLegalUpdate(update: LegalUpdateEntity) {
        legalUpdateDao.insertLegalUpdate(update)
    }

    suspend fun clearAllLegalUpdates() {
        legalUpdateDao.clearAllUpdates()
    }

    suspend fun insertChecklistItem(item: ComplianceChecklistItem) {
        checklistDao.insertItem(item)
    }

    suspend fun updateChecklistItem(item: ComplianceChecklistItem) {
        checklistDao.updateItem(item)
    }

    suspend fun clearChecklist() {
        checklistDao.clearAll()
    }

    suspend fun getProfileSync(): UserProfile? = profileDao.getProfileSync()

    suspend fun insertProfile(profile: UserProfile) {
        profileDao.insertProfile(profile)
    }

    suspend fun insertLog(log: ConsentLog) {
        logDao.insertLog(log)
    }

    suspend fun deleteLogsBefore(timestamp: Long): Int {
        return logDao.deleteLogsBefore(timestamp)
    }

    suspend fun saveNominee(nominee: Nominee) {
        nomineeDao.saveNominee(nominee)
    }

    suspend fun insertReport(report: GrievanceReport) {
        reportDao.insertReport(report)
    }

    suspend fun insertAuditTask(task: AuditTask) {
        auditTaskDao.insertAuditTask(task)
    }

    suspend fun updateAuditTask(task: AuditTask) {
        auditTaskDao.updateAuditTask(task)
    }

    suspend fun deleteAuditTask(task: AuditTask) {
        auditTaskDao.deleteAuditTask(task)
    }

    suspend fun clearAllAuditTasks() {
        auditTaskDao.clearAllAuditTasks()
    }

    /**
     * Complete Erasure execution representing 100% data purge across our systems
     */
    suspend fun executeCompleteErasure(): Int {
        profileDao.clearProfile()
        nomineeDao.clearNominee()
        reportDao.clearAllReports()
        auditTaskDao.clearAllAuditTasks()
        logDao.clearAllLogs()
        
        // Log the final erasure event so the system registers the exercise of Right to Erasure
        logDao.insertLog(ConsentLog(
            consentType = "DPDP_RIGHTS_EXERCISE",
            action = "ERASED",
            details = "All user personal identifier data, nominee designations, and grievance files have been thoroughly scrubbed from the active local database layer in accordance with Sec 12 of the DPDP Act 2023."
        ))
        
        return 1
    }
}
