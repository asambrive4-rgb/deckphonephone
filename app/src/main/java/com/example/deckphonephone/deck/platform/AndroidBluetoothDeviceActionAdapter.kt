package com.example.deckphonephone.deck.platform

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.deckphonephone.deck.application.BluetoothDeviceActionPort
import com.example.deckphonephone.deck.application.BluetoothDeviceActionResult

class AndroidBluetoothDeviceActionAdapter(
    private val context: Context,
) : BluetoothDeviceActionPort {
    override suspend fun startBluetoothDeviceAction(
        deviceName: String,
        deviceAddress: String,
    ): BluetoothDeviceActionResult {
        val request = BluetoothSettingsAutomationRequest(
            deviceName = deviceName,
            deviceAddress = deviceAddress,
        )

        return runCatching {
            if (!isAccessibilityServiceEnabled()) {
                return@runCatching if (openSettings(Settings.ACTION_ACCESSIBILITY_SETTINGS)) {
                    BluetoothDeviceActionResult.AccessibilityPermissionRequired
                } else {
                    BluetoothDeviceActionResult.SettingsOpenFailed
                }
            }

            if (!BluetoothSettingsAutomationCoordinator.tryStart(request)) {
                return@runCatching BluetoothDeviceActionResult.AlreadyRunning
            }

            if (!openSettings(Settings.ACTION_BLUETOOTH_SETTINGS)) {
                BluetoothSettingsAutomationCoordinator.clearIfCurrent(request)
                return@runCatching BluetoothDeviceActionResult.SettingsOpenFailed
            }

            BluetoothDeviceActionResult.Started
        }.getOrElse {
            BluetoothSettingsAutomationCoordinator.clearIfCurrent(request)
            BluetoothDeviceActionResult.Failure
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val isAccessibilityEnabled = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED,
            0,
        ) == 1
        if (!isAccessibilityEnabled) return false

        val expectedComponent = ComponentName(
            context,
            DeckBluetoothAccessibilityService::class.java,
        )
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ).orEmpty()

        return enabledServices
            .split(':')
            .mapNotNull(ComponentName::unflattenFromString)
            .any { component ->
                component.packageName == expectedComponent.packageName &&
                    component.className == expectedComponent.className
            }
    }

    private fun openSettings(action: String): Boolean {
        val intent = Intent(action).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return runCatching {
            context.startActivity(intent)
        }.isSuccess
    }
}