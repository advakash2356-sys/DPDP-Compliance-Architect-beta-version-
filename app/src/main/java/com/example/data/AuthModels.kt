package com.example.data

/**
 * Verified identity returned by Google Identity Provider.
 * Holds Google's stable unique subject ID, verified email, and profile name.
 */
data class GoogleVerifiedIdentity(
    val subjectId: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null
)

/**
 * State representing the active authentication & authorization lifecycle.
 */
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Authenticating : AuthState()
    data class Authorized(
        val user: AuthorizedUserEntity,
        val identity: GoogleVerifiedIdentity
    ) : AuthState()
    data class AccessDenied(
        val attemptedEmail: String,
        val reason: String
    ) : AuthState()
}
