package com.example.deckphonephone.deck.application

class ObserveCardsUseCase(
    private val repository: DeckRepository,
) {
    operator fun invoke(categoryId: Long) = repository.observeCards(categoryId)
}
