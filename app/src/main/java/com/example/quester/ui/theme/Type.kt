package com.example.quester.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.quester.R

// Font fantasy personalizzato
private val QuesterFantasy = FontFamily(
    Font(R.font.northeternal)
)

// Fallback leggibile per numeri/simboli mancanti
private val QuesterFallback = FontFamily.SansSerif

// Family con fallback: prima prova fantasy, poi sans
private val QuesterHybrid = FontFamily(
    Font(R.font.northeternal)
)

val Typography = Typography(
    // Titoli fantasy
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

    // Testi normali / campi input: meglio leggibili
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
    labelLarge = TextStyle(
        fontFamily = QuesterFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)