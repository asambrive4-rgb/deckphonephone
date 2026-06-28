package com.example.deckphonephone.deck.platform

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.deckphonephone.deck.application.ConnectedBluetoothDevice
import com.example.deckphonephone.deck.application.ConnectedBluetoothDevicesPort
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class AndroidConnectedBluetoothDevicesAdapter(
    private val context: Context,
) : ConnectedBluetoothDevicesPort {
    @SuppressLint("MissingPermission")
    override fun observeConnectedBluetoothDevices(): Flow<List<ConnectedBluetoothDevice>> {
        return callbackFlow {
            if (!hasBluetoothConnectPermission()) {
                trySend(emptyList())
                close()
                return@callbackFlow
            }

            val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter
            val connectedByProfile = mutableMapOf<Int, List<ConnectedBluetoothDevice>>()
            val profileProxies = mutableMapOf<Int, BluetoothProfile>()

            fun emitConnectedDevices() {
                trySend(
                    connectedByProfile.values
                        .flatten()
                        .distinctBy { it.address.uppercase() },
                )
            }

            fun refreshProfile(profile: Int) {
                val proxy = profileProxies[profile]
                connectedByProfile[profile] = connectedDevicesForProfile(
                    adapter = adapter,
                    profileProxy = proxy,
                )
                emitConnectedDevices()
            }

            val listener = object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                    profileProxies[profile] = proxy
                    refreshProfile(profile)
                }

                override fun onServiceDisconnected(profile: Int) {
                    profileProxies.remove(profile)
                    connectedByProfile.remove(profile)
                    emitConnectedDevices()
                }
            }

            connectedProfiles().forEach { profile ->
                adapter?.getProfileProxy(context, listener, profile)
            }

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    when (intent.action) {
                        BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED,
                        BluetoothDevice.ACTION_ACL_CONNECTED,
                        BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                            profileProxies.keys.forEach(::refreshProfile)
                        }
                    }
                }
            }

            registerConnectionReceiver(receiver)
            emitConnectedDevices()

            awaitClose {
                runCatching { context.unregisterReceiver(receiver) }
                profileProxies.forEach { (profile, proxy) ->
                    adapter?.closeProfileProxy(profile, proxy)
                }
            }
        }.distinctUntilChanged()
    }

    @SuppressLint("MissingPermission")
    private fun connectedDevicesForProfile(
        adapter: BluetoothAdapter?,
        profileProxy: BluetoothProfile?,
    ): List<ConnectedBluetoothDevice> {
        if (profileProxy == null) return emptyList()

        val profileConnectedDevices = profileProxy.connectedDevices.orEmpty()
        val bondedConnectedDevices = adapter?.bondedDevices.orEmpty()
            .filter { device ->
                profileProxy.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED
            }

        return (profileConnectedDevices + bondedConnectedDevices)
            .mapNotNull { device -> device.toConnectedBluetoothDevice() }
            .distinctBy { it.address.uppercase() }
    }

    @SuppressLint("MissingPermission")
    private fun BluetoothDevice.toConnectedBluetoothDevice(): ConnectedBluetoothDevice? {
        val address = address?.uppercase() ?: return null
        return ConnectedBluetoothDevice(
            name = name.orEmpty(),
            address = address,
        )
    }

    private fun connectedProfiles(): List<Int> {
        return buildList {
            add(BluetoothProfile.A2DP)
            add(BluetoothProfile.HEADSET)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(BluetoothProfile.HEARING_AID)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(BluetoothProfile.LE_AUDIO)
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

    private fun registerConnectionReceiver(receiver: BroadcastReceiver) {
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            context.registerReceiver(receiver, filter)
        }
    }
}
