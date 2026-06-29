package com.example.deckphonephone.deck.application

class SetColorThemeUseCase(
    private val appPreferenceRepository: AppPreferenceRepository,
) {
    suspend operator fun invoke(colorTheme: DeckColorTheme) {
        appPreferenceRepository.setColorTheme(colorTheme)
    }
}
