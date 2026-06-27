package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.DeckCategory

data class DeckOverlayUiState(
    val categories: List<DeckCategory> = emptyList(),
    val selectedCategoryId: Long? = null,
    val cards: List<ActionCard> = emptyList(),
    val message: String? = null,
)