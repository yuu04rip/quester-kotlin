package com.example.quester.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.quester.R
import com.example.quester.ui.theme.ThemeManager
import com.example.quester.ui.theme.AppTheme

// Font fantasy (default)
val FantasyFont = FontFamily(
    Font(R.font.northeternal)
)

// Font pixel (Arcade)
val PixelFont = FontFamily(
    Font(R.font.pixelfont)
)

/**
 * Componente per titoli con font dinamico in base al tema
 * - Fantasy → Northeternal per lettere, SansSerif per numeri
 * - Arcade → PixelFont per tutto (lettere e numeri)
 */
@Composable
fun FantasyTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Bold
) {
    val isArcade = ThemeManager.theme == AppTheme.ARCADE

    // Font per le lettere in base al tema
    val letterFont = if (isArcade) PixelFont else FantasyFont
    // Font per i numeri in base al tema
    val numberFont = if (isArcade) PixelFont else FontFamily.SansSerif

    // Scompone il testo in parti (testo e numeri separati)
    val parts = splitTextAndNumbers(text)

    if (parts.size == 1) {
        // Se è tutto testo o tutto numeri
        val fontFamily = if (parts.first().all { it.isDigit() }) {
            numberFont  // ✅ Numeri: SansSerif in Fantasy, PixelFont in Arcade
        } else {
            letterFont  // ✅ Lettere: Northeternal in Fantasy, PixelFont in Arcade
        }

        val finalStyle = style.copy(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            color = color
        )

        Text(
            text = text,
            modifier = modifier,
            style = finalStyle
        )
    } else {
        // Testo con numeri mescolati - usa AnnotatedString
        val annotatedString = buildAnnotatedString {
            parts.forEach { part ->
                val isDigit = part.all { it.isDigit() }
                val fontFamily = if (isDigit) {
                    numberFont  // ✅ Numeri: SansSerif in Fantasy, PixelFont in Arcade
                } else {
                    letterFont  // ✅ Lettere: Northeternal in Fantasy, PixelFont in Arcade
                }
                withStyle(
                    style = SpanStyle(
                        fontFamily = fontFamily,
                        fontWeight = fontWeight,
                        color = color
                    )
                ) {
                    append(part)
                }
            }
        }

        Text(
            text = annotatedString,
            modifier = modifier,
            style = style.copy(
                fontWeight = fontWeight,
                color = color
            )
        )
    }
}

/**
 * Divide il testo in parti: numeri e non numeri
 * Es: "Missione 5" → ["Missione ", "5"]
 */
private fun splitTextAndNumbers(text: String): List<String> {
    if (text.isEmpty()) return emptyList()

    val result = mutableListOf<String>()
    var currentPart = ""
    var currentIsDigit = text[0].isDigit()

    for (char in text) {
        val isDigit = char.isDigit()
        if (isDigit == currentIsDigit) {
            currentPart += char
        } else {
            if (currentPart.isNotEmpty()) {
                result.add(currentPart)
            }
            currentPart = char.toString()
            currentIsDigit = isDigit
        }
    }
    if (currentPart.isNotEmpty()) {
        result.add(currentPart)
    }
    return result
}