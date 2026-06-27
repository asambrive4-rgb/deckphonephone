package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction

class UpdateBluetoothDeviceCardUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(
        card: ActionCard,
        title: String,
        device: PairedBluetoothDevice?,
    ): DeckResult<ActionCard> {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            return DeckResult.Failure(DeckError.CardTitleBlank)
        }
        if (device == null) {
            return DeckResult.Failure(DeckError.BluetoothDeviceNotSelected)
        }
        if (device.address.isBlank()) {
            return DeckResult.Failure(DeckError.BluetoothDeviceAddressBlank)
        }

        return DeckResult.Success(
            repository.updateCard(
                card.copy(
                    title = trimmedTitle,
                    action = CardAction.BluetoothDevice(
                        deviceName = device.name.ifBlank { DEFAULT_BLUETOOTH_DEVICE_NAME },
                        deviceAddress = device.address,
                    ),
                ),
            ),
        )
    }
}