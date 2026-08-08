package com.example.quester.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MagicBurstButton(
    text: String,
    loading: Boolean,
    onClickAfterEffect: suspend () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var playEffect by remember { mutableStateOf(false) }

    val progress = remember { Animatable(0f) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (playEffect) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                val w = size.width
                val h = size.height
                val cx = w / 2f
                val cy = h / 2f

                // Aura centrale
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x66FFB74D),
                            Color(0x44FF7043),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = 120f + 160f * progress.value
                    ),
                    radius = 120f + 160f * progress.value,
                    center = Offset(cx, cy)
                )

                // Anelli magici
                drawCircle(
                    color = Color(0xFFFFD54F).copy(alpha = 1f - progress.value),
                    radius = 40f + 120f * progress.value,
                    center = Offset(cx, cy),
                    style = Stroke(width = 4f)
                )
                drawCircle(
                    color = Color(0xFFAB47BC).copy(alpha = 1f - progress.value),
                    radius = 20f + 90f * progress.value,
                    center = Offset(cx, cy),
                    style = Stroke(width = 3f)
                )

                // Particelle (12)
                repeat(12) { i ->
                    val angle = (2 * PI * i / 12.0).toFloat()
                    val dist = 20f + 160f * progress.value
                    val px = cx + cos(angle) * dist
                    val py = cy + sin(angle) * dist
                    drawCircle(
                        color = if (i % 2 == 0) Color(0xFFFFA726) else Color(0xFFEF5350),
                        radius = (6f - 3f * progress.value).coerceAtLeast(1.5f),
                        center = Offset(px, py),
                        alpha = (1f - progress.value).coerceAtLeast(0f)
                    )
                }
            }
        }

        Button(
            onClick = {
                if (loading) return@Button
                scope.launch {
                    playEffect = true
                    progress.snapTo(0f)
                    progress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
                    )
                    delay(120)
                    playEffect = false
                    onClickAfterEffect()
                }
            },
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD4A84F),
                contentColor = Color(0xFF1B1408)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF1B1408))
            } else {
                Text(text = text)
            }
        }
    }
}