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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.ui.theme.AppTheme
import com.example.quester.ui.theme.QuesterPixel
import com.example.quester.ui.theme.ThemeManager
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
    val currentTheme by ThemeManager.currentTheme.collectAsState()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (playEffect) {
            BurstEffectCanvas(theme = currentTheme, progress = progress.value)
        }

        MagicButton(
            text = text,
            loading = loading,
            theme = currentTheme,
            onClick = {
                if (loading) return@MagicButton
                scope.launch {
                    playEffect = true
                    progress.snapTo(0f)
                    progress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
                    )
                    delay(120L)
                    playEffect = false
                    onClickAfterEffect()
                }
            }
        )
    }
}

// ===== BURST EFFECT CANVAS =====

@Composable
private fun BurstEffectCanvas(
    theme: AppTheme,
    progress: Float
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        val isArcade = theme == AppTheme.ARCADE
        val (auraColors, ringColors, particleColors) = getEffectColors(theme)
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Aura centrale
        drawCircle(
            brush = Brush.radialGradient(
                colors = auraColors,
                center = Offset(cx, cy),
                radius = 120f + 160f * progress
            ),
            radius = 120f + 160f * progress,
            center = Offset(cx, cy)
        )

        // Anelli magici
        drawCircle(
            color = ringColors[0].copy(alpha = 1f - progress),
            radius = 40f + 120f * progress,
            center = Offset(cx, cy),
            style = Stroke(width = if (isArcade) 6f else 4f)
        )
        drawCircle(
            color = ringColors[1].copy(alpha = 1f - progress),
            radius = 20f + 90f * progress,
            center = Offset(cx, cy),
            style = Stroke(width = if (isArcade) 5f else 3f)
        )

        // Particelle
        drawParticles(isArcade, progress, cx, cy, particleColors)

        // Stelle extra per Arcade
        if (isArcade) {
            drawPixelStars(progress, cx, cy)
        }
    }
}

// ===== GET EFFECT COLORS =====

private fun getEffectColors(theme: AppTheme): EffectColors {
    return when (theme) {
        AppTheme.ARCADE -> {
            EffectColors(
                auraColors = listOf(
                    Color(0x6600FF41),
                    Color(0x4400FF41),
                    Color.Transparent
                ),
                ringColors = listOf(
                    Color(0xFF00FF41),
                    Color(0xFF00FF41)
                ),
                particleColors = listOf(
                    Color(0xFFFF00FF),
                    Color(0xFF00FFFF)
                )
            )
        }
        AppTheme.REGALE -> {
            EffectColors(
                auraColors = listOf(
                    Color(0x66F0CC78),
                    Color(0x44D4A84F),
                    Color.Transparent
                ),
                ringColors = listOf(
                    Color(0xFFF0CC78),
                    Color(0xFF9C27B0)
                ),
                particleColors = listOf(
                    Color(0xFFD4A84F),
                    Color(0xFF9C27B0)
                )
            )
        }
        else -> {
            EffectColors(
                auraColors = listOf(
                    Color(0x66FFB74D),
                    Color(0x44FF7043),
                    Color.Transparent
                ),
                ringColors = listOf(
                    Color(0xFFFFD54F),
                    Color(0xFFAB47BC)
                ),
                particleColors = listOf(
                    Color(0xFFFFA726),
                    Color(0xFFEF5350)
                )
            )
        }
    }
}

// ===== DRAW PARTICLES =====

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawParticles(
    isArcade: Boolean,
    progress: Float,
    cx: Float,
    cy: Float,
    particleColors: List<Color>
) {
    val particleCount = if (isArcade) 16 else 12
    repeat(particleCount) { i ->
        val angle = (2 * PI * i / particleCount).toFloat()
        val dist = 20f + 160f * progress
        val px = cx + cos(angle) * dist
        val py = cy + sin(angle) * dist
        drawCircle(
            color = if (i % 2 == 0) particleColors[0] else particleColors[1],
            radius = if (isArcade) {
                (8f - 4f * progress).coerceAtLeast(2f)
            } else {
                (6f - 3f * progress).coerceAtLeast(1.5f)
            },
            center = Offset(px, py),
            alpha = (1f - progress).coerceAtLeast(0f)
        )
    }
}

// ===== DRAW PIXEL STARS =====

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPixelStars(
    progress: Float,
    cx: Float,
    cy: Float
) {
    repeat(8) { j ->
        val angle = (2 * PI * j / 8 + progress * 0.5).toFloat()
        val dist = 60f + 100f * progress
        val px = cx + cos(angle) * dist
        val py = cy + sin(angle) * dist
        drawCircle(
            color = Color(0xCCFFFFFF),
            radius = (4f - 2f * progress).coerceAtLeast(1f),
            center = Offset(px, py),
            alpha = (0.8f - progress * 0.5f).coerceAtLeast(0f)
        )
    }
}

// ===== MAGIC BUTTON COLORS & CONFIG =====

private data class MagicButtonThemeConfig(
    val colors: androidx.compose.material3.ButtonColors,
    val shape: androidx.compose.ui.graphics.Shape,
    val modifier: Modifier,
    val textFont: FontFamily,
    val fontSize: androidx.compose.ui.unit.TextUnit,
    val letterSpacing: androidx.compose.ui.unit.TextUnit,
    val indicatorColor: Color
)

@Composable
private fun rememberMagicButtonConfig(theme: AppTheme): MagicButtonThemeConfig {
    return when (theme) {
        AppTheme.ARCADE -> {
            MagicButtonThemeConfig(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FF41),
                    contentColor = Color(0xFF0A0A0F),
                    disabledContainerColor = Color(0xFF00FF41).copy(alpha = 0.3f),
                    disabledContentColor = Color(0xFF0A0A0F).copy(alpha = 0.5f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(bottom = 6.dp),
                textFont = QuesterPixel,
                fontSize = 18.sp,
                letterSpacing = 2.sp,
                indicatorColor = Color(0xFF0A0A0F)
            )
        }
        AppTheme.REGALE -> {
            MagicButtonThemeConfig(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF0CC78),
                    contentColor = Color(0xFF1A1208),
                    disabledContainerColor = Color(0xFFF0CC78).copy(alpha = 0.3f),
                    disabledContentColor = Color(0xFF1A1208).copy(alpha = 0.5f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(bottom = 2.dp),
                textFont = FontFamily.Serif,
                fontSize = 17.sp,
                letterSpacing = 1.sp,
                indicatorColor = Color(0xFF1A1208)
            )
        }
        else -> {
            MagicButtonThemeConfig(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD4A84F),
                    contentColor = Color(0xFF1B1408),
                    disabledContainerColor = Color(0xFFD4A84F).copy(alpha = 0.3f),
                    disabledContentColor = Color(0xFF1B1408).copy(alpha = 0.5f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                textFont = FontFamily.Serif,
                fontSize = 16.sp,
                letterSpacing = 0.sp,
                indicatorColor = Color(0xFF1B1408)
            )
        }
    }
}

// ===== MAGIC BUTTON =====

@Composable
private fun MagicButton(
    text: String,
    loading: Boolean,
    theme: AppTheme,
    onClick: () -> Unit
) {
    val config = rememberMagicButtonConfig(theme)

    Button(
        onClick = onClick,
        enabled = !loading,
        colors = config.colors,
        shape = config.shape,
        modifier = config.modifier
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = config.indicatorColor
            )
        } else {
            Text(
                text = text,
                fontFamily = config.textFont,
                fontWeight = FontWeight.Bold,
                fontSize = config.fontSize,
                letterSpacing = config.letterSpacing
            )
        }
    }
}

// ===== DATA CLASS =====

private data class EffectColors(
    val auraColors: List<Color>,
    val ringColors: List<Color>,
    val particleColors: List<Color>
)