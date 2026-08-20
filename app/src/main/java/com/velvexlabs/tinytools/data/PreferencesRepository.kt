package com.velvexlabs.tinytools.data

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tinyToolsDataStore by preferencesDataStore(name = "tiny_tools_preferences")

enum class ThemeChoice { SYSTEM, LIGHT, DARK }

class PreferencesRepository(private val context: Context) {
    private val themeKey = stringPreferencesKey("theme_choice")

    val theme: Flow<ThemeChoice> = context.tinyToolsDataStore.data.map { preferences ->
        runCatching { ThemeChoice.valueOf(preferences[themeKey] ?: ThemeChoice.SYSTEM.name) }
            .getOrDefault(ThemeChoice.SYSTEM)
    }

    suspend fun setTheme(choice: ThemeChoice) {
        context.tinyToolsDataStore.updateData { current ->
            current.toMutablePreferences().apply { this[themeKey] = choice.name }
        }
    }
}
