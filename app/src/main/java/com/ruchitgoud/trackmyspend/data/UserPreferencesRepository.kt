package com.ruchitgoud.trackmyspend.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemePreference {
    SYSTEM, LIGHT, DARK
}

class UserPreferencesRepository(private val context: Context) {

    private val themeKey = stringPreferencesKey("theme_preference")

    val themePreference: Flow<ThemePreference> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[themeKey] ?: ThemePreference.SYSTEM.name
            ThemePreference.valueOf(themeName)
        }

    suspend fun setThemePreference(theme: ThemePreference) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = theme.name
        }
    }
}
