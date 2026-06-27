package com.example.deckphonephone.deck.domain

sealed interface CardAction {
    data class CopyText(val text: String) : CardAction
    data class OpenUrl(val url: String) : CardAction
    data class BluetoothDevice(
        val deviceName: String,
        val deviceAddress: String,
    ) : CardAction
}