package com.example.deckphonephone.deck.application

sealed interface ExecuteCardResult {
    data object OpenedUrl : ExecuteCardResult
    data object CopiedText : ExecuteCardResult
    data object DisabledCard : ExecuteCardResult
    data object CopyTextBlank : ExecuteCardResult
    data object OpenUrlFailed : ExecuteCardResult
    data object CopyTextFailed : ExecuteCardResult
    data class BluetoothAutomationStarted(val deviceName: String) : ExecuteCardResult
    data class BluetoothAccessibilityPermissionRequired(val deviceName: String) : ExecuteCardResult
    data object BluetoothSettingsOpenFailed : ExecuteCardResult
    data object BluetoothAutomationAlreadyRunning : ExecuteCardResult
    data object BluetoothDeviceAddressBlank : ExecuteCardResult
    data object BluetoothAutomationFailed : ExecuteCardResult
}
