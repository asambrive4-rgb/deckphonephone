package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation

class ExecuteActionCardUseCase(
    private val openUrlPort: OpenUrlPort,
    private val copyTextPort: CopyTextPort,
    private val bluetoothDeviceActionPort: BluetoothDeviceActionPort,
) {
    suspend operator fun invoke(card: ActionCard): ExecuteActionCardResult {
        if (!card.isEnabled) {
            return ExecuteActionCardResult.DisabledActionCard
        }

        return when (val operation = card.operation) {
            is ActionCardOperation.OpenUrl -> openUrl(operation.url)
            is ActionCardOperation.CopyText -> copyText(operation.text)
            is ActionCardOperation.BluetoothDevice -> startBluetoothDeviceAction(operation)
        }
    }

    private suspend fun openUrl(url: String): ExecuteActionCardResult {
        if (url.isBlank()) {
            return ExecuteActionCardResult.OpenUrlFailed
        }

        return when (openUrlPort.openUrl(url)) {
            OpenUrlResult.Success -> ExecuteActionCardResult.OpenedUrl
            OpenUrlResult.Failure -> ExecuteActionCardResult.OpenUrlFailed
        }
    }

    private suspend fun copyText(text: String): ExecuteActionCardResult {
        if (text.isBlank()) {
            return ExecuteActionCardResult.CopyTextBlank
        }

        return when (copyTextPort.copyText(text)) {
            CopyTextResult.Success -> ExecuteActionCardResult.CopiedText
            CopyTextResult.Failure -> ExecuteActionCardResult.CopyTextFailed
        }
    }

    private suspend fun startBluetoothDeviceAction(
        operation: ActionCardOperation.BluetoothDevice,
    ): ExecuteActionCardResult {
        val deviceName = operation.deviceName.ifBlank { DEFAULT_BLUETOOTH_DEVICE_NAME }
        if (operation.deviceAddress.isBlank()) {
            return ExecuteActionCardResult.BluetoothDeviceAddressBlank
        }

        return when (
            bluetoothDeviceActionPort.startBluetoothDeviceAction(
                deviceName = deviceName,
                deviceAddress = operation.deviceAddress,
            )
        ) {
            BluetoothDeviceActionResult.Started -> {
                ExecuteActionCardResult.BluetoothAutomationStarted(deviceName)
            }

            BluetoothDeviceActionResult.AccessibilityPermissionRequired -> {
                ExecuteActionCardResult.BluetoothAccessibilityPermissionRequired(deviceName)
            }

            BluetoothDeviceActionResult.SettingsOpenFailed -> {
                ExecuteActionCardResult.BluetoothSettingsOpenFailed
            }

            BluetoothDeviceActionResult.AlreadyRunning -> {
                ExecuteActionCardResult.BluetoothAutomationAlreadyRunning
            }

            BluetoothDeviceActionResult.Failure -> {
                ExecuteActionCardResult.BluetoothAutomationFailed
            }
        }
    }
}
