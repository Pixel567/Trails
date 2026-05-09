package com.poznantrails.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary          = HikingGreen,
    onPrimary        = Color.White,
    background       = Background,
    surface          = Surface,
    onBackground     = OnSurface,
    onSurface        = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    outline          = Outline,
    error            = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary          = HikingGreenDark,
    onPrimary        = Color(0xFF0B1A0B),
    background       = BackgroundDark,
    surface          = SurfaceDark,
    onBackground     = OnSurfaceDark,
    onSurface        = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline          = OutlineDark,
    error            = ErrorRedDark
)

data class AppPalette(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val hikingPrimary: Color,
    val hikingSoft: Color,
    val cyclingPrimary: Color,
    val cyclingSoft: Color,
    val difficultyEasy: Color,
    val difficultyMedium: Color,
    val difficultyHard: Color,
    val error: Color
)

val LocalAppPalette = staticCompositionLocalOf {
    AppPalette(
        isDark = false,
        background = Background,
        surface = Surface,
        onSurface = OnSurface,
        onSurfaceVariant = OnSurfaceVariant,
        outline = Outline,
        hikingPrimary = HikingGreen,
        hikingSoft = HikingGreenLight,
        cyclingPrimary = CyclingBlue,
        cyclingSoft = CyclingBlueLight,
        difficultyEasy = DifficultyEasy,
        difficultyMedium = DifficultyMedium,
        difficultyHard = DifficultyHard,
        error = ErrorRed
    )
}

@Composable
fun PoznanTrailsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val palette = if (darkTheme) {
        AppPalette(
            isDark = true,
            background = BackgroundDark,
            surface = SurfaceDark,
            onSurface = OnSurfaceDark,
            onSurfaceVariant = OnSurfaceVariantDark,
            outline = OutlineDark,
            hikingPrimary = HikingGreenDark,
            hikingSoft = HikingGreenDarkSurface,
            cyclingPrimary = CyclingBlueDark,
            cyclingSoft = CyclingBlueDarkSurface,
            difficultyEasy = DifficultyEasyDark,
            difficultyMedium = DifficultyMediumDark,
            difficultyHard = DifficultyHardDark,
            error = ErrorRedDark
        )
    } else {
        AppPalette(
            isDark = false,
            background = Background,
            surface = Surface,
            onSurface = OnSurface,
            onSurfaceVariant = OnSurfaceVariant,
            outline = Outline,
            hikingPrimary = HikingGreen,
            hikingSoft = HikingGreenLight,
            cyclingPrimary = CyclingBlue,
            cyclingSoft = CyclingBlueLight,
            difficultyEasy = DifficultyEasy,
            difficultyMedium = DifficultyMedium,
            difficultyHard = DifficultyHard,
            error = ErrorRed
        )
    }

    CompositionLocalProvider(LocalAppPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            content     = content
        )
    }
}
