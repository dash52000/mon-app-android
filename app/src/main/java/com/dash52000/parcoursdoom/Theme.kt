package com.dash52000.parcoursdoom

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Palette : encre, os, braise et or terni.
private val Ink = Color(0xFF0E0D0C)
private val Coal = Color(0xFF181614)
private val Ash = Color(0xFF24211E)
private val AshHigh = Color(0xFF2B2824)
private val Bone = Color(0xFFE9E0D0)
private val Dust = Color(0xFFA0968A)
private val Ember = Color(0xFFD0553F)
private val Gold = Color(0xFFCFA64E)

private val DoomColors = darkColorScheme(
    primary = Ember,
    onPrimary = Ink,
    secondary = Gold,
    onSecondary = Ink,
    secondaryContainer = Color(0xFF3A2A22),
    onSecondaryContainer = Bone,
    background = Ink,
    onBackground = Bone,
    surface = Coal,
    onSurface = Bone,
    surfaceVariant = Ash,
    onSurfaceVariant = Dust,
    surfaceContainer = Coal,
    surfaceContainerHigh = AshHigh,
    surfaceContainerHighest = AshHigh,
    outline = Color(0xFF4A443D),
)

private val Base = Typography()

private val DoomType = Base.copy(
    displaySmall = Base.displaySmall.copy(
        fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 34.sp, lineHeight = 40.sp,
    ),
    headlineMedium = Base.headlineMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold),
    headlineSmall = Base.headlineSmall.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold),
    titleMedium = Base.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    bodyLarge = Base.bodyLarge.copy(lineHeight = 26.sp),
)

@Composable
fun DoomTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DoomColors, typography = DoomType, content = content)
}
