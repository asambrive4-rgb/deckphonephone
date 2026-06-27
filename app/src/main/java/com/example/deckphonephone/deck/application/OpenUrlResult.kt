package com.example.deckphonephone.deck.application

sealed interface OpenUrlResult {
    data object Success : OpenUrlResult
    data object Failure : OpenUrlResult
}