package com.example.deckphonephone.deck.application

class SetDarkThemeUseCase(
    private val appPreferenceRepository: AppPreferenceRepository,
) {
    suspend operator fun invoke(isDarkTheme: Boolean) {
        appPreferenceRepository.setDarkTheme(isDarkTheme)
    }
}
