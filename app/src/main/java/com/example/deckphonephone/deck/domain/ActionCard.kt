package com.example.deckphonephone.deck.domain

data class ActionCard(
    val id: Long,
    val categoryId: Long,
    val title: String,
    val description: String = "",
    val action: CardAction,
    val isEnabled: Boolean = true,
)
