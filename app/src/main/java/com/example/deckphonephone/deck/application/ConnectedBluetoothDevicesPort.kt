package com.example.deckphonephone.deck.application

import kotlinx.coroutines.flow.Flow

data class ConnectedBluetoothDevice(
    val name: String,
    val address: String,
)

interface ConnectedBluetoothDevicesPort {
    fun observeConnectedBluetoothDevices(): Flow<List<ConnectedBluetoothDevice>>
}
