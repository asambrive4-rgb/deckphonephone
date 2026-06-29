package com.example.deckphonephone.deck.application

sealed interface ExecuteActionCardResult {
    data object OpenedUrl : ExecuteActionCardResult
    data object CopiedText : ExecuteActionCardResult
    data object DisabledActionCard : ExecuteActionCardResult
    data object CopyTextBlank : ExecuteActionCardResult
    data object OpenUrlFailed : ExecuteActionCardResult
    data object CopyTextFailed : ExecuteActionCardResult
    data class BluetoothAutomationStarted(val deviceName: String) : ExecuteActionCardResult
    data class BluetoothAccessibilityPermissionRequired(val deviceName: String) : ExecuteActionCardResult
    data object BluetoothSettingsOpenFailed : ExecuteActionCardResult
    data object BluetoothAutomationAlreadyRunning : ExecuteActionCardResult
    data object BluetoothDeviceAddressBlank : ExecuteActionCardResult
    data object BluetoothAutomationFailed : ExecuteActionCardResult
}
