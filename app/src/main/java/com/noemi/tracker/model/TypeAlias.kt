package com.noemi.tracker.model

import com.google.firebase.auth.AuthResult

typealias SignInResponse = OutCome<AuthResult>
typealias RegisterResponse = OutCome<AuthResult>
typealias SignOutResponse = OutCome<Boolean>
typealias ResendEmailVerification = OutCome<Boolean>
typealias ResetPassword = OutCome<Boolean>