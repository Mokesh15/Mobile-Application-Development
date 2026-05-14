package com.tripmate.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Orange500,
    secondary = TextGrey,
    background = Black,
    surface = DarkGrey,
    onPrimary = Black,
    onBackground = TextWhite,
    onSurface = TextWhite,
    primaryContainer = Orange500.copy(alpha = 0.2f),
    onPrimaryContainer = Orange500,
    surfaceVariant = SurfaceGrey,
    onSurfaceVariant = TextGrey,
    outline = BorderGrey
)

private val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Orange500,
    secondary = TextGrey,
    background = White,
    surface = LightSurface,
    onPrimary = White,
    onBackground = TextBlack,
    onSurface = TextBlack,
    primaryContainer = Orange500.copy(alpha = 0.1f),
    onPrimaryContainer = Orange500,
    surfaceVariant = White,
    onSurfaceVariant = TextGrey,
    outline = LightBorder
)

@Composable
fun TripMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
