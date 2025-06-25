package com.example.icbmpfinalboss.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Define the light color scheme using the colors from Color.kt
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryBlue,
    primaryContainer = PrimaryContainerBlue,
    onPrimaryContainer = OnPrimaryContainerBlue,
    secondary = SecondaryOrange,
    onSecondary = OnSecondaryOrange,
    secondaryContainer = SecondaryContainerOrange,
    onSecondaryContainer = OnSecondaryContainerOrange,
    tertiary = TertiaryGreen,
    onTertiary = OnTertiaryGreen,
    tertiaryContainer = TertiaryContainerGreen,
    onTertiaryContainer = OnTertiaryContainerGreen,
    error = ErrorRed,
    background = BackgroundLight,
    surface = SurfaceLight
)

// Define the dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDark,
    onPrimary = OnPrimaryBlueDark,
    primaryContainer = PrimaryContainerBlueDark,
    onPrimaryContainer = OnPrimaryContainerBlueDark,
    secondary = SecondaryOrangeDark,
    onSecondary = OnSecondaryOrangeDark,
    secondaryContainer = SecondaryContainerOrangeDark,
    onSecondaryContainer = OnSecondaryContainerOrangeDark,
    background = BackgroundDark,
    surface = SurfaceDark
)

@Composable
fun CloudBMSDashboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
