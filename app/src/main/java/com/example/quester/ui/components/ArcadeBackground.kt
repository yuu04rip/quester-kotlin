package com.example.quester.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.quester.R
import com.example.quester.ui.theme.ThemeManager
import com.example.quester.ui.theme.AppTheme

@Composable
fun ArcadeBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isArcade = ThemeManager.theme == AppTheme.ARCADE

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (isArcade) {
                    // SFONDO ARCADE (con immagine)
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A0A0F),
                            Color(0xFF12121A),
                            Color(0xFF1A1A2E)
                        )
                    )
                } else {
                    // SFONDO FANTASY
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D0B14),
                            Color(0xFF171321),
                            Color(0xFF0B0813)
                        )
                    )
                }
            )
    ) {
        // Se Arcade, mostra l'immagine pixelata come overlay
        if (isArcade) {
            Image(
                painter = painterResource(id = R.drawable.bg_arcade_pixel),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        content()
    }
}