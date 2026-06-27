package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction

class UpdateTextCardUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(
        card: ActionCard,
        title: String,
        text: String,
    ): DeckResult<ActionCard> {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            return DeckResult.Failure(DeckError.CardTitleBlank)
        }
        if (text.isBlank()) {
            return DeckResult.Failure(DeckError.TextBlank)
        }

        return DeckResult.Success(
            repository.updateCard(
                card.copy(
                    title = trimmedTitle,
                    action = CardAction.CopyText(text),
                ),
            ),
        )
    }
}