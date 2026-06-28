package com.example.deckphonephone.deck.application

import kotlinx.coroutines.flow.StateFlow

class ObserveOverlayHandPreferenceUseCase(
    private val appPreferenceRepository: AppPreferenceRepository,
) {
    operator fun invoke(): StateFlow<OverlayHandPreference> {
        return appPreferenceRepository.overlayHandPreference
    }
}
