package com.example.quester.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ============================================================
//  COLOR SCHEME - TEMA SCURO (FANTASY DARK)
// ============================================================

private val FantasyDarkColorScheme = darkColorScheme(
    primary = FantasyPurple,
    onPrimary = FantasyText,
    primaryContainer = FantasyPurpleDark,
    onPrimaryContainer = FantasyGoldLight,
    secondary = FantasyGold,
    onSecondary = Color(0xFF0D0B14),
    secondaryContainer = FantasyGoldDark,
    onSecondaryContainer = FantasyText,
    tertiary = FantasyPurpleLight,
    onTertiary = FantasyText,
    tertiaryContainer = FantasyPurpleDark,
    onTertiaryContainer = FantasyGoldLight,
    background = FantasyBackground,
    onBackground = FantasyText,
    surface = FantasySurface,
    onSurface = FantasyText,
    surfaceVariant = FantasySurfaceLight,
    onSurfaceVariant = FantasyTextSecondary,
    surfaceTint = FantasyPurple,
    error = FantasyError,
    onError = FantasyText,
    errorContainer = FantasyError.copy(alpha = 0.15f),
    onErrorContainer = FantasyError,
    outline = FantasyGold.copy(alpha = 0.35f),
    outlineVariant = FantasyGold.copy(alpha = 0.15f),
    scrim = Color.Black.copy(alpha = 0.7f),
    inverseSurface = FantasySurfaceLight,
    inverseOnSurface = FantasyText,
    inversePrimary = FantasyGoldLight
)

// ============================================================
//  COLOR SCHEME - TEMA CHIARO (FANTASY LIGHT)
// ============================================================

private val FantasyLightColorScheme = lightColorScheme(
    primary = FantasyLightPurple,
    onPrimary = FantasyLightText,
    primaryContainer = FantasyLightPurpleLight,
    onPrimaryContainer = FantasyLightText,
    secondary = FantasyLightGold,
    onSecondary = FantasyLightText,
    secondaryContainer = FantasyLightGoldLight,
    onSecondaryContainer = FantasyLightText,
    tertiary = FantasyLightPurple,
    onTertiary = FantasyLightText,
    tertiaryContainer = FantasyLightPurpleLight,
    onTertiaryContainer = FantasyLightText,
    background = FantasyLightBackground,
    onBackground = FantasyLightText,
    surface = FantasyLightSurface,
    onSurface = FantasyLightText,
    surfaceVariant = FantasyLightSurfaceVariant,
    onSurfaceVariant = FantasyLightTextSecondary,
    surfaceTint = FantasyLightPurple,
    error = FantasyLightError,
    onError = FantasyLightSurface,
    errorContainer = FantasyLightError.copy(alpha = 0.12f),
    onErrorContainer = FantasyLightError,
    outline = FantasyLightPurple.copy(alpha = 0.25f),
    outlineVariant = FantasyLightPurple.copy(alpha = 0.12f),
    scrim = Color.Black.copy(alpha = 0.5f),
    inverseSurface = FantasyLightSurfaceVariant,
    inverseOnSurface = FantasyLightText,
    inversePrimary = FantasyLightPurple
)

// ============================================================
//  COLOR SCHEME - TEMA ARCADE (PIXEL/RETRÒ)
// ============================================================

private val ArcadeColorScheme = darkColorScheme(
    primary = ArcadeGreen,
    onPrimary = Color(0xFF0A0A0F),
    primaryContainer = ArcadeGreen.copy(alpha = 0.2f),
    onPrimaryContainer = ArcadeGreen,
    secondary = ArcadePink,
    onSecondary = Color(0xFF0A0A0F),
    secondaryContainer = ArcadePink.copy(alpha = 0.2f),
    onSecondaryContainer = ArcadePink,
    tertiary = ArcadeCyan,
    onTertiary = Color(0xFF0A0A0F),
    tertiaryContainer = ArcadeCyan.copy(alpha = 0.2f),
    onTertiaryContainer = ArcadeCyan,
    background = ArcadeBackground,
    onBackground = ArcadeText,
    surface = ArcadeSurface,
    onSurface = ArcadeText,
    surfaceVariant = ArcadeSurfaceLight,
    onSurfaceVariant = ArcadeTextSecondary,
    surfaceTint = ArcadeGreen,
    error = ArcadeError,
    onError = Color(0xFF0A0A0F),
    errorContainer = ArcadeError.copy(alpha = 0.15f),
    onErrorContainer = ArcadeError,
    outline = ArcadeGreen.copy(alpha = 0.35f),
    outlineVariant = ArcadeGreen.copy(alpha = 0.15f),
    scrim = Color.Black.copy(alpha = 0.7f),
    inverseSurface = ArcadeSurfaceLight,
    inverseOnSurface = ArcadeText,
    inversePrimary = ArcadePink
)

// ============================================================
//  FUNZIONE TEMA
// ============================================================

@Composable
fun QuesterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeType: AppTheme = AppTheme.FANTASY,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        AppTheme.FANTASY -> if (darkTheme) FantasyDarkColorScheme else FantasyLightColorScheme
        AppTheme.ARCADE -> ArcadeColorScheme
    }

    // ✅ Usa typography dinamica in base al tema
    val typography = when (themeType) {
        AppTheme.FANTASY -> FantasyTypography
        AppTheme.ARCADE -> ArcadeTypography
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}