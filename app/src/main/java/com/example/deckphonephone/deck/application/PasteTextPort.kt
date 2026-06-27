package com.example.deckphonephone.deck.application

interface PasteTextPort {
    suspend fun pasteText(text: String): PasteTextResult
}