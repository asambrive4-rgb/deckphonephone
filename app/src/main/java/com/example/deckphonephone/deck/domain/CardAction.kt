package com.example.deckphonephone.deck.domain

sealed interface CardAction {
    data class TextPaste(val text: String) : CardAction
    data class OpenUrl(val url: String) : CardAction
}
