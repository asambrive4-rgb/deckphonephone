package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction

class CreateTextCardUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
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

        val card = repository.createCard(
            categoryId = categoryId,
            title = trimmedTitle,
            action = CardAction.TextPaste(text),
        )
        return DeckResult.Success(card)
    }
}
