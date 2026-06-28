package com.example.deckphonephone.deck.application

import kotlinx.coroutines.flow.StateFlow

interface AppPreferenceRepository {
    val isDarkTheme: StateFlow<Boolean>
    val overlayHandPreference: StateFlow<OverlayHandPreference>

    suspend fun setDarkTheme(isDarkTheme: Boolean)

    suspend fun setOverlayHandPreference(overlayHandPreference: OverlayHandPreference)
}
