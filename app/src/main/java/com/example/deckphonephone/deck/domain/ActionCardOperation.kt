package com.example.deckphonephone.deck.domain

sealed interface ActionCardOperation {
    data class CopyText(val text: String) : ActionCardOperation
    data class OpenUrl(val url: String) : ActionCardOperation
    data class BluetoothDevice(
        val deviceName: String,
        val deviceAddress: String,
    ) : ActionCardOperation
}