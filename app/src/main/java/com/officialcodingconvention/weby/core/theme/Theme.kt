package com.officialcodingconvention.weby.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = WebyPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = WebyPrimary.copy(alpha = 0.12f),
    onPrimaryContainer = WebyPrimaryVariant,
    secondary = WebySecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = WebySecondary.copy(alpha = 0.12f),
    onSecondaryContainer = WebySecondaryVariant,
    tertiary = WebyTertiary,
    onTertiary = LightOnPrimary,
    tertiaryContainer = WebyTertiary.copy(alpha = 0.12f),
    onTertiaryContainer = WebyTertiary,
    error = Error,
    onError = Color.White,
    errorContainer = Error.copy(alpha = 0.12f),
    onErrorContainer = Error,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    scrim = Color.Black.copy(alpha = 0.32f),
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkOnSurface,
    inversePrimary = WebyPrimary,
    surfaceTint = WebyPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = WebyPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = WebyPrimary.copy(alpha = 0.24f),
    onPrimaryContainer = WebyPrimary,
    secondary = WebySecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = WebySecondary.copy(alpha = 0.24f),
    onSecondaryContainer = WebySecondary,
    tertiary = WebyTertiary,
    onTertiary = DarkOnPrimary,
    tertiaryContainer = WebyTertiary.copy(alpha = 0.24f),
    onTertiaryContainer = WebyTertiary,
    error = Error,
    onError = Color.White,
    errorContainer = Error.copy(alpha = 0.24f),
    onErrorContainer = Error,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    scrim = Color.Black.copy(alpha = 0.6f),
    inverseSurface = LightSurface,
    inverseOnSurface = LightOnSurface,
    inversePrimary = WebyPrimaryVariant,
    surfaceTint = WebyPrimary
)

@Composable
fun WebyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WebyTypography,
        shapes = WebyShapes,
        content = content
    )
}
