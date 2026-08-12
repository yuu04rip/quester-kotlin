package com.example.quester.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

open class DefaultButtonStyle : ButtonStyle {

    @Composable
    override fun getButtonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = FantasyGold,
            contentColor = Color(0xFF0D0B14),
            disabledContainerColor = FantasyGold.copy(alpha = 0.3f),
            disabledContentColor = Color(0xFF0D0B14).copy(alpha = 0.5f)
        )
    }

    @Composable
    override fun getOutlinedButtonColors(): ButtonColors {
        return ButtonDefaults.outlinedButtonColors(
            contentColor = FantasyGold,
            disabledContentColor = FantasyGold.copy(alpha = 0.3f)
        )
    }

    @Composable
    override fun getTextButtonColors(): ButtonColors {
        return ButtonDefaults.textButtonColors(
            contentColor = FantasyGold,
            disabledContentColor = FantasyGold.copy(alpha = 0.3f)
        )
    }

    @Composable
    override fun getButtonShape(): androidx.compose.ui.graphics.Shape {
        return MaterialTheme.shapes.medium
    }

    @Composable
    override fun getTextStyle(): TextStyle {
        return TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }

    override fun getButtonModifier(pressed: Boolean, enabled: Boolean): Modifier {
        return Modifier
    }

    @Composable
    override fun getErrorTextColor(): Color {
        return FantasyError
    }

    @Composable
    override fun getSuccessTextColor(): Color {
        return FantasySuccess
    }
}