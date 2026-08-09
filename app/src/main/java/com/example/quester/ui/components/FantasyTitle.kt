package com.example.quester.ui.components

import androidx.compose.foundation.text.BasicText
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

// Carica il font personalizzato
private val FantasyFont = FontFamily(
    Font(R.font.northeternal)
)

/**
 * Componente per titoli con font fantasy per il testo e font normale per i numeri
 */
@Composable
fun FantasyTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Bold
) {
    // Scompone il testo in parti (testo e numeri separati)
    val parts = splitTextAndNumbers(text)

    if (parts.size == 1) {
        // Se è tutto testo o tutto numeri
        val fontFamily = if (parts.first().all { it.isDigit() }) {
            FontFamily.SansSerif
        } else {
            FantasyFont
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
                withStyle(
                    style = SpanStyle(
                        fontFamily = if (isDigit) FontFamily.SansSerif else FantasyFont,
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
 * Es: "Livello 10" → ["Livello ", "10"]
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