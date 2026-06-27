package com.example.deckphonephone.deck.application

sealed interface PairedBluetoothDevicesResult {
    data class Success(val devices: List<PairedBluetoothDevice>) : PairedBluetoothDevicesResult
    data object PermissionDenied : PairedBluetoothDevicesResult
    data object BluetoothUnavailable : PairedBluetoothDevicesResult
    data object Failure : PairedBluetoothDevicesResult
}