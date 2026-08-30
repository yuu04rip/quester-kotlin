package com.example.quester.ui.theme

import android.content.Context
import com.example.quester.data.preferences.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ThemeManager {
    // 1. Stato reattivo per le Coroutine e StateFlow
    private val _currentTheme = MutableStateFlow(AppTheme.DEFAULT)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    // Riferimento thread-safe o istanza globale disaccoppiata dal contesto UI
    private var themePreferences: ThemePreferences? = null

    fun initPreferences(context: Context) {
        if (themePreferences == null) {
            // Usiamo sempre applicationContext per evitare qualsiasi memory leak di Activity/Fragment
            themePreferences = ThemePreferences(context.applicationContext)
        }
    }

    // 2. Proprietà diretta per il vecchio codice
    val theme: AppTheme
        get() = _currentTheme.value

    // 3. Funzione unica per aggiornare il tema ovunque e salvarlo su disco
    fun setTheme(theme: AppTheme, saveToPrefs: Boolean = true) {
        _currentTheme.value = theme
        if (saveToPrefs) {
            themePreferences?.let { prefs ->
                CoroutineScope(Dispatchers.IO).launch {
                    prefs.saveTheme(theme)
                }
            }
        }
    }

    fun isThemeOwned(theme: AppTheme, ownedItems: List<String>): Boolean {
        val themeId = when (theme) {
            AppTheme.DEFAULT -> return true // Il tema base è sempre posseduto
            AppTheme.FANTASY -> "theme_fantasy"
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