package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction

class ExecuteCardUseCase(
    private val openUrlPort: OpenUrlPort,
    private val copyTextPort: CopyTextPort,
) {
    suspend operator fun invoke(card: ActionCard): ExecuteCardResult {
        if (!card.isEnabled) {
            return ExecuteCardResult.DisabledCard
        }

        return when (val action = card.action) {
            is CardAction.OpenUrl -> openUrl(action.url)
            is CardAction.CopyText -> copyText(action.text)
        }
    }

    private suspend fun openUrl(url: String): ExecuteCardResult {
        if (url.isBlank()) {
            return ExecuteCardResult.OpenUrlFailed
        }

        return when (openUrlPort.openUrl(url)) {
            OpenUrlResult.Success -> ExecuteCardResult.OpenedUrl
            OpenUrlResult.Failure -> ExecuteCardResult.OpenUrlFailed
        }
    }

    private suspend fun copyText(text: String): ExecuteCardResult {
        if (text.isBlank()) {
            return ExecuteCardResult.CopyTextBlank
        }

        return when (copyTextPort.copyText(text)) {
            CopyTextResult.Success -> ExecuteCardResult.CopiedText
            CopyTextResult.Failure -> ExecuteCardResult.CopyTextFailed
        }
    }
}
