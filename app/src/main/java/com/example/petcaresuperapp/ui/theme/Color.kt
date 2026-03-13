package com.example.petcaresuperapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Modern 2026 Design System - Primary & Secondary
val Primary2026 = Color(0xFF22C55E) // Modern Green
val Secondary2026 = Color(0xFF4ADE80)
val Accent2026 = Color(0xFF10B981)

// Elegant Dark Palette
val BackgroundDark = Color(0xFF0F172A)
val SurfaceDark = Color(0xFF1E293B)
val SurfaceVariantDark = Color(0xFF334155)

// Text Colors
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFF94A3B8)
val TextSlate = Color(0xFF64748B)

// Functional Colors
val Error2026 = Color(0xFFEF4444)
val Warning2026 = Color(0xFFF59E0B)
val Info2026 = Color(0xFF3B82F6)

// Gradients
val PremiumGradient = Brush.verticalGradient(
    colors = listOf(Primary2026, Color(0xFF16A34A))
)

val GlassGradient = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.15f),
        Color.White.copy(alpha = 0.05f)
    )
)

// Legacy compatibility (mapping to new system)
val PrimaryColor = Primary2026
val SecondaryColor = Secondary2026
val BackgroundColor = BackgroundDark
val SurfaceColor = SurfaceDark
val TextPrimary = TextWhite
val TextSecondary = TextGray
val PrimaryGradient = PremiumGradient

// Keep some old names if absolutely necessary for build, but transition to new ones
val PrimaryLight = Primary2026
val PrimaryMain = Primary2026
val PrimaryDarkColor = Color(0xFF16A34A)
val DarkBackground = BackgroundDark
val DarkSurface = SurfaceDark
val DarkTextPrimary = TextWhite
val DarkTextSecondary = TextGray
