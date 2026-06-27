package com.example.deckphonephone.deck.application

interface CopyTextPort {
    suspend fun copyText(text: String): CopyTextResult
}