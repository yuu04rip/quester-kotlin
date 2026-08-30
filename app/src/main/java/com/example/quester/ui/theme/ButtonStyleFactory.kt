package com.example.quester.ui.theme

import androidx.compose.runtime.Composable

@Composable
fun getButtonStyle(): ButtonStyle {
    return when (ThemeManager.theme) {
        AppTheme.DEFAULT, AppTheme.REGALE -> DefaultButtonStyle()
        AppTheme.FANTASY -> FantasyBoardButtonStyle() // Usa la classe nel nuovo file
        AppTheme.ARCADE -> ArcadeButtonStyle()
    }
}