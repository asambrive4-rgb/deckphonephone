package com.example.deckphonephone.deck.application

class SetOverlayHandPreferenceUseCase(
    private val appPreferenceRepository: AppPreferenceRepository,
) {
    suspend operator fun invoke(overlayHandPreference: OverlayHandPreference) {
        appPreferenceRepository.setOverlayHandPreference(overlayHandPreference)
    }
}
