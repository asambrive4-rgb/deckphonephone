package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.DeckCategory

data class DeckUiState(
    val categories: List<DeckCategory> = emptyList(),
    val selectedCategoryId: Long? = null,
    val cards: List<ActionCard> = emptyList(),
    val categoryNameInput: String = "",
    val cardTitleInput: String = "",
    val cardPayloadInput: String = "",
    val selectedCardType: CardType = CardType.Text,
    val message: String? = null,
)

enum class CardType {
    Text,
    Web,
}
