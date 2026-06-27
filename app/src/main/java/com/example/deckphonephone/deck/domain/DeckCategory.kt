package com.example.deckphonephone.deck.domain

data class DeckCategory(
    val id: Long,
    val name: String,
    val description: String = "",
    val isEnabled: Boolean = true,
)
