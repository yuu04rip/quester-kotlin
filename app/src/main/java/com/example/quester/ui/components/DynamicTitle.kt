package com.example.quester.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.quester.ui.theme.ThemeManager
import com.example.quester.ui.theme.AppTheme

/**
 * Componente per titoli che segue il tema corrente
 * - Fantasy → font Northeternal
 * - Arcade → font Pixel
 */
@Composable
fun DynamicTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Bold
) {
    // Usa il font del tema corrente
    val fontFamily = when (ThemeManager.theme) {
        AppTheme.FANTASY -> FantasyFont
        AppTheme.ARCADE -> PixelFont
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