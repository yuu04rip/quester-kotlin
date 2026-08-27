package com.example.quester.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.quester.R

// Font fantasy personalizzato (default / regale)
val QuesterFantasy = FontFamily(
    Font(R.font.mediaval)
)

// Font pixelato per Arcade
val QuesterPixel = FontFamily(
    Font(R.font.pixelfont)
)

// Alias globali per retrocompatibilità ed evitare duplicati nei componenti
val FantasyFont = QuesterFantasy
val PixelFont = QuesterPixel

// ============================================================
//  TYPOGRAPHY FANTASY (DEFAULT / REGALE)
// ============================================================

val FantasyTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = QuesterFantasy,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = QuesterFantasy,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = QuesterFantasy,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = QuesterFantasy,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = QuesterFantasy,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = QuesterFantasy,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = QuesterFantasy,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = QuesterFantasy,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = QuesterFantasy,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = QuesterFantasy,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = QuesterFantasy,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    )
)

// ============================================================
//  TYPOGRAPHY ARCADE (PIXEL)
// ============================================================

val ArcadeTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = QuesterPixel,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 1.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = QuesterPixel,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = QuesterPixel,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = QuesterPixel,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = QuesterPixel,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = QuesterPixel,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = QuesterPixel,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    bodySmall = TextStyle(
        fontFamily = QuesterPixel,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    ),
    labelLarge = TextStyle(
        fontFamily = QuesterPixel,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelMedium = TextStyle(
        fontFamily = QuesterPixel,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    ),
    labelSmall = TextStyle(
        fontFamily = QuesterPixel,
        fontWeight = FontWeight.Medium,
        fontSize = 8.sp
    )
)

val Typography = FantasyTypography