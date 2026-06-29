package com.example.deckphonephone.deck.application

class DeleteActionCardUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(actionCardId: Long) {
        repository.deleteActionCard(actionCardId)
    }
}