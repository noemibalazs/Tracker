package com.noemi.tracker.navigation

import com.noemi.tracker.utils.ROUTE_CHARTS
import com.noemi.tracker.utils.ROUTE_HOME

data class TrackerBarItem(
    val title: String,
    val icon: Int,
    val navRoute: NavRoute
)

sealed class NavRoute(val route: String) {
    data object Home : NavRoute(ROUTE_HOME)
    data object Charts : NavRoute(ROUTE_CHARTS)
}