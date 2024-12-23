package com.noemi.tracker.screens.login

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.noemi.tracker.BuildConfig
import com.noemi.tracker.R
import com.noemi.tracker.model.AuthState
import com.noemi.tracker.model.UIAuthEvent
import com.noemi.tracker.providers.NonceProvider
import com.noemi.tracker.screens.tracker.TrackerActivity
import com.noemi.tracker.screens.register.RegisterActivity
import com.noemi.tracker.screens.resetpassword.ResetPasswordActivity
import com.noemi.tracker.utils.components.EmailOutlineTextField
import com.noemi.tracker.utils.components.HeaderText
import com.noemi.tracker.utils.components.LargeActionButton
import com.noemi.tracker.utils.components.PasswordOutlineTextField
import com.noemi.tracker.utils.components.SmallCircularButton
import com.noemi.tracker.utils.components.ProgressIndicator
import com.noemi.tracker.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<AuthViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val keyBoardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val intentLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        scope.launch {
            googleSignIn(
                coroutineScope = scope,
                context = context,
                onEvent = viewModel::onEvent,
                onHandleFailure = viewModel::handleFailure,
                onAddGoogleAccountIntent = viewModel::addGoogleAccountIntent,
                intentLauncher = null
            )
        }
    }

    if (uiState.authState == AuthState.Authenticated || uiState.authState == AuthState.Anonymous) TrackerActivity.newInstance(context).also {
        (context as LoginActivity).finish()
    }

    if (!uiState.errorMessage.isNullOrEmpty()) Toast.makeText(context, "${uiState.errorMessage}", Toast.LENGTH_LONG).show()

    var passwordVisualTransformation by remember {
        mutableStateOf<VisualTransformation>(
            PasswordVisualTransformation()
        )
    }
    val passwordInteractionState = remember { MutableInteractionSource() }
    val emailInteractionState = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // Login
            item {
                HeaderText(text = stringResource(id = R.string.label_login))
            }

            // Email
            item {
                EmailOutlineTextField(
                    value = viewModel.emailAddress,
                    onValueChanged = { viewModel.updateEmailAddress(it) },
                    hasError = viewModel.emailHasError,
                    interactionSource = emailInteractionState,
                    imeAction = ImeAction.Next,
                    keyBoardController = null
                )
            }

            // Password
            item {
                PasswordOutlineTextField(
                    value = viewModel.password,
                    onValueChanged = { viewModel.updatePassword(it) },
                    hasError = viewModel.passwordHasError,
                    trailingIcon = {
                        Icon(
                            painter = painterResource(
                                id = when (passwordVisualTransformation != VisualTransformation.None) {
                                    true -> R.drawable.ic_eye_off
                                    else -> R.drawable.ic_eye_on
                                }
                            ),
                            contentDescription = stringResource(id = R.string.label_placeholder_password),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                passwordVisualTransformation = when (passwordVisualTransformation != VisualTransformation.None) {
                                    true -> VisualTransformation.None
                                    else -> PasswordVisualTransformation()
                                }
                            }
                        )
                    },
                    imeAction = ImeAction.Done,
                    passwordVisualTransformation = passwordVisualTransformation,
                    interactionSource = passwordInteractionState,
                    keyBoardController = keyBoardController
                )
            }

            // Spacer
            item {
                Spacer(modifier = modifier.padding(20.dp))
            }

            // LogIn button
            item {
                Box(
                    modifier = modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SmallCircularButton(
                        isEnabled = viewModel.loginEnabled,
                        buttonText = stringResource(id = R.string.label_submit_login),
                        onClick = {
                            viewModel.onEvent(UIAuthEvent.SignIn(email = viewModel.emailAddress, password = viewModel.password))
                        },
                        keyboardController = keyBoardController
                    )

                    if (uiState.isLoading) ProgressIndicator(size = 46, strokeWidth = 3)
                }
            }

            // Or use
            item {

                Box(
                    modifier = modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        HorizontalDivider(
                            modifier = modifier.width(36.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            thickness = 2.dp
                        )

                        HorizontalDivider(
                            modifier = modifier.width(36.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            thickness = 2.dp
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.label_or),
                        modifier = modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Spacer
            item {
                Spacer(modifier = modifier.padding(8.dp))
            }

            // GoogleSignIn
            item {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onTertiaryContainer)
                        .clickable {
                            viewModel.onGoogleSingInClicked()

                            googleSignIn(
                                coroutineScope = scope,
                                context = context,
                                onEvent = viewModel::onEvent,
                                onHandleFailure = viewModel::handleFailure,
                                onAddGoogleAccountIntent = viewModel::addGoogleAccountIntent,
                                intentLauncher = intentLauncher
                            )
                        },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = stringResource(id = R.string.label_google_button),
                        contentScale = ContentScale.Crop,
                        modifier = modifier
                            .padding(start = 24.dp)
                            .size(40.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.label_google_button),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = modifier.padding(12.dp)
                    )
                }
            }

            // Spacer
            item { Spacer(modifier = modifier.padding(12.dp)) }

            // Register
            item {
                LargeActionButton(
                    buttonText = stringResource(id = R.string.label_register),
                    onClick = {
                        RegisterActivity.newInstance(context)
                    })
            }

            // Spacer
            item { Spacer(modifier = modifier.padding(12.dp)) }

            // Reset button
            item {
                LargeActionButton(
                    buttonText = stringResource(id = R.string.label_reset_password),
                    onClick = {
                        ResetPasswordActivity.newInstance(context)
                    })
            }

            // Spacer
            item {
                Spacer(modifier = modifier.padding(8.dp))
            }
        }
    }
}

private fun googleSignIn(
    coroutineScope: CoroutineScope,
    context: Context,
    onEvent: (UIAuthEvent) -> Unit,
    onHandleFailure: (GetCredentialException) -> Unit,
    onAddGoogleAccountIntent: () -> Intent,
    intentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>?
) {
    val credentialManager = CredentialManager.create(context)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.CLIENT_ID)
        .setAutoSelectEnabled(true)
        .setNonce(NonceProvider.getNonce())
        .build()

    val googleSignRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(
                request = googleSignRequest,
                context = context
            )
            onEvent(UIAuthEvent.GoogleSignIn(result))
        } catch (e: NoCredentialException) {
            intentLauncher?.launch(onAddGoogleAccountIntent.invoke())
        } catch (e: GetCredentialException) {
            onHandleFailure.invoke(e)
        }
    }
}
