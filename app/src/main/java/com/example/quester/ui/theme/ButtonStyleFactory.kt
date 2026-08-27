package com.example.quester.ui.theme

import androidx.compose.runtime.Composable

@Composable
fun getButtonStyle(): ButtonStyle {
    return when (ThemeManager.theme) {
        AppTheme.DEFAULT, AppTheme.FANTASY, AppTheme.REGALE -> DefaultButtonStyle()
        AppTheme.ARCADE -> ArcadeButtonStyle()
    }
}