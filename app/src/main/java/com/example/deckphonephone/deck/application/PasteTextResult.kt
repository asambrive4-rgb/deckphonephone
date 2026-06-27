package com.example.deckphonephone.deck.application

sealed interface PasteTextResult {
    data object Success : PasteTextResult
    data object Deferred : PasteTextResult
    data object Failure : PasteTextResult
}