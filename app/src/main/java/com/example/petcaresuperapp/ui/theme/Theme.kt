package com.example.petcaresuperapp.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.Black,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,
    secondary = SecondaryLight,
    onSecondary = Color.Black,
    tertiary = AccentLight,
    onTertiary = Color.Black,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    error = Color(0xFFCF6679)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryMain,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryDark,
    secondary = SecondaryMain,
    onSecondary = Color.White,
    tertiary = AccentMain,
    onTertiary = Color.White,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightOnSurface,
    onSurface = LightOnSurface,
    error = Color(0xFFB00020)
)

@Composable
fun PetCareSuperAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
