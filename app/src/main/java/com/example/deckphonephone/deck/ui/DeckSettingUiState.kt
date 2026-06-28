package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.OverlayHandPreference
import com.example.deckphonephone.deck.application.PairedBluetoothDevice
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.DeckCategory

data class DeckSettingUiState(
    val categories: List<DeckCategory> = emptyList(),
    val isCategoriesLoading: Boolean = true,
    val selectedCategoryId: Long? = null,
    val cards: List<ActionCard> = emptyList(),
    val isCardsLoading: Boolean = false,
    val categoryNameInput: String = "",
    val isCreatingCategory: Boolean = false,
    val cardTitleInput: String = "",
    val cardPayloadInput: String = "",
    val selectedCardType: CardType = CardType.Text,
    val selectedBluetoothDevice: PairedBluetoothDevice? = null,
    val pairedBluetoothDevices: List<PairedBluetoothDevice> = emptyList(),
    val isBluetoothDevicesLoading: Boolean = false,
    val isCreatingCard: Boolean = false,
    val isDarkTheme: Boolean = false,
    val overlayHandPreference: OverlayHandPreference = OverlayHandPreference.Right,
    val isAppSettingsOpen: Boolean = false,
    val editingCategory: CategoryEditState? = null,
    val editingCard: CardEditState? = null,
    val deleteTarget: DeleteTarget? = null,
    val message: String? = null,
)

data class CategoryEditState(
    val categoryId: Long,
    val name: String,
)

data class CardEditState(
    val cardId: Long,
    val title: String,
    val payload: String,
    val selectedCardType: CardType,
    val selectedBluetoothDevice: PairedBluetoothDevice?,
    val isEnabled: Boolean,
)

enum class CardType {
    Text,
    Web,
    Bluetooth,
}

sealed interface DeleteTarget {
    data class Category(val category: DeckCategory) : DeleteTarget
    data class Card(val card: ActionCard) : DeleteTarget
}
