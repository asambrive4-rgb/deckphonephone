package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import com.example.deckphonephone.deck.domain.UrlNormalizationResult
import com.example.deckphonephone.deck.domain.UrlNormalizer

class CreateWebCardUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
        title: String,
        rawUrl: String,
    ): DeckResult<ActionCard> {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            return DeckResult.Failure(DeckError.CardTitleBlank)
        }
        if (rawUrl.isBlank()) {
            return DeckResult.Failure(DeckError.UrlBlank)
        }

        val normalizedUrl = when (val result = UrlNormalizer.normalize(rawUrl)) {
            is UrlNormalizationResult.Success -> result.url
            UrlNormalizationResult.Invalid -> return DeckResult.Failure(DeckError.InvalidUrl)
        }

        val card = repository.createCard(
            categoryId = categoryId,
            title = trimmedTitle,
            action = CardAction.OpenUrl(normalizedUrl),
        )
        return DeckResult.Success(card)
    }
}
