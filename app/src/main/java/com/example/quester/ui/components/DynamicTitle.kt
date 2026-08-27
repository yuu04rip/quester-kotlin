package com.example.quester.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.quester.ui.theme.AppTheme
import com.example.quester.ui.theme.FantasyFont
import com.example.quester.ui.theme.PixelFont
import com.example.quester.ui.theme.ThemeManager

/**
 * Componente unico e centralizzato per i titoli dinamici in base al tema corrente.
 */
@Composable
fun DynamicTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Bold
) {
    val fontFamily = when (ThemeManager.theme) {
        AppTheme.ARCADE -> PixelFont
        else -> FantasyFont // Copre FANTASY, REGALE, DEFAULT, ecc.
    }

    Text(
        text = text,
        modifier = modifier,
        style = style.copy(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            color = color
        )
    )
}