package com.example.deckphonephone.deck.application

import kotlinx.coroutines.flow.StateFlow

class ObserveColorThemeUseCase(
    private val appPreferenceRepository: AppPreferenceRepository,
) {
    operator fun invoke(): StateFlow<DeckColorTheme> {
        return appPreferenceRepository.colorTheme
    }
}
