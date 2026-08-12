package com.example.quester.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ArcadeButtonStyle : DefaultButtonStyle() {

    @Composable
    override fun getButtonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color(0xFFE0E0E0),
            disabledContentColor = Color(0xFF888888)
        )
    }

    @Composable
    override fun getOutlinedButtonColors(): ButtonColors {
        return ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black,
            disabledContentColor = Color(0xFF888888)
        )
    }

    @Composable
    override fun getTextButtonColors(): ButtonColors {
        return ButtonDefaults.textButtonColors(
            contentColor = Color.Black,
            disabledContentColor = Color(0xFF888888)
        )
    }

    @Composable
    override fun getButtonShape(): Shape {
        return RoundedCornerShape(4.dp)
    }

    @Composable
    fun getBorderStroke(): BorderStroke {
        return BorderStroke(3.dp, Color.Black)
    }

    @Composable
    override fun getTextStyle(): TextStyle {
        return TextStyle(
            // ✅ USA IL VERO FONT PIXELATO
            fontFamily = QuesterPixel,  // ← Invece di FontFamily.Monospace
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 1.5.sp
        )
    }

    override fun getButtonModifier(pressed: Boolean, enabled: Boolean): Modifier {
        return if (pressed) {
            Modifier
                .padding(bottom = 6.dp)
                .offset(y = 4.dp)
        } else {
            Modifier
                .drawBehind {
                    drawRoundRect(
                        color = Color.Black,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
                .padding(bottom = 6.dp)
        }
    }

    @Composable
    override fun getErrorTextColor(): Color {
        return Color(0xFFFF2222)
    }

    @Composable
    override fun getSuccessTextColor(): Color {
        return Color(0xFF00AA22)
    }
}