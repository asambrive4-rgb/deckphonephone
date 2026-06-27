package com.example.deckphonephone.deck.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.deckphonephone.deck.application.ThemePreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPreferencesThemePreferenceRepository(
    context: Context,
) : ThemePreferenceRepository {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )
    private val _isDarkTheme = MutableStateFlow(preferences.getBoolean(KEY_DARK_THEME, false))
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == KEY_DARK_THEME) {
            _isDarkTheme.value = preferences.getBoolean(KEY_DARK_THEME, false)
        }
    }

    override val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override suspend fun setDarkTheme(isDarkTheme: Boolean) {
        _isDarkTheme.value = isDarkTheme
        preferences.edit()
            .putBoolean(KEY_DARK_THEME, isDarkTheme)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "deck_preferences"
        const val KEY_DARK_THEME = "dark_theme"
    }
}
