package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation
import com.example.deckphonephone.deck.domain.UrlNormalizationResult
import com.example.deckphonephone.deck.domain.UrlNormalizer

class CreateWebActionCardUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
        title: String,
        rawUrl: String,
    ): DeckResult<ActionCard> {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            return DeckResult.Failure(DeckError.ActionCardTitleBlank)
        }
        if (rawUrl.isBlank()) {
            return DeckResult.Failure(DeckError.UrlBlank)
        }

        val normalizedUrl = when (val result = UrlNormalizer.normalize(rawUrl)) {
            is UrlNormalizationResult.Success -> result.url
            UrlNormalizationResult.Invalid -> return DeckResult.Failure(DeckError.InvalidUrl)
        }

        val card = repository.createActionCard(
            categoryId = categoryId,
            title = trimmedTitle,
            operation = ActionCardOperation.OpenUrl(normalizedUrl),
        )
        return DeckResult.Success(card)
    }
}
