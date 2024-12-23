package com.noemi.tracker.screens.tracker

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.noemi.tracker.R
import com.noemi.tracker.model.AuthState
import com.noemi.tracker.navigation.NavRoute
import com.noemi.tracker.model.UIAuthEvent
import com.noemi.tracker.model.UserDetails
import com.noemi.tracker.navigation.TrackerNavigationItems
import com.noemi.tracker.screens.charts.ChartsScreen
import com.noemi.tracker.screens.expenses.ExpensesScreen
import com.noemi.tracker.screens.landing.LandingActivity
import com.noemi.tracker.utils.components.ProgressIndicator
import com.noemi.tracker.utils.components.SmallCircularButton
import com.noemi.tracker.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TrackerScreen(navController: NavHostController = rememberNavController()) {

    val viewModel = viewModel<AuthViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var userDetails by remember { mutableStateOf(UserDetails()) }
    var userDetailsLoading by remember { mutableStateOf(false) }

    if (uiState.authState == AuthState.SignedOut) LandingActivity.newInstance(context).also {
        (context as TrackerActivity).finish()
    }

    LaunchedEffect(key1 = true) {
        userDetailsLoading = true
        delay(300)

        userDetails = viewModel.getUserDetails()
        userDetailsLoading = false
    }

    Scaffold(
        content = {
            TrackerContent(
                isLoading = uiState.isLoading,
                userDetails = userDetails,
                userDetailsLoading = userDetailsLoading,
                onEvent = viewModel::onEvent,
                navController = navController
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    )
}

@Composable
fun TrackerContent(
    userDetailsLoading: Boolean,
    userDetails: UserDetails,
    isLoading: Boolean,
    onEvent: (UIAuthEvent) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        when (userDetailsLoading) {
            true -> Column(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProgressIndicator(size = 90, strokeWidth = 9)
            }

            else -> {
                Spacer(modifier = modifier.padding(36.dp))

                UserDetailsHeader(user = userDetails, isLoading = isLoading, onEvent = onEvent)

                Spacer(modifier = modifier.padding(12.dp))

                NavigationHost(navController = navController)
            }
        }
    }
}

@Composable
fun UserDetailsHeader(
    user: UserDetails,
    isLoading: Boolean,
    onEvent: (UIAuthEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            AsyncImage(
                //model = if (user.avatar.isNullOrEmpty()) R.drawable.avatar else user.avatar,
                model = R.drawable.avatar,
                contentDescription = stringResource(id = R.string.label_avatar),
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Text(
                text = if (user.name.isNullOrEmpty()) stringResource(id = R.string.label_avatar) else user.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = modifier.wrapContentWidth(),
                contentAlignment = Alignment.Center
            ) {
                SmallCircularButton(
                    isEnabled = true,
                    buttonText = stringResource(id = R.string.label_submit_sign_out),
                    onClick = { onEvent(UIAuthEvent.SignOut) }
                )

                if (isLoading) ProgressIndicator(size = 46, strokeWidth = 3)
            }
        }
    }
}


@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.Home.route
    ) {
        composable(route = NavRoute.Home.route) {
            ExpensesScreen()
        }

        composable(route = NavRoute.Charts.route) {
            ChartsScreen()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val context = LocalContext.current
    val navigationItems = TrackerNavigationItems.getNavigationItems(context)

    NavigationBar {

        val currentEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentEntry?.destination?.route

        navigationItems.forEach { item ->

            NavigationBarItem(
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                selected = currentRoute == item.navRoute.route,
                onClick = {
                    navController.navigate(item.navRoute.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }

                },
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = null) })
        }
    }
}

