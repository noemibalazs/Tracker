package com.noemi.tracker.screens.verifyemail

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi.tracker.R
import com.noemi.tracker.model.AuthState
import com.noemi.tracker.model.UIAuthEvent
import com.noemi.tracker.screens.tracker.TrackerActivity
import com.noemi.tracker.utils.components.ProgressIndicator
import com.noemi.tracker.viewmodel.AuthViewModel

@Composable
fun EmailVerificationScreen(
    email: String,
    modifier: Modifier = Modifier
) {

    val viewModel = hiltViewModel<AuthViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiState.authState == AuthState.Authenticated || uiState.authState == AuthState.Anonymous) TrackerActivity.newInstance(context).also {
        (context as EmailVerificationActivity).finish()
    }

    if (!uiState.errorMessage.isNullOrEmpty()) Toast.makeText(context, "${uiState.errorMessage}", Toast.LENGTH_LONG).show()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
        ) {
            // Verification
            item {
                Text(
                    text = stringResource(id = R.string.label_verification),
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Image with lock
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Box(
                        modifier = modifier
                            .size(size = 120.dp)
                            .clip(shape = CircleShape)
                            .background(color = MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.ic_eye_off),
                            contentDescription = stringResource(id = R.string.label_email_verification),
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = modifier
                                .align(alignment = Alignment.TopStart)
                                .offset(
                                    x = 43.dp,
                                    y = 43.dp
                                )
                                .size(size = 36.dp)
                        )
                    }

                    Text(
                        modifier = modifier.padding(top = 20.dp),
                        text = stringResource(id = R.string.label_verification_link),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Text(
                        text = stringResource(id = R.string.label_email_verification_sent),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Text(
                        text = email,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            // Resend verification email
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.label_didnt_receive_the_link),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        modifier = modifier.padding(end = 8.dp)
                    )

                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.label_resend),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.clickable {
                                viewModel.onEvent(UIAuthEvent.ResendVerificationEmail)
                            }
                        )

                        if (uiState.isLoading) ProgressIndicator(size = 46, strokeWidth = 3)
                    }
                }
            }
        }
    }
}