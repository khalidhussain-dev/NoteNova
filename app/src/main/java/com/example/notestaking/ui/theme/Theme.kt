package com.example.notestaking.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = NovaIndigo,
    onPrimary = NovaCardLight,
    primaryContainer = NoteColorDefault,
    onPrimaryContainer = NovaIndigoDark,
    secondary = NovaTeal,
    onSecondary = NovaCardLight,
    tertiary = NovaTealDark,
    background = NovaSurfaceLight,
    onBackground = NovaIndigoDark,
    surface = NovaCardLight,
    onSurface = NovaIndigoDark,
    surfaceVariant = NoteColorSky,
    onSurfaceVariant = NovaIndigo
)

private val DarkColorScheme = darkColorScheme(
    primary = NovaIndigo,
    onPrimary = NovaCardDark,
    primaryContainer = NoteColorDefaultDark,
    onPrimaryContainer = NovaIndigo,
    secondary = NovaTeal,
    onSecondary = NovaCardDark,
    tertiary = NovaTealDark,
    background = NovaSurfaceDark,
    onBackground = NovaCardLight,
    surface = NovaCardDark,
    onSurface = NovaCardLight,
    surfaceVariant = NoteColorSkyDark,
    onSurfaceVariant = NovaIndigo
)

@Composable
fun NoteNovaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
