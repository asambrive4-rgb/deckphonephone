package com.example.deckphonephone.deck.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.deckphonephone.deck.application.AppPreferenceRepository
import com.example.deckphonephone.deck.application.DeckColorTheme
import com.example.deckphonephone.deck.application.OverlayHandPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPreferencesAppPreferenceRepository(
    context: Context,
) : AppPreferenceRepository {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )
    private val _isDarkTheme = MutableStateFlow(preferences.getBoolean(KEY_DARK_THEME, false))
    private val _colorTheme = MutableStateFlow(
        preferences.getString(KEY_COLOR_THEME, null).toDeckColorTheme(),
    )
    private val _overlayHandPreference = MutableStateFlow(
        preferences.getString(KEY_OVERLAY_HAND_PREFERENCE, null).toOverlayHandPreference(),
    )
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            KEY_DARK_THEME -> {
                _isDarkTheme.value = preferences.getBoolean(KEY_DARK_THEME, false)
            }

            KEY_COLOR_THEME -> {
                _colorTheme.value = preferences
                    .getString(KEY_COLOR_THEME, null)
                    .toDeckColorTheme()
            }

            KEY_OVERLAY_HAND_PREFERENCE -> {
                _overlayHandPreference.value = preferences
                    .getString(KEY_OVERLAY_HAND_PREFERENCE, null)
                    .toOverlayHandPreference()
            }
        }
    }

    override val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    override val colorTheme: StateFlow<DeckColorTheme> = _colorTheme.asStateFlow()
    override val overlayHandPreference: StateFlow<OverlayHandPreference> = _overlayHandPreference.asStateFlow()

    init {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override suspend fun setDarkTheme(isDarkTheme: Boolean) {
        _isDarkTheme.value = isDarkTheme
        preferences.edit()
            .putBoolean(KEY_DARK_THEME, isDarkTheme)
            .apply()
    }

    override suspend fun setColorTheme(colorTheme: DeckColorTheme) {
        _colorTheme.value = colorTheme
        preferences.edit()
            .putString(KEY_COLOR_THEME, colorTheme.toPreferenceValue())
            .apply()
    }

    override suspend fun setOverlayHandPreference(overlayHandPreference: OverlayHandPreference) {
        _overlayHandPreference.value = overlayHandPreference
        preferences.edit()
            .putString(KEY_OVERLAY_HAND_PREFERENCE, overlayHandPreference.toPreferenceValue())
            .apply()
    }

    private fun String?.toOverlayHandPreference(): OverlayHandPreference {
        return when (this) {
            VALUE_LEFT_HAND -> OverlayHandPreference.Left
            VALUE_RIGHT_HAND -> OverlayHandPreference.Right
            else -> OverlayHandPreference.Right
        }
    }

    private fun String?.toDeckColorTheme(): DeckColorTheme {
        return when (this) {
            VALUE_COLOR_APRICOT -> DeckColorTheme.Apricot
            VALUE_COLOR_MINT -> DeckColorTheme.Mint
            VALUE_COLOR_LAVENDER -> DeckColorTheme.Lavender
            VALUE_COLOR_SKY -> DeckColorTheme.Sky
            else -> DeckColorTheme.Sky
        }
    }

    private fun DeckColorTheme.toPreferenceValue(): String {
        return when (this) {
            DeckColorTheme.Sky -> VALUE_COLOR_SKY
            DeckColorTheme.Apricot -> VALUE_COLOR_APRICOT
            DeckColorTheme.Mint -> VALUE_COLOR_MINT
            DeckColorTheme.Lavender -> VALUE_COLOR_LAVENDER
        }
    }

    private fun OverlayHandPreference.toPreferenceValue(): String {
        return when (this) {
            OverlayHandPreference.Left -> VALUE_LEFT_HAND
            OverlayHandPreference.Right -> VALUE_RIGHT_HAND
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "deck_preferences"
        const val KEY_DARK_THEME = "dark_theme"
        const val KEY_COLOR_THEME = "color_theme"
        const val KEY_OVERLAY_HAND_PREFERENCE = "overlay_hand_preference"
        const val VALUE_COLOR_SKY = "sky"
        const val VALUE_COLOR_APRICOT = "apricot"
        const val VALUE_COLOR_MINT = "mint"
        const val VALUE_COLOR_LAVENDER = "lavender"
        const val VALUE_LEFT_HAND = "left"
        const val VALUE_RIGHT_HAND = "right"
    }
}
