package com.noemi.tracker.screens.resetpassword

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi.tracker.R
import com.noemi.tracker.model.UIAuthEvent
import com.noemi.tracker.screens.login.LoginActivity
import com.noemi.tracker.utils.components.EmailOutlineTextField
import com.noemi.tracker.utils.components.ProgressIndicator
import com.noemi.tracker.utils.components.SmallCircularButton
import com.noemi.tracker.viewmodel.AuthViewModel

@Composable
fun ResetPasswordScreen(modifier: Modifier = Modifier) {

    val viewModel = hiltViewModel<AuthViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    if (!uiState.errorMessage.isNullOrEmpty()) Toast.makeText(context, "${uiState.errorMessage}", Toast.LENGTH_LONG).show()

    val emailInteractionState = remember { MutableInteractionSource() }
    var onContinueClicked by remember { mutableStateOf(false) }

    if (!uiState.isLoading && onContinueClicked) LoginActivity.newInstance(context)

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

            // Reset password
            item {
                Text(
                    text = stringResource(
                        id = R.string.label_reset_your_password
                    ), modifier = modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 32.dp),

                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Email
            item {
                EmailOutlineTextField(
                    value = viewModel.emailAddress,
                    onValueChanged = { viewModel.updateEmailAddress(it) },
                    hasError = viewModel.emailHasError,
                    interactionSource = emailInteractionState,
                    imeAction = ImeAction.Done,
                    keyBoardController = keyboardController
                )
            }

            // Spacer
            item {
                Spacer(modifier = modifier.padding(20.dp))
            }

            // Continue button
            item {
                Box(
                    modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    SmallCircularButton(
                        isEnabled = !viewModel.emailHasError && viewModel.emailAddress.isNotEmpty(), buttonText = stringResource(id = R.string.label_continue), onClick = {
                            viewModel.onEvent(UIAuthEvent.ResetPassword(email = viewModel.emailAddress))
                            onContinueClicked = true
                        }, keyboardController = keyboardController
                    )

                    if (uiState.isLoading) ProgressIndicator(size = 46, strokeWidth = 3)
                }
            }

        }
    }
}