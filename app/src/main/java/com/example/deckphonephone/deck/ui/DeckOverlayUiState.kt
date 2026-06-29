package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.ConnectedBluetoothDevice
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.DeckCategory

data class DeckOverlayUiState(
    val categories: List<DeckCategory> = emptyList(),
    val isCategoriesLoading: Boolean = true,
    val selectedCategoryId: Long? = null,
    val actionCards: List<ActionCard> = emptyList(),
    val connectedBluetoothDevices: List<ConnectedBluetoothDevice> = emptyList(),
    val isActionCardsLoading: Boolean = false,
    val message: String? = null,
)
