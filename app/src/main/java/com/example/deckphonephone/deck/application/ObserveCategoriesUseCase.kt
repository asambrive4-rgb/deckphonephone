package com.example.deckphonephone.deck.application

class ObserveCategoriesUseCase(
    private val repository: DeckRepository,
) {
    operator fun invoke() = repository.observeCategories()
}
