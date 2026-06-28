package com.example.deckphonephone.deck.application

import kotlinx.coroutines.flow.StateFlow

class ObserveDarkThemeUseCase(
    private val appPreferenceRepository: AppPreferenceRepository,
) {
    operator fun invoke(): StateFlow<Boolean> {
        return appPreferenceRepository.isDarkTheme
    }
}
