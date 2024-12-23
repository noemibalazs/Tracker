package com.noemi.tracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.noemi.tracker.R

val baseline = Typography()

val philosopherFamily = FontFamily(
    Font(R.font.philosopher_regular, FontWeight.Normal),
    Font(R.font.philosopher_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.philosopher_bold, FontWeight.Bold)
)

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = philosopherFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = philosopherFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = philosopherFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = philosopherFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = philosopherFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = philosopherFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = philosopherFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = philosopherFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = philosopherFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = philosopherFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = philosopherFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = philosopherFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = philosopherFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = philosopherFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = philosopherFamily)
)