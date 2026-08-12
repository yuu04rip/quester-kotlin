package com.example.quester.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun FloatingTextPopup(
    text: String,
    isArcade: Boolean = true,
    onComplete: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(true) }
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        offsetY.animateTo(
            targetValue = -100f,
            animationSpec = tween(durationMillis = 1200)
        )
        visible = false
        onComplete()
    }

    if (visible) {
        Text(
            text = text,
            modifier = Modifier
                .offset(y = offsetY.value.dp)
                .size(200.dp),
            color = if (isArcade) Color(0xFF00FF41) else Color(0xFFFFD700),
            fontSize = if (isArcade) 20.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = if (isArcade) FontFamily.Monospace else FontFamily.Serif
        )
    }
}