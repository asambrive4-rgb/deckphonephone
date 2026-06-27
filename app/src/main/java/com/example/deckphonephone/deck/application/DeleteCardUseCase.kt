package com.example.deckphonephone.deck.application

class DeleteCardUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(cardId: Long) {
        repository.deleteCard(cardId)
    }
}