package com.noemi.tracker.screens.register

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi.tracker.R
import com.noemi.tracker.model.UIAuthEvent
import com.noemi.tracker.screens.login.LoginActivity
import com.noemi.tracker.screens.verifyemail.EmailVerificationActivity
import com.noemi.tracker.utils.components.EmailOutlineTextField
import com.noemi.tracker.utils.components.HeaderText
import com.noemi.tracker.utils.components.PasswordOutlineTextField
import com.noemi.tracker.utils.components.ProgressIndicator
import com.noemi.tracker.utils.components.SmallCircularButton
import com.noemi.tracker.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<AuthViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val keyBoardController = LocalSoftwareKeyboardController.current

    if (!uiState.errorMessage.isNullOrEmpty()) Toast.makeText(context, "${uiState.errorMessage}", Toast.LENGTH_LONG).show()

    if (uiState.alreadyRegistered) EmailVerificationActivity.newInstance(context, viewModel.emailAddress)

    var passwordVisualTransformation by remember {
        mutableStateOf<VisualTransformation>(
            PasswordVisualTransformation()
        )
    }

    var confirmPasswordVisualTransformation by remember {
        mutableStateOf<VisualTransformation>(
            PasswordVisualTransformation()
        )
    }
    val passwordInteractionState = remember { MutableInteractionSource() }
    val emailInteractionState = remember { MutableInteractionSource() }
    val confirmedPasswordInteraction = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // Sign Up
            item {
                HeaderText(text = stringResource(id = R.string.label_sign_up))
            }

            // Email Address
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
                    imeAction = ImeAction.Next,
                    passwordVisualTransformation = passwordVisualTransformation,
                    interactionSource = passwordInteractionState,
                    keyBoardController = null
                )
            }

            // Confirm Password
            item {
                PasswordOutlineTextField(
                    value = viewModel.confirmPassword,
                    onValueChanged = { viewModel.updateConfirmedPassword(it) },
                    hasError = viewModel.confirmedPasswordHasError,
                    trailingIcon = {
                        Icon(
                            painter = painterResource(
                                id = when (confirmPasswordVisualTransformation != VisualTransformation.None) {
                                    true -> R.drawable.ic_eye_off
                                    else -> R.drawable.ic_eye_on
                                }
                            ),
                            contentDescription = stringResource(id = R.string.label_placeholder_confirm_password),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                confirmPasswordVisualTransformation = when (confirmPasswordVisualTransformation != VisualTransformation.None) {
                                    true -> VisualTransformation.None
                                    else -> PasswordVisualTransformation()
                                }
                            }
                        )
                    },
                    imeAction = ImeAction.Done,
                    passwordVisualTransformation = confirmPasswordVisualTransformation,
                    interactionSource = confirmedPasswordInteraction,
                    keyBoardController = keyBoardController
                )
            }

            // Spacer
            item {
                Spacer(modifier = modifier.padding(20.dp))
            }

            // Sign Up
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SmallCircularButton(
                        isEnabled = viewModel.signUpEnabled,
                        buttonText = stringResource(id = R.string.label_sign_up),
                        onClick = {
                            viewModel.onEvent(UIAuthEvent.Register(email = viewModel.emailAddress, password = viewModel.password))
                        },
                        keyboardController = keyBoardController
                    )

                    if (uiState.isLoading) ProgressIndicator(size = 46, strokeWidth = 3)
                }
            }

            // Spacer
            item {
                Spacer(modifier = modifier.padding(8.dp))
            }

            // Or
            item {

                Box(
                    modifier = modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        HorizontalDivider(
                            modifier = modifier.width(36.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            thickness = 2.dp
                        )

                        Text(
                            text = stringResource(id = R.string.label_already_has_an_account),
                            modifier = modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        HorizontalDivider(
                            modifier = modifier.width(36.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            thickness = 2.dp
                        )
                    }
                }
            }

            // Spacer
            item {
                Spacer(modifier = modifier.padding(8.dp))
            }

            // Log In
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SmallCircularButton(
                        isEnabled = true,
                        buttonText = stringResource(id = R.string.label_submit_login),
                        onClick = {
                            LoginActivity.newInstance(context)
                        })
                }
            }
        }
    }
}