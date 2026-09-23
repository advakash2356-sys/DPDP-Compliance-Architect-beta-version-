package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserProfile::class,
        ConsentLog::class,
        ConsentRecord::class,
        Nominee::class,
        GrievanceReport::class,
        AuditTask::class,
        ComplianceChecklistItem::class,
        LawEntity::class,
        LawSectionEntity::class,
        LegalUpdateEntity::class,
        AuditLog::class,
        AuthorizedUserEntity::class,
        UserAuditLogEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun consentLogDao(): ConsentLogDao
    abstract fun consentRecordDao(): ConsentRecordDao
    abstract fun nomineeDao(): NomineeDao
    abstract fun grievanceReportDao(): GrievanceReportDao
    abstract fun auditTaskDao(): AuditTaskDao
    abstract fun complianceChecklistItemDao(): ComplianceChecklistItemDao
    abstract fun lawDao(): LawDao
    abstract fun lawSectionDao(): LawSectionDao
    abstract fun legalUpdateDao(): LegalUpdateDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun authorizedUserDao(): AuthorizedUserDao
    abstract fun userAuditLogDao(): UserAuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Safe schema transition for historic user audits
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Safe schema transition for checklist items
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dpdp_safeguard_db_v4"
                )
                .fallbackToDestructiveMigration(dropAllTables = false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
