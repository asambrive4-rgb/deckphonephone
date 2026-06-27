package com.example.deckphonephone.deck.application

class SetDarkThemeUseCase(
    private val themePreferenceRepository: ThemePreferenceRepository,
) {
    suspend operator fun invoke(isDarkTheme: Boolean) {
        themePreferenceRepository.setDarkTheme(isDarkTheme)
    }
}
