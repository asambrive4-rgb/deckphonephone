package com.example.deckphonephone.deck.platform

import android.os.SystemClock

data class BluetoothSettingsAutomationRequest(
    val deviceName: String,
    val deviceAddress: String,
)

data class BluetoothSettingsClickedRequest(
    val request: BluetoothSettingsAutomationRequest,
    val stateBeforeClick: BluetoothSettingsDeviceState,
)

object BluetoothSettingsAutomationCoordinator {
    const val CLICK_RESULT_DELAY_MS = 1_500L
    private const val REQUEST_TIMEOUT_MS = 15_000L

    private var pendingRequest: PendingRequest? = null
    private var pendingRequestListener: (() -> Unit)? = null

    @Synchronized
    fun setPendingRequestListener(listener: () -> Unit) {
        pendingRequestListener = listener
    }

    @Synchronized
    fun clearPendingRequestListener(listener: () -> Unit) {
        if (pendingRequestListener === listener) {
            pendingRequestListener = null
        }
    }

    @Synchronized
    fun tryStart(
        request: BluetoothSettingsAutomationRequest,
        nowMillis: Long = SystemClock.elapsedRealtime(),
    ): Boolean {
        expireIfNeededLocked(nowMillis)
        if (pendingRequest != null) return false

        pendingRequest = PendingRequest(
            request = request,
            startedAtMillis = nowMillis,
        )
        pendingRequestListener?.invoke()
        return true
    }

    @Synchronized
    fun current(
        nowMillis: Long = SystemClock.elapsedRealtime(),
    ): BluetoothSettingsAutomationRequest? {
        expireIfNeededLocked(nowMillis)
        return pendingRequest?.request
    }

    @Synchronized
    fun markClicked(
        stateBeforeClick: BluetoothSettingsDeviceState,
        nowMillis: Long = SystemClock.elapsedRealtime(),
    ): BluetoothSettingsClickedRequest? {
        val pending = pendingRequest ?: return null
        val clicked = pending.copy(
            clickedAtMillis = nowMillis,
            stateBeforeClick = stateBeforeClick,
        )
        pendingRequest = clicked
        return clicked.toClickedRequest()
    }

    @Synchronized
    fun clickedRequest(
        nowMillis: Long = SystemClock.elapsedRealtime(),
    ): BluetoothSettingsClickedRequest? {
        expireIfNeededLocked(nowMillis)
        return pendingRequest
            ?.takeIf { it.clickedAtMillis != null }
            ?.toClickedRequest()
    }

    @Synchronized
    fun expireIfNeeded(
        nowMillis: Long = SystemClock.elapsedRealtime(),
    ): BluetoothSettingsAutomationRequest? {
        return expireIfNeededLocked(nowMillis)
    }

    @Synchronized
    fun finish(): BluetoothSettingsAutomationRequest? {
        val request = pendingRequest?.request
        pendingRequest = null
        return request
    }

    @Synchronized
    fun clearIfCurrent(request: BluetoothSettingsAutomationRequest) {
        if (pendingRequest?.request == request) {
            pendingRequest = null
        }
    }

    private fun expireIfNeededLocked(nowMillis: Long): BluetoothSettingsAutomationRequest? {
        val pending = pendingRequest ?: return null
        if (nowMillis - pending.startedAtMillis <= REQUEST_TIMEOUT_MS) {
            return null
        }

        pendingRequest = null
        return pending.request
    }

    private fun PendingRequest.toClickedRequest(): BluetoothSettingsClickedRequest {
        return BluetoothSettingsClickedRequest(
            request = request,
            stateBeforeClick = stateBeforeClick,
        )
    }

    private data class PendingRequest(
        val request: BluetoothSettingsAutomationRequest,
        val startedAtMillis: Long,
        val clickedAtMillis: Long? = null,
        val stateBeforeClick: BluetoothSettingsDeviceState = BluetoothSettingsDeviceState.Unknown,
    )
}