package com.example.deckphonephone.deck.application

import kotlinx.coroutines.flow.StateFlow

interface ThemePreferenceRepository {
    val isDarkTheme: StateFlow<Boolean>

    suspend fun setDarkTheme(isDarkTheme: Boolean)
}
