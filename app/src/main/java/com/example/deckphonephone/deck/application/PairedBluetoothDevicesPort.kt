package com.example.deckphonephone.deck.application

interface PairedBluetoothDevicesPort {
    suspend fun listPairedBluetoothDevices(): PairedBluetoothDevicesResult
}