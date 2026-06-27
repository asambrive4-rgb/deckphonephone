package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.DeckCategory

data class DeckOverlayUiState(
    val categories: List<DeckCategory> = emptyList(),
    val isCategoriesLoading: Boolean = true,
    val selectedCategoryId: Long? = null,
    val cards: List<ActionCard> = emptyList(),
    val isCardsLoading: Boolean = false,
    val message: String? = null,
)