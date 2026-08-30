package com.example.quester.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private data class PixelButtonColors(
    val background: Color,
    val text: Color,
    val border: Color
)

private fun getPixelButtonColors(isArcade: Boolean, isPressed: Boolean): PixelButtonColors {
    if (isArcade) {
        val bg = if (isPressed) Color(0xFF0A0A0F) else Color(0xFF00FF41)
        val txt = if (isPressed) Color(0xFF00FF41) else Color(0xFF0A0A0F)
        return PixelButtonColors(background = bg, text = txt, border = Color(0xFF00FF41))
    }

    return PixelButtonColors(
        background = Color(0xFFD4A84F),
        text = Color(0xFF0D0B14),
        border = Color(0xFFD4A84F)
    )
}

@Composable
fun PixelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isArcade: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = remember { mutableStateOf(false) }
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(pressed.value) {
        val targetOffset = if (pressed.value) 4f else 0f
        offsetY.animateTo(targetOffset, tween(durationMillis = 80))
    }

    val cornerRadius = 4.dp
    val borderWidth = 3.dp
    val buttonColors = getPixelButtonColors(isArcade, pressed.value)

    Box(
        modifier = modifier
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .drawBehind {
                drawRoundRect(
                    color = Color.Black,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
                )
            }
            .padding(bottom = 6.dp)
            .background(buttonColors.background, shape = RoundedCornerShape(cornerRadius))
            .border(borderWidth, buttonColors.border, shape = RoundedCornerShape(cornerRadius))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                pressed.value = true
                onClick()
                pressed.value = false
            }
            .padding(
                horizontal = if (isArcade) 28.dp else 24.dp,
                vertical = if (isArcade) 16.dp else 12.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = buttonColors.text,
            fontSize = if (isArcade) 20.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = if (isArcade) FontFamily.Monospace else FontFamily.Serif,
            letterSpacing = if (isArcade) 2.sp else 0.sp
        )
    }
}