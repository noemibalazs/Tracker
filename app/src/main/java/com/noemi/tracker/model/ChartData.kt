package com.noemi.tracker.model

import androidx.compose.ui.graphics.Color

data class ChartData(
    val type: String,
    val value: Float,
    val color: Color,
    val range: ClosedFloatingPointRange<Float>
)