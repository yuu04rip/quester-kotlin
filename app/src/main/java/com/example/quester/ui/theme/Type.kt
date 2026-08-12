package com.example.quester.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.quester.R

// Font fantasy personalizzato (default)
val QuesterFantasy = FontFamily(
    Font(R.font.northeternal)
)

// Font pixelato per Arcade
val QuesterPixel = FontFamily(
    Font(R.font.pixelfont)
)

// Fallback SansSerif per numeri e testi leggibili
private val QuesterFallback = FontFamily.SansSerif

// ============================================================
//  TYPOGRAPHY FANTASY (DEFAULT)
//  ✅ I numeri usano SansSerif, le lettere usano Northeternal
// ============================================================

val FantasyTypography = Typography(
    // Headline - solo testo (Northeternal)
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

    // Title - misto (testo Northeternal, numeri SansSerif)
    titleLarge = TextStyle(
        fontFamily = QuesterFallback,  // ✅ SansSerif per sicurezza
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = QuesterFallback,  // ✅ SansSerif per numeri
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = QuesterFallback,  // ✅ SansSerif
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),

    // Body - sempre SansSerif (leggibile)
    bodyLarge = TextStyle(
        fontFamily = QuesterFallback,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = QuesterFallback,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = QuesterFallback,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),

    // Label - SansSerif per numeri
    labelLarge = TextStyle(
        fontFamily = QuesterFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = QuesterFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = QuesterFallback,  // ✅ SansSerif per numeri
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    )
)

// ============================================================
//  TYPOGRAPHY ARCADE (PIXEL)
//  ✅ TUTTO in PixelFont (anche i numeri)
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

// ============================================================
//  TYPOGRAPHY COMPLETA
// ============================================================

val Typography = FantasyTypography