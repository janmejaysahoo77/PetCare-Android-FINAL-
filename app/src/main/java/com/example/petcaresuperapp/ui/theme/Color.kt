package com.example.petcaresuperapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Modern Professional Palette - Primary
val PrimaryLight = Color(0xFF4CAF50)
val PrimaryMain = Color(0xFF2E7D32)
val PrimaryDark = Color(0xFF1B5E20)
val PrimaryContainer = Color(0xFFC8E6C9)

// Secondary / Accent
val SecondaryLight = Color(0xFF64B5F6)
val SecondaryMain = Color(0xFF2196F3)
val SecondaryDark = Color(0xFF1565C0)

val AccentLight = Color(0xFFFF8A65)
val AccentMain = Color(0xFFFF5722)
val AccentDark = Color(0xFFBF360C)

// Neutral Colors - Light Mode
val LightBackground = Color(0xFFF8F9FA)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF1A1C1E)
val LightTextPrimary = Color(0xFF1A1C1E)
val LightTextSecondary = Color(0xFF42474E)
val LightTextTertiary = Color(0xFF72777F)

// Neutral Colors - Dark Mode
val DarkBackground = Color(0xFF0E1410)
val DarkSurface = Color(0xFF1A1C1E)
val DarkOnSurface = Color(0xFFE2E2E6)
val DarkTextPrimary = Color(0xFFE2E2E6)
val DarkTextSecondary = Color(0xFFC2C7CF)
val DarkTextTertiary = Color(0xFF8E9199)

// Gradient Definitions
val SecondaryGradStart = Color(0xFF64B5F6)
val SecondaryGradEnd = Color(0xFF2196F3)
val AccentGradStart = Color(0xFFFF8A65)
val AccentGradEnd = Color(0xFFFF5722)

val PrimaryGradientColors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
val SecondaryGradientColors = listOf(SecondaryGradStart, SecondaryGradEnd)
val AccentGradientColors = listOf(AccentGradStart, AccentGradEnd)
val SurfaceGradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFF1F3F4))

// Legacy & Compatibility (Keep for now to avoid breaking other screens)
val PrimaryColor = PrimaryMain
val SecondaryColor = SecondaryMain
val AccentColor = AccentMain
val PrimaryGreen = PrimaryLight
val BackgroundColor = Color(0xFFF8F9FA)
val SurfaceColor = Color(0xFFFFFFFF)
val DividerColor = Color(0xFFE0E0E0)
val TextPrimary = Color(0xFF1A1A1A)
val TextDark = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF6E6E6E)
val TextGrey = Color(0xFF9E9E9E)
val SuccessGradStart = Color(0xFF11998E)
val SuccessGradEnd = Color(0xFF38EF7D)
val PrimaryGradient = Brush.linearGradient(PrimaryGradientColors)
val SecondaryGradient = Brush.linearGradient(SecondaryGradientColors)
val AccentGradient = Brush.linearGradient(AccentGradientColors)
val SuccessGradient = Brush.linearGradient(listOf(SuccessGradStart, SuccessGradEnd))
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
