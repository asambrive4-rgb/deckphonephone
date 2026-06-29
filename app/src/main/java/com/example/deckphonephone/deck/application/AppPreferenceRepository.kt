package com.example.deckphonephone.deck.application

import kotlinx.coroutines.flow.StateFlow

interface AppPreferenceRepository {
    val isDarkTheme: StateFlow<Boolean>
    val colorTheme: StateFlow<DeckColorTheme>
    val overlayHandPreference: StateFlow<OverlayHandPreference>

    suspend fun setDarkTheme(isDarkTheme: Boolean)

    suspend fun setColorTheme(colorTheme: DeckColorTheme)

    suspend fun setOverlayHandPreference(overlayHandPreference: OverlayHandPreference)
}
