package com.example.deckphonephone.deck.application

interface BluetoothDeviceActionPort {
    suspend fun startBluetoothDeviceAction(
        deviceName: String,
        deviceAddress: String,
    ): BluetoothDeviceActionResult
}

sealed interface BluetoothDeviceActionResult {
    data object Started : BluetoothDeviceActionResult
    data object AccessibilityPermissionRequired : BluetoothDeviceActionResult
    data object SettingsOpenFailed : BluetoothDeviceActionResult
    data object AlreadyRunning : BluetoothDeviceActionResult
    data object Failure : BluetoothDeviceActionResult
}
