package com.example.quester.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ============================================================
//  COLOR SCHEME - TEMA SCURO (FANTASY DARK - DEFAULT)
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
//  COLOR SCHEME - TEMA FANTASY SPECIALE (PIÙ ORO / AMBRA)
// ============================================================

private val FantasySpecialDarkColorScheme = darkColorScheme(
    primary = FantasyGold,
    onPrimary = Color(0xFF0D0B14),
    primaryContainer = FantasyGoldDark,
    onPrimaryContainer = FantasyText,
    secondary = FantasyPurpleLight,
    onSecondary = FantasyText,
    secondaryContainer = FantasyPurpleDark,
    onSecondaryContainer = FantasyGoldLight,
    tertiary = FantasyGoldLight,
    onTertiary = Color(0xFF0D0B14),
    tertiaryContainer = FantasyGoldDark,
    onTertiaryContainer = FantasyText,
    background = Color(0xFF110D18),
    onBackground = FantasyText,
    surface = Color(0xFF1B1528),
    onSurface = FantasyText,
    surfaceVariant = Color(0xFF261E38),
    onSurfaceVariant = FantasyTextSecondary,
    surfaceTint = FantasyGold,
    error = FantasyError,
    onError = FantasyText,
    errorContainer = FantasyError.copy(alpha = 0.15f),
    onErrorContainer = FantasyError,
    outline = FantasyGold.copy(alpha = 0.6f),
    outlineVariant = FantasyGold.copy(alpha = 0.25f),
    scrim = Color.Black.copy(alpha = 0.7f),
    inverseSurface = FantasySurfaceLight,
    inverseOnSurface = FantasyText,
    inversePrimary = FantasyGold
)

// ============================================================
//  COLOR SCHEME - TEMA REGALE (LUSSO IMPERIALE / ORO E PORPORA)
// ============================================================

private val RegaleDarkColorScheme = darkColorScheme(
    primary = FantasyGoldLight,
    onPrimary = Color(0xFF1A1208),
    primaryContainer = FantasyGold,
    onPrimaryContainer = Color(0xFF1A1208),
    secondary = Color(0xFF9C27B0),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A148C),
    onSecondaryContainer = FantasyGoldLight,
    tertiary = FantasyGold,
    onTertiary = Color(0xFF1A1208),
    tertiaryContainer = FantasyGoldDark,
    onTertiaryContainer = FantasyText,
    background = Color(0xFF0F0B14),
    onBackground = FantasyText,
    surface = Color(0xFF181022),
    onSurface = FantasyText,
    surfaceVariant = Color(0xFF241833),
    onSurfaceVariant = FantasyTextSecondary,
    surfaceTint = FantasyGoldLight,
    error = FantasyError,
    onError = FantasyText,
    errorContainer = FantasyError.copy(alpha = 0.15f),
    onErrorContainer = FantasyError,
    outline = FantasyGold.copy(alpha = 0.8f),
    outlineVariant = FantasyGold.copy(alpha = 0.35f),
    scrim = Color.Black.copy(alpha = 0.8f),
    inverseSurface = FantasySurfaceLight,
    inverseOnSurface = FantasyText,
    inversePrimary = FantasyGold
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
//  COLOR SCHEME - BACHECA FANTASY (LEGNO E PERGAMENA)
// ============================================================

private val FantasyBoardColorScheme = darkColorScheme(
    primary = FantasyBoardGoldLeaf,
    onPrimary = FantasyBoardInk,
    primaryContainer = FantasyBoardWoodLight,
    onPrimaryContainer = FantasyBoardParchment,
    secondary = FantasyBoardRedWax,
    onSecondary = FantasyBoardParchment,
    secondaryContainer = FantasyBoardWoodDark,
    onSecondaryContainer = FantasyBoardGoldLeaf,
    tertiary = FantasyBoardParchment,
    onTertiary = FantasyBoardInk,
    tertiaryContainer = FantasyBoardWood,
    onTertiaryContainer = FantasyBoardParchment,
    background = FantasyBoardWoodDark,
    onBackground = FantasyBoardParchment,
    surface = FantasyBoardWood,
    onSurface = FantasyBoardParchment,
    surfaceVariant = FantasyBoardWoodLight,
    onSurfaceVariant = FantasyBoardParchmentDark,
    surfaceTint = FantasyBoardGoldLeaf,
    error = FantasyBoardRedWax,
    onError = FantasyBoardParchment,
    errorContainer = FantasyBoardRedWax.copy(alpha = 0.15f),
    onErrorContainer = FantasyBoardRedWax,
    outline = FantasyBoardWoodLight,
    outlineVariant = FantasyBoardWoodDark,
    scrim = Color.Black.copy(alpha = 0.8f),
    inverseSurface = FantasyBoardParchment,
    inverseOnSurface = FantasyBoardInk,
    inversePrimary = FantasyBoardWood
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
    themeType: AppTheme = AppTheme.DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        AppTheme.DEFAULT -> if (darkTheme) FantasyDarkColorScheme else FantasyLightColorScheme
        AppTheme.FANTASY -> if (darkTheme) FantasyBoardColorScheme else FantasyLightColorScheme // Idem qui se vuoi che segua il sistema, oppure lascialo così se è voluto
        AppTheme.REGALE -> if (darkTheme) RegaleDarkColorScheme else FantasyLightColorScheme
        AppTheme.ARCADE -> ArcadeColorScheme
    }

    val typography = when (themeType) {
        AppTheme.ARCADE -> ArcadeTypography
        else -> FantasyTypography
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}