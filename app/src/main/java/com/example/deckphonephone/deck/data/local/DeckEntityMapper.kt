package com.example.deckphonephone.deck.data.local

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation
import com.example.deckphonephone.deck.domain.DeckCategory

internal const val OPERATION_COPY_TEXT = "copy_text"
private const val OPERATION_TEXT_PASTE = "text_paste"
internal const val OPERATION_OPEN_URL = "open_url"
internal const val OPERATION_BLUETOOTH_DEVICE = "bluetooth_device"

fun CategoryEntity.toDomain() = DeckCategory(
    id = id,
    name = name,
    description = description,
    isEnabled = isEnabled,
)

fun ActionCardEntity.toDomain() = ActionCard(
    id = id,
    categoryId = categoryId,
    title = title,
    description = description,
    operation = when (operationType) {
        OPERATION_COPY_TEXT,
        OPERATION_TEXT_PASTE -> ActionCardOperation.CopyText(textValue.orEmpty())

        OPERATION_OPEN_URL -> ActionCardOperation.OpenUrl(urlValue.orEmpty())

        OPERATION_BLUETOOTH_DEVICE -> ActionCardOperation.BluetoothDevice(
            deviceName = bluetoothDeviceName.orEmpty(),
            deviceAddress = bluetoothDeviceAddress.orEmpty(),
        )

        else -> error("Unknown card operation type: $operationType")
    },
    isEnabled = isEnabled,
)

fun newCategoryEntity(
    name: String,
    description: String,
    isEnabled: Boolean,
) = CategoryEntity(
    name = name,
    description = description,
    isEnabled = isEnabled,
)

fun newActionCardEntity(
    categoryId: Long,
    title: String,
    description: String,
    operation: ActionCardOperation,
    isEnabled: Boolean,
) = when (operation) {
    is ActionCardOperation.CopyText -> ActionCardEntity(
        categoryId = categoryId,
        title = title,
        description = description,
        operationType = OPERATION_COPY_TEXT,
        textValue = operation.text,
        isEnabled = isEnabled,
    )

    is ActionCardOperation.OpenUrl -> ActionCardEntity(
        categoryId = categoryId,
        title = title,
        description = description,
        operationType = OPERATION_OPEN_URL,
        urlValue = operation.url,
        isEnabled = isEnabled,
    )

    is ActionCardOperation.BluetoothDevice -> ActionCardEntity(
        categoryId = categoryId,
        title = title,
        description = description,
        operationType = OPERATION_BLUETOOTH_DEVICE,
        bluetoothDeviceName = operation.deviceName,
        bluetoothDeviceAddress = operation.deviceAddress,
        isEnabled = isEnabled,
    )
}