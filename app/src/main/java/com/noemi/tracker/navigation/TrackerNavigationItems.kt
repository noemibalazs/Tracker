package com.noemi.tracker.navigation

import android.content.Context
import com.noemi.tracker.R

object TrackerNavigationItems {

    fun getNavigationItems(context: Context): List<TrackerBarItem> =
        listOf(
            TrackerBarItem(
                title = context.getString(R.string.label_home),
                icon = R.drawable.ic_home,
                navRoute = NavRoute.Home
            ),

            TrackerBarItem(
                title = context.getString(R.string.label_charts),
                icon = R.drawable.ic_chart,
                navRoute = NavRoute.Charts
            )
        )
}