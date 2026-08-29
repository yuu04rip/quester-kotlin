package com.example.quester.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quester.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

// RIMUOVI "private" per renderlo accessibile
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferences(private val context: Context) {

    companion object {
        private val THEME_KEY = stringPreferencesKey("selected_theme")
    }

    // Salva il tema
    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    // Legge il tema salvato (Flow)
    fun getThemeFlow(): Flow<AppTheme> {
        return context.dataStore.data.map { preferences ->
            val themeName = preferences[THEME_KEY] ?: AppTheme.FANTASY.name
            try {
                AppTheme.valueOf(themeName)
            } catch (_: IllegalArgumentException) {
                AppTheme.FANTASY
            }
        }
    }

    // Lettura singola (sospesa)
    suspend fun getTheme(): AppTheme {
        val preferences = context.dataStore.data.first()
        val themeName = preferences[THEME_KEY] ?: AppTheme.FANTASY.name
        return try {
            AppTheme.valueOf(themeName)
        } catch (_: IllegalArgumentException) {
            AppTheme.FANTASY
        }
    }
}