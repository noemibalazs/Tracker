package com.noemi.tracker.repository

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.noemi.tracker.model.OutCome
import com.noemi.tracker.model.RegisterResponse
import com.noemi.tracker.model.ResendEmailVerification
import com.noemi.tracker.model.ResetPassword
import com.noemi.tracker.model.SignInResponse
import com.noemi.tracker.model.SignOutResponse
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) : AuthRepository {

    override suspend fun signIn(email: String, password: String): SignInResponse {
        return try {
            val firebaseAuthResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            firebaseAuthResult.user?.let { user ->
                println("SignIn - user unique id :${user.uid} - email verified: ${user.isEmailVerified}")
            }
            OutCome.Success(firebaseAuthResult)
        } catch (e: FirebaseException) {
            val error = "SignIn error ${e.localizedMessage}"
            println(error)
            OutCome.Error(error)
        }
    }

    override suspend fun register(email: String, password: String): RegisterResponse {
        return try {
            val firebaseAuthResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            firebaseAuthResult.user?.let { user ->
                println("User registered - unique id :${user.uid} - email verified: ${user.isEmailVerified}")
            }
            OutCome.Success(firebaseAuthResult)
        } catch (e: FirebaseException) {
            val error = "Register error ${e.localizedMessage}"
            println(error)
            OutCome.Error(error)
        }
    }

    override suspend fun signOut(): SignOutResponse {
        return try {
            firebaseAuth.signOut()
            OutCome.Success(true)
        } catch (e: FirebaseException) {
            val error = "SignOut error ${e.localizedMessage}"
            println(error)
            OutCome.Error(error)
        }
    }

    override suspend fun resendEmailVerification(): ResendEmailVerification {
        return try {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            OutCome.Success(true)
        } catch (e: FirebaseException) {
            val error = "Resend email verification error ${e.localizedMessage}"
            println(error)
            OutCome.Error(error)
        }
    }

    override suspend fun resetPassword(email: String): ResetPassword {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            OutCome.Success(true)
        } catch (e: Exception) {
            val error = when (e) {
                is FirebaseAuthInvalidUserException -> e.localizedMessage
                is FirebaseException -> e.localizedMessage
                else -> e.localizedMessage
            }
            OutCome.Error(error ?: "Reset password exception")
        }
    }
}