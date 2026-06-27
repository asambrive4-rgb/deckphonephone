package com.example.deckphonephone.deck.application

class ListPairedBluetoothDevicesUseCase(
    private val pairedBluetoothDevicesPort: PairedBluetoothDevicesPort,
) {
    suspend operator fun invoke(): PairedBluetoothDevicesResult {
        return pairedBluetoothDevicesPort.listPairedBluetoothDevices()
    }
}