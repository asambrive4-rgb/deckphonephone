package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.DeckError
import com.example.deckphonephone.deck.application.PairedBluetoothDevice
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction

internal fun ActionCard.toCardEditState(): CardEditState {
    return CardEditState(
        cardId = id,
        title = title,
        payload = action.toCardPayload(),
        selectedCardType = action.toCardType(),
        selectedBluetoothDevice = action.toPairedBluetoothDevice(),
        isEnabled = isEnabled,
    )
}

internal fun DeckError.toDeckSettingMessage(): String {
    return when (this) {
        DeckError.CategoryNameBlank -> "카테고리 이름을 입력해 주세요."
        DeckError.CardTitleBlank -> "슬롯 이름을 입력해 주세요."
        DeckError.TextBlank -> "복사할 문구를 입력해 주세요."
        DeckError.UrlBlank -> "열 웹페이지 주소를 입력해 주세요."
        DeckError.InvalidUrl -> "웹 주소 형식이 올바르지 않습니다."
        DeckError.CategoryNotSelected -> "카테고리를 먼저 선택해 주세요."
        DeckError.BluetoothDeviceNotSelected -> "블루투스 기기를 선택해 주세요."
        DeckError.BluetoothDeviceAddressBlank -> "블루투스 기기 주소를 찾지 못했습니다."
    }
}

private fun CardAction.toCardPayload(): String {
    return when (this) {
        is CardAction.CopyText -> text
        is CardAction.OpenUrl -> url
        is CardAction.BluetoothDevice -> deviceAddress
    }
}

private fun CardAction.toCardType(): CardType {
    return when (this) {
        is CardAction.CopyText -> CardType.Text
        is CardAction.OpenUrl -> CardType.Web
        is CardAction.BluetoothDevice -> CardType.Bluetooth
    }
}

private fun CardAction.toPairedBluetoothDevice(): PairedBluetoothDevice? {
    return when (this) {
        is CardAction.BluetoothDevice -> PairedBluetoothDevice(
            name = deviceName,
            address = deviceAddress,
        )

        is CardAction.CopyText,
        is CardAction.OpenUrl -> null
    }
}
