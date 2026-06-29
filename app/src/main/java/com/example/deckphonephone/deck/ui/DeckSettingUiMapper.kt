package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.DeckError
import com.example.deckphonephone.deck.application.PairedBluetoothDevice
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation

internal fun ActionCard.toActionCardEditState(): ActionCardEditState {
    return ActionCardEditState(
        actionCardId = id,
        title = title,
        payload = operation.toCardPayload(),
        selectedActionCardType = operation.toActionCardType(),
        selectedBluetoothDevice = operation.toPairedBluetoothDevice(),
        isEnabled = isEnabled,
    )
}

internal fun DeckError.toDeckSettingMessage(): String {
    return when (this) {
        DeckError.CategoryNameBlank -> "카테고리 이름을 입력해 주세요."
        DeckError.ActionCardTitleBlank -> "슬롯 이름을 입력해 주세요."
        DeckError.TextBlank -> "복사할 문구를 입력해 주세요."
        DeckError.UrlBlank -> "열 웹페이지 주소를 입력해 주세요."
        DeckError.InvalidUrl -> "웹 주소 형식이 올바르지 않습니다."
        DeckError.CategoryNotSelected -> "카테고리를 먼저 선택해 주세요."
        DeckError.BluetoothDeviceNotSelected -> "블루투스 기기를 선택해 주세요."
        DeckError.BluetoothDeviceAddressBlank -> "블루투스 기기 주소를 찾지 못했습니다."
    }
}

private fun ActionCardOperation.toCardPayload(): String {
    return when (this) {
        is ActionCardOperation.CopyText -> text
        is ActionCardOperation.OpenUrl -> url
        is ActionCardOperation.BluetoothDevice -> deviceAddress
    }
}

private fun ActionCardOperation.toActionCardType(): ActionCardType {
    return when (this) {
        is ActionCardOperation.CopyText -> ActionCardType.Text
        is ActionCardOperation.OpenUrl -> ActionCardType.Web
        is ActionCardOperation.BluetoothDevice -> ActionCardType.Bluetooth
    }
}

private fun ActionCardOperation.toPairedBluetoothDevice(): PairedBluetoothDevice? {
    return when (this) {
        is ActionCardOperation.BluetoothDevice -> PairedBluetoothDevice(
            name = deviceName,
            address = deviceAddress,
        )

        is ActionCardOperation.CopyText,
        is ActionCardOperation.OpenUrl -> null
    }
}
