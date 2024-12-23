package com.noemi.tracker.viewmodel

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noemi.tracker.base.BaseAuthViewModel
import com.noemi.tracker.model.AuthState
import com.noemi.tracker.model.OutCome
import com.noemi.tracker.model.UIAuthState
import com.noemi.tracker.model.UIAuthEvent
import com.noemi.tracker.repository.AuthRepository
import com.noemi.tracker.providers.DispatcherProvider
import com.noemi.tracker.utils.isErrorOrNull
import com.noemi.tracker.utils.isSuccessOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import android.util.Patterns.EMAIL_ADDRESS
import android.provider.Settings.ACTION_ADD_ACCOUNT
import com.noemi.tracker.manager.DataManager
import com.noemi.tracker.model.UserDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val dataManager: DataManager
) :
    BaseAuthViewModel<UIAuthEvent>() {

    var emailAddress by mutableStateOf("")
        private set

    fun updateEmailAddress(input: String) {
        emailAddress = input
    }

    val emailHasError by derivedStateOf {
        when (emailAddress.isNotEmpty()) {
            true -> !EMAIL_ADDRESS.matcher(emailAddress).matches()
            else -> false
        }
    }

    var password by mutableStateOf("")
        private set

    fun updatePassword(input: String) {
        password = input
    }

    val passwordHasError by derivedStateOf {
        when (password.isNotEmpty()) {
            true -> {
                val regex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,32}\$")
                !regex.matches(password)
            }

            else -> false
        }
    }

    val loginEnabled by derivedStateOf {
        (emailAddress.isNotEmpty() && !emailHasError) && (password.isNotEmpty() && !passwordHasError)
    }

    var confirmPassword by mutableStateOf("")
        private set

    fun updateConfirmedPassword(input: String) {
        confirmPassword = input
    }

    val confirmedPasswordHasError by derivedStateOf {
        when (confirmPassword.isNotEmpty()) {
            true -> {
                val regex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,32}\$")
                !regex.matches(confirmPassword)
            }

            else -> false
        }
    }

    val signUpEnabled by derivedStateOf {
        (emailAddress.isNotEmpty() && !emailHasError) &&
                (password.isNotEmpty() && !passwordHasError) &&
                (confirmPassword.isNotEmpty() && !confirmedPasswordHasError) &&
                password == confirmPassword
    }

    override suspend fun handleEvent(event: UIAuthEvent) {
        when (event) {
            is UIAuthEvent.Register -> registerUser(event.email, event.password)
            is UIAuthEvent.SignIn -> signInUser(event.email, event.password)
            is UIAuthEvent.SignOut -> signOutUser()
            is UIAuthEvent.ResendVerificationEmail -> resendVerificationEmail()
            is UIAuthEvent.GoogleSignIn -> handleGoogleSignIn(event.response)
            is UIAuthEvent.ResetPassword -> resetPassword(event.email)
            is UIAuthEvent.AuthState -> verifyUserAuthState()
        }
    }

    private suspend fun registerUser(email: String, password: String) {
        updateUIState { UIAuthState(isLoading = true) }
        delay(900)

        viewModelScope.launch(dispatcherProvider.io()) {

            val response = authRepository.register(email, password)
            handleUIState(true, response)
        }
    }

    private suspend fun signInUser(email: String, password: String) {
        updateUIState { UIAuthState(isLoading = true) }
        delay(900)

        viewModelScope.launch(dispatcherProvider.io()) {
            val response = authRepository.signIn(email, password)
            handleUIState(false, response)
        }
    }

    private suspend fun signOutUser() {
        updateUIState { UIAuthState(isLoading = true) }
        delay(900)

        viewModelScope.launch(dispatcherProvider.io()) {
            val response = authRepository.signOut()
            response.isSuccessOrNull()?.let {
                updateUIState {
                    UIAuthState(
                        isLoading = false,
                        errorMessage = null,
                        user = null,
                        authState = AuthState.SignedOut,
                        alreadyRegistered = false,
                        isAuthenticated = false,
                        isAnonymous = false
                    )
                }
                dataManager.setUserDetails(UserDetails())
            }
            response.isErrorOrNull()?.let { error ->
                UIAuthState(
                    errorMessage = error,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun resendVerificationEmail() {
        updateUIState { UIAuthState(isLoading = true) }
        delay(900)

        viewModelScope.launch(dispatcherProvider.io()) {
            val response = authRepository.resendEmailVerification()
            response.isSuccessOrNull()?.let {
                updateUIState {
                    UIAuthState(
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            response.isErrorOrNull()?.let { error ->
                updateUIState {
                    UIAuthState(
                        isLoading = false,
                        errorMessage = error
                    )
                }
            }
        }
    }

    private suspend fun resetPassword(email: String) {
        updateUIState { UIAuthState(isLoading = true) }
        delay(900)

        viewModelScope.launch(dispatcherProvider.io()) {
            val result = authRepository.resetPassword(email)

            result.isSuccessOrNull()?.let {
                updateUIState { UIAuthState(isLoading = false, errorMessage = "We have sent a password reset link to your email address.") }
            }
            result.isErrorOrNull()?.let {
                updateUIState { UIAuthState(isLoading = false, errorMessage = it) }
            }
        }
    }

    private suspend fun handleGoogleSignIn(result: GetCredentialResponse) {
        updateUIState { UIAuthState(isLoading = true) }
        delay(900)

        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val idTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = idTokenCredential.idToken
                        val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                        val user = Firebase.auth.signInWithCredential(authCredential).await().user

                        user?.let {
                            updateUIState {
                                UIAuthState(
                                    isLoading = false,
                                    errorMessage = null,
                                    user = user,
                                    isAnonymous = user.isAnonymous,
                                    isAuthenticated = true,
                                    alreadyRegistered = false,
                                    authState = if (user.isAnonymous) AuthState.Anonymous else AuthState.Authenticated
                                )
                            }

                            dataManager.setUserDetails(
                                UserDetails(
                                    email = user.email,
                                    name = user.displayName,
                                    avatar = user.photoUrl.toString()
                                )
                            )
                        }
                    } catch (e: Exception) {
                        val error = when (e) {
                            is GoogleIdTokenParsingException -> e.localizedMessage
                            is FirebaseAuthException -> e.localizedMessage
                            else -> e.localizedMessage
                        }
                        updateUIState {
                            UIAuthState(
                                isLoading = false,
                                errorMessage = error
                            )
                        }
                    }
                }
            }

            else -> println("Unexpected type of credentials")
        }
    }

    private suspend fun handleUIState(newlyRegistered: Boolean, result: OutCome<AuthResult>) {
        result.isSuccessOrNull()?.user?.let { customer ->

            if (newlyRegistered && !customer.isEmailVerified) {

                customer.sendEmailVerification().addOnCompleteListener { task ->
                    suspend { task.await() }

                    when (task.isSuccessful) {
                        true -> suspend {
                            dataManager.setUserDetails(
                                UserDetails(
                                    email = customer.email,
                                    avatar = customer.photoUrl?.toString(),
                                    name = customer.displayName
                                )
                            )
                        }

                        else -> println("Email verification failed")
                    }
                }
            } else {
                dataManager.setUserDetails(
                    UserDetails(
                        email = customer.email,
                        avatar = customer.photoUrl?.toString(),
                        name = customer.displayName
                    )
                )
            }

            updateUIState {
                UIAuthState(
                    isLoading = false,
                    errorMessage = null,
                    user = customer,
                    alreadyRegistered = newlyRegistered,
                    isAnonymous = customer.isAnonymous,
                    isAuthenticated = true,
                    authState = if (customer.isAnonymous) AuthState.Anonymous else AuthState.Authenticated
                )
            }
        }

        result.isErrorOrNull()?.let { error ->
            updateUIState {
                UIAuthState(
                    isLoading = false,
                    errorMessage = error
                )
            }
        }
    }

    fun addGoogleAccountIntent(): Intent {
        val intent = Intent(ACTION_ADD_ACCOUNT)
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        return intent
    }

    fun handleFailure(error: GetCredentialException) {
        updateUIState {
            UIAuthState(isLoading = false, errorMessage = error.localizedMessage)
        }
    }

    fun onGoogleSingInClicked() {
        viewModelScope.launch {
            updateUIState { UIAuthState(isLoading = true) }
            delay(900)
        }
    }

    private suspend fun verifyUserAuthState() {
        updateUIState { UIAuthState(isLoading = true) }
        delay(900)

        viewModelScope.launch {
            val result = getUserDetails()

            when (!result.email.isNullOrEmpty()) {
                true -> updateUIState {
                    UIAuthState(
                        isLoading = false,
                        authState = AuthState.Authenticated,
                        errorMessage = null,
                        alreadyRegistered = false
                    )
                }

                else -> updateUIState {
                    UIAuthState(
                        isLoading = false,
                        alreadyRegistered = false
                    )
                }
            }
        }
    }

    suspend fun getUserDetails() = dataManager.getUserDetails()
}