package com.example.deckphonephone.deck.application

sealed interface CopyTextResult {
    data object Success : CopyTextResult
    data object Failure : CopyTextResult
}