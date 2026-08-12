package com.example.quester.ui.theme

import androidx.compose.runtime.Composable

@Composable
fun getButtonStyle(): ButtonStyle {
    return when (ThemeManager.theme) {
        AppTheme.FANTASY -> DefaultButtonStyle()
        AppTheme.ARCADE -> ArcadeButtonStyle()
    }
}