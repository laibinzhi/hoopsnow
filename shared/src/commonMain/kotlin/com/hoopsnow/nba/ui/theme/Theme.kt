package com.hoopsnow.nba.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val HoopsNowColorScheme = darkColorScheme(
    primary = Blue600,
    onPrimary = White,
    primaryContainer = Blue600,
    onPrimaryContainer = White,
    secondary = Blue400,
    onSecondary = Slate950,
    background = Slate950,
    onBackground = White,
    surface = Slate900,
    onSurface = White,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate400,
    outline = Slate800,
    outlineVariant = Slate700,
    error = Red500,
    onError = White,
)

@Composable
fun HoopsNowTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = HoopsNowColorScheme,
        typography = HoopsNowTypography,
        content = content,
    )
}
