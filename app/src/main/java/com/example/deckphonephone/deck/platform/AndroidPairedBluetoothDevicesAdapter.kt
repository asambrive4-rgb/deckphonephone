package com.example.deckphonephone.deck.platform

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.deckphonephone.deck.application.PairedBluetoothDevice
import com.example.deckphonephone.deck.application.PairedBluetoothDevicesPort
import com.example.deckphonephone.deck.application.PairedBluetoothDevicesResult

class AndroidPairedBluetoothDevicesAdapter(
    private val context: Context,
) : PairedBluetoothDevicesPort {
    @SuppressLint("MissingPermission")
    override suspend fun listPairedBluetoothDevices(): PairedBluetoothDevicesResult {
        if (!hasBluetoothConnectPermission()) {
            return PairedBluetoothDevicesResult.PermissionDenied
        }

        val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter
            ?: return PairedBluetoothDevicesResult.BluetoothUnavailable

        return runCatching {
            val devices = adapter.bondedDevices.orEmpty()
                .mapNotNull { device ->
                    val address = device.address ?: return@mapNotNull null
                    PairedBluetoothDevice(
                        name = device.name.orEmpty().ifBlank { "이름 없는 기기" },
                        address = address,
                    )
                }
                .sortedWith(compareBy({ it.name.lowercase() }, { it.address }))
            PairedBluetoothDevicesResult.Success(devices)
        }.getOrElse { error ->
            if (error is SecurityException) {
                PairedBluetoothDevicesResult.PermissionDenied
            } else {
                PairedBluetoothDevicesResult.Failure
            }
        }
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT,
            ) == PackageManager.PERMISSION_GRANTED
    }
}