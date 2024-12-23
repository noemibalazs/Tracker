package com.noemi.tracker.repository

import com.noemi.tracker.model.RegisterResponse
import com.noemi.tracker.model.ResendEmailVerification
import com.noemi.tracker.model.ResetPassword
import com.noemi.tracker.model.SignInResponse
import com.noemi.tracker.model.SignOutResponse

interface AuthRepository {

    suspend fun signIn(email: String, password: String): SignInResponse
    suspend fun register(email: String, password: String): RegisterResponse
    suspend fun signOut(): SignOutResponse
    suspend fun resendEmailVerification(): ResendEmailVerification
    suspend fun resetPassword(email: String): ResetPassword
}