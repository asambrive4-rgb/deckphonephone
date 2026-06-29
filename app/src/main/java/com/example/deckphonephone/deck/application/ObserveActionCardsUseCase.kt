package com.example.deckphonephone.deck.application

class ObserveActionCardsUseCase(
    private val repository: DeckRepository,
) {
    operator fun invoke(categoryId: Long) = repository.observeActionCards(categoryId)
}
