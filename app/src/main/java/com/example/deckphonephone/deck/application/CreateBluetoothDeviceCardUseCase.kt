package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction

class CreateBluetoothDeviceCardUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
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

        val card = repository.createCard(
            categoryId = categoryId,
            title = trimmedTitle,
            action = CardAction.BluetoothDevice(
                deviceName = device.name.ifBlank { DEFAULT_BLUETOOTH_DEVICE_NAME },
                deviceAddress = device.address,
            ),
        )
        return DeckResult.Success(card)
    }
}

internal const val DEFAULT_BLUETOOTH_DEVICE_NAME = "이름 없는 기기"