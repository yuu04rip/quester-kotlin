package com.example.quester.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    // 1. Stato reattivo per le Coroutine e StateFlow
    private val _currentTheme = MutableStateFlow(AppTheme.FANTASY)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    // 2. Proprietà diretta per il vecchio codice (es. ArcadeBackground)
    val theme: AppTheme
        get() = _currentTheme.value

    // 3. Funzione unica per aggiornare il tema ovunque
    fun setTheme(theme: AppTheme) {
        _currentTheme.value = theme
    }

    fun isThemeOwned(theme: AppTheme, ownedItems: List<String>): Boolean {
        val themeId = when (theme) {
            AppTheme.FANTASY -> "theme_fantasy"
            AppTheme.ARCADE -> "theme_arcade"
        }
        return themeId in ownedItems
    }

    fun getThemeDisplayName(theme: AppTheme): String {
        return when (theme) {
            AppTheme.FANTASY -> "Fantasy"
            AppTheme.ARCADE -> "Arcade"
        }
    }
}

enum class AppTheme {
    FANTASY,
    ARCADE
}