package com.example.deckphonephone.deck.application

import kotlinx.coroutines.flow.StateFlow

class ObserveDarkThemeUseCase(
    private val themePreferenceRepository: ThemePreferenceRepository,
) {
    operator fun invoke(): StateFlow<Boolean> {
        return themePreferenceRepository.isDarkTheme
    }
}
