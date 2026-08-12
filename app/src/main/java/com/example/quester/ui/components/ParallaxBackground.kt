package com.example.quester.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ParallaxBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            offsetX.animateTo(
                targetValue = 100f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 20000, delayMillis = 0)
                )
            )
            offsetX.snapTo(0f)
            delay(100)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Sfondo con stelle e pixel art
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawParallaxBackground(offsetX.value)
        }
        content()
    }
}

private fun DrawScope.drawParallaxBackground(offset: Float) {
    val width = size.width
    val height = size.height

    // Sfondo principale (cielo scuro)
    drawRect(
        color = Color(0xFF0A0A1A),
        topLeft = Offset(0f, 0f),
        size = size
    )

    // Stelle (pixel art)
    val starPositions = listOf(
        Pair(0.1f, 0.1f) to 3f,
        Pair(0.8f, 0.2f) to 2f,
        Pair(0.3f, 0.4f) to 4f,
        Pair(0.9f, 0.6f) to 3f,
        Pair(0.2f, 0.8f) to 2f,
        Pair(0.7f, 0.3f) to 5f,
        Pair(0.5f, 0.7f) to 3f,
        Pair(0.1f, 0.5f) to 4f,
        Pair(0.6f, 0.9f) to 2f,
        Pair(0.4f, 0.2f) to 3f
    )

    starPositions.forEach { (position, sizePx) ->
        val x = (position.first * width + offset * 2) % width
        val y = position.second * height
        drawPixelStar(x, y, sizePx)
    }

    // Linea dell'orizzonte (montagne pixel)
    drawPixelMountains(width, height, offset)
}

private fun DrawScope.drawPixelStar(x: Float, y: Float, size: Float) {
    // Stella pixelata (quadrati invece di cerchi)
    val halfSize = size / 2
    val pixelSize = size / 4
    repeat(4) { row ->
        repeat(4) { col ->
            if (row == 1 || row == 2 || col == 1 || col == 2) {
                drawRect(
                    color = Color(0x88FFFFFF),
                    topLeft = Offset(x - halfSize + col * pixelSize, y - halfSize + row * pixelSize),
                    size = androidx.compose.ui.geometry.Size(pixelSize, pixelSize)
                )
            }
        }
    }
}

private fun DrawScope.drawPixelMountains(width: Float, height: Float, offset: Float) {
    val mountainColor = Color(0xFF1A1A2E)
    val mountainColor2 = Color(0xFF2A1A3E)

    // Montagna 1
    drawRect(
        color = mountainColor,
        topLeft = Offset((-50f + offset * 0.5f) % (width + 100f), height * 0.6f),
        size = androidx.compose.ui.geometry.Size(150f, height * 0.4f)
    )

    // Montagna 2
    drawRect(
        color = mountainColor2,
        topLeft = Offset((100f + offset * 0.3f) % (width + 200f), height * 0.65f),
        size = androidx.compose.ui.geometry.Size(200f, height * 0.35f)
    )

    // Montagna 3
    drawRect(
        color = mountainColor,
        topLeft = Offset((250f + offset * 0.4f) % (width + 300f), height * 0.7f),
        size = androidx.compose.ui.geometry.Size(120f, height * 0.3f)
    )
}