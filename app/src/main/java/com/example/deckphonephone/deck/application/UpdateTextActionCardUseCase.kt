package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation

class UpdateTextActionCardUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(
        card: ActionCard,
        title: String,
        text: String,
    ): DeckResult<ActionCard> {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            return DeckResult.Failure(DeckError.ActionCardTitleBlank)
        }
        if (text.isBlank()) {
            return DeckResult.Failure(DeckError.TextBlank)
        }

        return DeckResult.Success(
            repository.updateActionCard(
                card.copy(
                    title = trimmedTitle,
                    operation = ActionCardOperation.CopyText(text),
                ),
            ),
        )
    }
}