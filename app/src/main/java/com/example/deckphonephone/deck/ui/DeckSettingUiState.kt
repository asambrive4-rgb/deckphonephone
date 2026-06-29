package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.OverlayHandPreference
import com.example.deckphonephone.deck.application.ConnectedBluetoothDevice
import com.example.deckphonephone.deck.application.PairedBluetoothDevice
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.DeckCategory

data class DeckSettingUiState(
    val categories: List<DeckCategory> = emptyList(),
    val isCategoriesLoading: Boolean = true,
    val selectedCategoryId: Long? = null,
    val actionCards: List<ActionCard> = emptyList(),
    val isActionCardsLoading: Boolean = false,
    val categoryNameInput: String = "",
    val isCreatingCategory: Boolean = false,
    val actionCardTitleInput: String = "",
    val actionCardPayloadInput: String = "",
    val selectedActionCardType: ActionCardType = ActionCardType.Text,
    val selectedBluetoothDevice: PairedBluetoothDevice? = null,
    val pairedBluetoothDevices: List<PairedBluetoothDevice> = emptyList(),
    val connectedBluetoothDevices: List<ConnectedBluetoothDevice> = emptyList(),
    val isBluetoothDevicesLoading: Boolean = false,
    val isCreatingActionCard: Boolean = false,
    val isDarkTheme: Boolean = false,
    val overlayHandPreference: OverlayHandPreference = OverlayHandPreference.Right,
    val isAppSettingsOpen: Boolean = false,
    val editingCategory: CategoryEditState? = null,
    val editingActionCard: ActionCardEditState? = null,
    val deleteTarget: DeleteTarget? = null,
    val message: String? = null,
)

data class CategoryEditState(
    val categoryId: Long,
    val name: String,
)

data class ActionCardEditState(
    val actionCardId: Long,
    val title: String,
    val payload: String,
    val selectedActionCardType: ActionCardType,
    val selectedBluetoothDevice: PairedBluetoothDevice?,
    val isEnabled: Boolean,
)

enum class ActionCardType {
    Text,
    Web,
    Bluetooth,
}

sealed interface DeleteTarget {
    data class Category(val category: DeckCategory) : DeleteTarget
    data class ActionCard(
        val actionCard: com.example.deckphonephone.deck.domain.ActionCard,
    ) : DeleteTarget
}
