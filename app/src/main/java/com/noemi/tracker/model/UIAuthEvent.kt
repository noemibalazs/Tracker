package com.noemi.tracker.model

import androidx.credentials.GetCredentialResponse

sealed interface UIAuthEvent {

    data class SignIn(val email: String, val password: String) : UIAuthEvent
    data class Register(val email: String, val password: String) : UIAuthEvent
    data class GoogleSignIn(val response: GetCredentialResponse) : UIAuthEvent
    data object SignOut : UIAuthEvent
    data object ResendVerificationEmail : UIAuthEvent
    data class ResetPassword(val email: String) : UIAuthEvent
    data object AuthState : UIAuthEvent
}