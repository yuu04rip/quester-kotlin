package com.example.quester.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.quester.ui.theme.AppTheme
import com.example.quester.ui.theme.ThemeManager
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun CRTEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isArcade = ThemeManager.theme == AppTheme.ARCADE
    val coroutineScope = rememberCoroutineScope()

    var touchCenter by remember { mutableStateOf(Offset.Unspecified) }
    val rippleRadius = remember { Animatable(0f) }
    val rippleAlpha = remember { Animatable(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (isArcade) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures { offset ->
                            touchCenter = offset
                            coroutineScope.launch {
                                rippleRadius.snapTo(0f)
                                rippleAlpha.snapTo(0.45f)
                                launch {
                                    rippleRadius.animateTo(
                                        targetValue = 350f,
                                        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                                    )
                                }
                                launch {
                                    rippleAlpha.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(durationMillis = 600, easing = LinearEasing)
                                    )
                                }
                            }
                        }
                    }
                } else Modifier
            )
    ) {
        content()

        if (isArcade) {
            val infiniteTransition = rememberInfiniteTransition(label = "crt_effects")

            val flickerAlpha by infiniteTransition.animateFloat(
                initialValue = 0.05f,
                targetValue = 0.12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 40, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "flicker_alpha"
            )

            val mainInterferenceProgress by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "main_interference"
            )

            val secondaryInterferenceProgress by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1800, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "secondary_interference"
            )

            val glitchTrigger by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 250, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glitch_trigger"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawScanlines(flickerAlpha)
                drawMainInterference(mainInterferenceProgress)
                drawSecondaryInterference(secondaryInterferenceProgress)
                drawTouchRipple(touchCenter, rippleRadius.value, rippleAlpha.value)
                drawGlitches(glitchTrigger)
                drawVignette()
            }
        }
    }
}

// ===== DRAW HELPERS =====

private fun DrawScope.drawScanlines(flickerAlpha: Float) {
    val width = size.width
    val height = size.height
    for (y in 0 until height.toInt() step 3) {
        drawLine(
            color = Color.Black.copy(alpha = flickerAlpha),
            start = Offset(0f, y.toFloat()),
            end = Offset(width, y.toFloat()),
            strokeWidth = 1.2f
        )
    }
}

private fun DrawScope.drawMainInterference(progress: Float) {
    val width = size.width
    val height = size.height
    val barY = progress * (height + 200f) - 100f
    val barHeight = 110f

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.04f),
                Color.Black.copy(alpha = 0.22f),
                Color.Cyan.copy(alpha = 0.02f),
                Color.Transparent
            ),
            startY = barY - barHeight,
            endY = barY + barHeight
        ),
        topLeft = Offset(0f, barY - barHeight),
        size = Size(width, barHeight * 2)
    )
}

private fun DrawScope.drawSecondaryInterference(progress: Float) {
    val width = size.width
    val height = size.height
    val barY = progress * (height + 100f) - 50f
    val barHeight = 35f

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.12f),
                Color.Transparent
            ),
            startY = barY - barHeight,
            endY = barY + barHeight
        ),
        topLeft = Offset(0f, barY - barHeight),
        size = Size(width, barHeight * 2)
    )
}

private fun DrawScope.drawTouchRipple(touchCenter: Offset, radius: Float, alpha: Float) {
    if (touchCenter == Offset.Unspecified || alpha <= 0f) return

    drawCircle(
        color = Color.Cyan.copy(alpha = alpha * 0.7f),
        center = touchCenter,
        radius = radius,
        style = Stroke(width = 12f)
    )

    drawCircle(
        color = Color.Black.copy(alpha = alpha * 0.5f),
        center = touchCenter,
        radius = (radius - 14f).coerceAtLeast(0f),
        style = Stroke(width = 8f)
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha * 0.4f),
                Color.Transparent
            ),
            center = touchCenter,
            radius = (radius * 0.5f).coerceAtLeast(1f)
        ),
        center = touchCenter,
        radius = radius * 0.5f
    )
}

private fun DrawScope.drawGlitches(glitchTrigger: Float) {
    val width = size.width
    val height = size.height
    val random = Random((glitchTrigger * 1000).toInt())

    if (random.nextFloat() <= 0.4f) return

    val numGlitches = random.nextInt(1, 4)
    for (i in 0 until numGlitches) {
        val glitchY = random.nextFloat() * height
        val glitchHeight = random.nextFloat() * 4f + 1f
        val glitchAlpha = random.nextFloat() * 0.25f

        drawRect(
            color = Color.White.copy(alpha = glitchAlpha),
            topLeft = Offset(0f, glitchY),
            size = Size(width, glitchHeight)
        )
    }
}

private fun DrawScope.drawVignette() {
    val width = size.width
    val height = size.height
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent,
                Color.Black.copy(alpha = 0.35f),
                Color.Black.copy(alpha = 0.7f)
            ),
            center = Offset(width / 2f, height / 2f),
            radius = maxOf(width, height) * 0.75f
        ),
        size = Size(width, height)
    )
}