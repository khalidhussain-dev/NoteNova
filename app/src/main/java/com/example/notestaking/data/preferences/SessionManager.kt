package com.example.notestaking.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "note_nova_session")

class SessionManager(private val context: Context) {

    private val userIdKey = longPreferencesKey("user_id")
    private val userEmailKey = stringPreferencesKey("user_email")
    private val userNameKey = stringPreferencesKey("user_name")
    private val rememberLoginKey = booleanPreferencesKey("remember_login")

    val sessionFlow: Flow<SessionState> = context.dataStore.data.map { prefs ->
        val userId = prefs[userIdKey] ?: -1L
        if (userId > 0) {
            SessionState.LoggedIn(
                userId = userId,
                email = prefs[userEmailKey].orEmpty(),
                fullName = prefs[userNameKey].orEmpty()
            )
        } else {
            SessionState.LoggedOut
        }
    }

    suspend fun saveSession(userId: Long, email: String, fullName: String, remember: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[userIdKey] = userId
            prefs[userEmailKey] = email
            prefs[userNameKey] = fullName
            prefs[rememberLoginKey] = remember
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}

sealed class SessionState {
    data object LoggedOut : SessionState()
    data class LoggedIn(
        val userId: Long,
        val email: String,
        val fullName: String
    ) : SessionState()
}
