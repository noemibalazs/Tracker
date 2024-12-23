package com.noemi.tracker.model

import com.google.firebase.auth.FirebaseUser

data class UIAuthState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val isAnonymous: Boolean = false,
    val alreadyRegistered: Boolean = false,
    val user: FirebaseUser? = null,
    val authState: AuthState? = null
)

enum class AuthState {
    Anonymous,
    Authenticated,
    SignedOut
}
