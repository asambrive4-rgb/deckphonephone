package com.example.deckphonephone.deck.application

import kotlinx.coroutines.flow.Flow

class ObserveConnectedBluetoothDevicesUseCase(
    private val connectedBluetoothDevicesPort: ConnectedBluetoothDevicesPort,
) {
    operator fun invoke(): Flow<List<ConnectedBluetoothDevice>> {
        return connectedBluetoothDevicesPort.observeConnectedBluetoothDevices()
    }
}
