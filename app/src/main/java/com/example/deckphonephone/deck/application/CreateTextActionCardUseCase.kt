package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation

class CreateTextActionCardUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
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

        val card = repository.createActionCard(
            categoryId = categoryId,
            title = trimmedTitle,
            operation = ActionCardOperation.CopyText(text),
        )
        return DeckResult.Success(card)
    }
}
