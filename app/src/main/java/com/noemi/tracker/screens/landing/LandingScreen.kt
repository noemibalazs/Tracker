package com.noemi.tracker.screens.landing

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi.tracker.model.AuthState
import com.noemi.tracker.model.UIAuthEvent
import com.noemi.tracker.screens.login.LoginActivity
import com.noemi.tracker.screens.tracker.TrackerActivity
import com.noemi.tracker.utils.components.ProgressIndicator
import com.noemi.tracker.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(modifier: Modifier = Modifier) {

    val viewModel = hiltViewModel<AuthViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(UIAuthEvent.AuthState)
        delay(2100)

        when (uiState.authState == AuthState.Authenticated || uiState.authState == AuthState.Anonymous) {
            true -> TrackerActivity.newInstance(context).also { (context as LandingActivity).finish() }
            else -> LoginActivity.newInstance(context).also { (context as LandingActivity).finish() }
        }
    }

    if (!uiState.errorMessage.isNullOrEmpty()) Toast.makeText(context, "${uiState.errorMessage}", Toast.LENGTH_LONG).show()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (uiState.isLoading) ProgressIndicator(size = 90, strokeWidth = 9)
    }
}