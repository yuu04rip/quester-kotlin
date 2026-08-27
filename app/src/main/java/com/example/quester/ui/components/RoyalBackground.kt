package com.example.quester.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.quester.R
import com.example.quester.ui.theme.AppTheme
import com.example.quester.ui.theme.ThemeManager

@Composable
fun RoyalBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val currentTheme by ThemeManager.currentTheme.collectAsState()
    val isRegal = currentTheme == AppTheme.REGALE

    if (!isRegal) {
        content()
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Sfondo pixelato regale (assicurati di avere bg_royal_pixel.png in res/drawable)
        Image(
            painter = painterResource(id = R.drawable.bg_royal_pixel),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Leggera patina scura per far risaltare i contenuti in primo piano
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        // Contenuto principale sopra lo sfondo
        content()
    }
}