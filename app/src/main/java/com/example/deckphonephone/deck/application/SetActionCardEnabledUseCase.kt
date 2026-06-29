package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard

class SetActionCardEnabledUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(
        card: ActionCard,
        isEnabled: Boolean,
    ): ActionCard {
        return repository.updateActionCard(card.copy(isEnabled = isEnabled))
    }
}