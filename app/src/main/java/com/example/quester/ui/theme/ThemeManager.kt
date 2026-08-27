package com.example.quester.ui.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    // 1. Stato reattivo per le Coroutine e StateFlow
    private val _currentTheme = MutableStateFlow(AppTheme.DEFAULT)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    // 2. Proprietà diretta per il vecchio codice
    val theme: AppTheme
        get() = _currentTheme.value

    // 3. Funzione unica per aggiornare il tema ovunque
    fun setTheme(theme: AppTheme) {
        _currentTheme.value = theme
    }

    fun isThemeOwned(theme: AppTheme, ownedItems: List<String>): Boolean {
        val themeId = when (theme) {
            AppTheme.DEFAULT -> return true // Il tema base è sempre posseduto
            AppTheme.FANTASY -> "theme_fantasy" // Tema bacheca fantasy sbloccabile
            AppTheme.ARCADE -> "theme_arcade"
            AppTheme.REGALE -> "reward_tema_regale"
        }
        return themeId in ownedItems
    }

    fun getThemeDisplayName(theme: AppTheme): String {
        return when (theme) {
            AppTheme.DEFAULT -> "Classico"
            AppTheme.FANTASY -> "Bacheca Fantasy"
            AppTheme.ARCADE -> "Arcade"
            AppTheme.REGALE -> "Regale"
        }
    }
}

enum class AppTheme {
    DEFAULT,
    FANTASY,
    ARCADE,
    REGALE
}