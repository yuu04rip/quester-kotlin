package com.example.quester.data.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {

    private object Keys {
        val LOGGED_USER_ID = longPreferencesKey("logged_user_id")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val isLoggedIn: Flow<Boolean> =
        context.sessionDataStore.data.map { prefs ->
            prefs[Keys.IS_LOGGED_IN] ?: false
        }

    val loggedUserId: Flow<Long?> =
        context.sessionDataStore.data.map { prefs ->
            prefs[Keys.LOGGED_USER_ID]
        }

    suspend fun createSession(userId: Long) {
        context.sessionDataStore.edit { prefs ->
            // Puliamo eventuali rimasugli prima di scrivere la nuova sessione
            prefs.clear()
            prefs[Keys.LOGGED_USER_ID] = userId
            prefs[Keys.IS_LOGGED_IN] = true
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs ->
            // Svuota completamente DataStore
            prefs.clear()
        }
    }
}