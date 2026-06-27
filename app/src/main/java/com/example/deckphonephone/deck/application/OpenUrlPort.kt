package com.example.deckphonephone.deck.application

interface OpenUrlPort {
    suspend fun openUrl(url: String): OpenUrlResult
}