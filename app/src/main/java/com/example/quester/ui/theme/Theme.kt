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
    // Primari
    primary = FantasyPurple,
    onPrimary = FantasyText,
    primaryContainer = FantasyPurpleDark,
    onPrimaryContainer = FantasyGoldLight,

    // Secondari
    secondary = FantasyGold,
    onSecondary = Color(0xFF0D0B14),
    secondaryContainer = FantasyGoldDark,
    onSecondaryContainer = FantasyText,

    // Terziari
    tertiary = FantasyPurpleLight,
    onTertiary = FantasyText,
    tertiaryContainer = FantasyPurpleDark,
    onTertiaryContainer = FantasyGoldLight,

    // Background e Surface
    background = FantasyBackground,
    onBackground = FantasyText,

    surface = FantasySurface,
    onSurface = FantasyText,
    surfaceVariant = FantasySurfaceLight,
    onSurfaceVariant = FantasyTextSecondary,

    surfaceTint = FantasyPurple,

    // Errori
    error = FantasyError,
    onError = FantasyText,
    errorContainer = FantasyError.copy(alpha = 0.15f),
    onErrorContainer = FantasyError,

    // Outline
    outline = FantasyGold.copy(alpha = 0.35f),
    outlineVariant = FantasyGold.copy(alpha = 0.15f),

    // Altri
    scrim = Color.Black.copy(alpha = 0.7f),
    inverseSurface = FantasySurfaceLight,
    inverseOnSurface = FantasyText,
    inversePrimary = FantasyGoldLight
)

// ============================================================
//  COLOR SCHEME - TEMA CHIARO (FANTASY LIGHT)
// ============================================================

private val FantasyLightColorScheme = lightColorScheme(
    // Primari
    primary = FantasyLightPurple,
    onPrimary = FantasyLightText,
    primaryContainer = FantasyLightPurpleLight,
    onPrimaryContainer = FantasyLightText,

    // Secondari
    secondary = FantasyLightGold,
    onSecondary = FantasyLightText,
    secondaryContainer = FantasyLightGoldLight,
    onSecondaryContainer = FantasyLightText,

    // Terziari
    tertiary = FantasyLightPurple,
    onTertiary = FantasyLightText,
    tertiaryContainer = FantasyLightPurpleLight,
    onTertiaryContainer = FantasyLightText,

    // Background e Surface
    background = FantasyLightBackground,
    onBackground = FantasyLightText,

    surface = FantasyLightSurface,
    onSurface = FantasyLightText,
    surfaceVariant = FantasyLightSurfaceVariant,
    onSurfaceVariant = FantasyLightTextSecondary,

    surfaceTint = FantasyLightPurple,

    // Errori
    error = FantasyLightError,
    onError = FantasyLightSurface,
    errorContainer = FantasyLightError.copy(alpha = 0.12f),
    onErrorContainer = FantasyLightError,

    // Outline
    outline = FantasyLightPurple.copy(alpha = 0.25f),
    outlineVariant = FantasyLightPurple.copy(alpha = 0.12f),

    // Altri
    scrim = Color.Black.copy(alpha = 0.5f),
    inverseSurface = FantasyLightSurfaceVariant,
    inverseOnSurface = FantasyLightText,
    inversePrimary = FantasyLightPurple
)

// ============================================================
//  FUNZIONE TEMA
// ============================================================

@Composable
fun QuesterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        FantasyDarkColorScheme
    } else {
        FantasyLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}