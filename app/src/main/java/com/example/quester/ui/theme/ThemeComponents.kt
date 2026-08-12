package com.example.quester.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun getCardBorder(): BorderStroke {
    val isArcade = ThemeManager.theme == AppTheme.ARCADE

    return if (isArcade) {
        BorderStroke(
            width = 2.dp,
            color = Color(0xFF00FF41).copy(alpha = 0.5f)
        )
    } else {
        BorderStroke(
            width = 2.dp,
            color = FantasyPurple.copy(alpha = 0.65f)
        )
    }
}

@Composable
fun getCardShape() = RoundedCornerShape(
    if (ThemeManager.theme == AppTheme.ARCADE) 12.dp else 28.dp
)

@Composable
fun getCardElevation() = if (ThemeManager.theme == AppTheme.ARCADE) 8.dp else 24.dp