package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation
import com.example.deckphonephone.deck.domain.UrlNormalizationResult
import com.example.deckphonephone.deck.domain.UrlNormalizer

class UpdateWebActionCardUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(
        card: ActionCard,
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

        return DeckResult.Success(
            repository.updateActionCard(
                card.copy(
                    title = trimmedTitle,
                    operation = ActionCardOperation.OpenUrl(normalizedUrl),
                ),
            ),
        )
    }
}