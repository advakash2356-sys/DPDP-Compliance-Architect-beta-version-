package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.AuthState
import com.example.data.AuthorizedUserEntity
import com.example.data.ComplianceRepository
import com.example.ui.ComplianceViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthAndAccessControlTest {

    private lateinit var application: Application
    private lateinit var database: AppDatabase
    private lateinit var repository: ComplianceRepository

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        database = AppDatabase.getDatabase(application)
        repository = ComplianceRepository(database)
    }

    @Test
    fun testBootstrapAdminSeeding() = runBlocking {
        repository.seedInitialAdminIfEmpty()
        val admin = repository.getUserByEmail("adv.akash2356@gmail.com")
        assertNotNull(admin)
        assertEquals("ADMIN", admin?.role)
        assertEquals("ACTIVE", admin?.status)
        assertTrue(admin?.authorized == true)
    }

    @Test
    fun testUnauthorizedAccountRejected() = runBlocking {
        repository.seedInitialAdminIfEmpty()
        val viewModel = ComplianceViewModel(application)

        viewModel.signInWithGoogle("unauthorized.tester@gmail.com")
        // Allow coroutines to complete
        Thread.sleep(200)

        val state = viewModel.authState.value
        assertTrue("Expected AccessDenied state", state is AuthState.AccessDenied)
        val denied = state as AuthState.AccessDenied
        assertEquals("unauthorized.tester@gmail.com", denied.attemptedEmail)
    }

    @Test
    fun testInitialAdminSignInSuccess() = runBlocking {
        repository.seedInitialAdminIfEmpty()
        val viewModel = ComplianceViewModel(application)

        viewModel.signInWithGoogle("Adv.akash2356@gmail.com")
        Thread.sleep(200)

        val state = viewModel.authState.value
        assertTrue("Expected Authorized state for initial admin", state is AuthState.Authorized)
        val authorized = state as AuthState.Authorized
        assertEquals("adv.akash2356@gmail.com", authorized.user.email)
        assertEquals("ADMIN", authorized.user.role)
        assertEquals("ACTIVE", authorized.user.status)
        assertNotNull(authorized.user.googleSubjectId)
    }

    @Test
    fun testAdminCanAddUserAndUserCanSignIn() = runBlocking {
        repository.seedInitialAdminIfEmpty()
        val viewModel = ComplianceViewModel(application)

        // 1. Sign in as Admin
        viewModel.signInWithGoogle("Adv.akash2356@gmail.com")
        Thread.sleep(200)

        // 2. Admin adds a new authorized user
        viewModel.addAuthorizedUser("teammate@gmail.com", "USER")
        Thread.sleep(200)

        val addedUser = repository.getUserByEmail("teammate@gmail.com")
        assertNotNull(addedUser)
        assertEquals("USER", addedUser?.role)
        assertEquals("ACTIVE", addedUser?.status)

        // 3. Verify audit log was recorded
        val auditLogs = repository.allUserAuditLogs.first()
        assertTrue(auditLogs.any { it.action == "USER_ADDED" && it.targetUserId == "teammate@gmail.com" })

        // 4. Sign out Admin and sign in as newly added user
        viewModel.signOut()
        assertEquals(AuthState.Unauthenticated, viewModel.authState.value)

        viewModel.signInWithGoogle("teammate@gmail.com")
        Thread.sleep(200)

        val teammateState = viewModel.authState.value
        assertTrue(teammateState is AuthState.Authorized)
        val authTeammate = teammateState as AuthState.Authorized
        assertEquals("teammate@gmail.com", authTeammate.user.email)
        assertEquals("USER", authTeammate.user.role)
    }

    @Test
    fun testDeactivationAndReactivationFlow() = runBlocking {
        repository.seedInitialAdminIfEmpty()
        val viewModel = ComplianceViewModel(application)

        // Sign in as Admin
        viewModel.signInWithGoogle("Adv.akash2356@gmail.com")
        Thread.sleep(200)

        // Add user
        viewModel.addAuthorizedUser("auditor.staff@gmail.com", "USER")
        Thread.sleep(200)
        val user = repository.getUserByEmail("auditor.staff@gmail.com")!!

        // Deactivate user
        viewModel.toggleUserStatus(user.id)
        Thread.sleep(200)

        val deactivatedUser = repository.getUserByEmail("auditor.staff@gmail.com")!!
        assertEquals("INACTIVE", deactivatedUser.status)
        assertFalse(deactivatedUser.authorized)

        // Attempt login with deactivated user -> must be denied
        viewModel.signOut()
        viewModel.signInWithGoogle("auditor.staff@gmail.com")
        Thread.sleep(200)

        assertTrue(viewModel.authState.value is AuthState.AccessDenied)

        // Admin logs back in and reactivates user
        viewModel.signInWithGoogle("Adv.akash2356@gmail.com")
        Thread.sleep(200)

        viewModel.toggleUserStatus(user.id)
        Thread.sleep(200)

        val reactivatedUser = repository.getUserByEmail("auditor.staff@gmail.com")!!
        assertEquals("ACTIVE", reactivatedUser.status)
        assertTrue(reactivatedUser.authorized)

        // Auditor staff can now log back in
        viewModel.signOut()
        viewModel.signInWithGoogle("auditor.staff@gmail.com")
        Thread.sleep(200)

        assertTrue(viewModel.authState.value is AuthState.Authorized)
    }

    @Test
    fun testBootstrapAdminCannotBeDeactivatedOrDemoted() = runBlocking {
        repository.seedInitialAdminIfEmpty()
        val viewModel = ComplianceViewModel(application)

        viewModel.signInWithGoogle("Adv.akash2356@gmail.com")
        Thread.sleep(200)

        val admin = repository.getUserByEmail("adv.akash2356@gmail.com")!!

        // Try deactivating bootstrap admin
        viewModel.toggleUserStatus(admin.id)
        Thread.sleep(200)

        val unchangedAdmin = repository.getUserByEmail("adv.akash2356@gmail.com")!!
        assertEquals("ACTIVE", unchangedAdmin.status)
        assertTrue(unchangedAdmin.authorized)

        // Try demoting bootstrap admin
        viewModel.changeUserRole(admin.id, "USER")
        Thread.sleep(200)

        val stillAdmin = repository.getUserByEmail("adv.akash2356@gmail.com")!!
        assertEquals("ADMIN", stillAdmin.role)

        // Try revoking bootstrap admin
        viewModel.revokeUserAccess(admin.id)
        Thread.sleep(200)

        val stillExists = repository.getUserByEmail("adv.akash2356@gmail.com")
        assertNotNull(stillExists)
    }
}
