package com.example.deckphonephone.deck.platform

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class DeckBluetoothAccessibilityService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private val pendingRequestListener: () -> Unit = {
        handler.post { processPendingRequest() }
        Unit
    }
    private var isRetryScheduled = false
    private var isClickInProgress = false

    override fun onCreate() {
        super.onCreate()
        BluetoothSettingsAutomationCoordinator.setPendingRequestListener(pendingRequestListener)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        BluetoothSettingsAutomationCoordinator.setPendingRequestListener(pendingRequestListener)
        processPendingRequest()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        processPendingRequest()
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        BluetoothSettingsAutomationCoordinator.clearPendingRequestListener(pendingRequestListener)
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun processPendingRequest() {
        BluetoothSettingsAutomationCoordinator.expireIfNeeded()?.let { expiredRequest ->
            isClickInProgress = false
            showToast("${expiredRequest.deviceName}을 Bluetooth 설정에서 찾지 못했습니다.")
            return
        }

        if (BluetoothSettingsAutomationCoordinator.clickedRequest() != null) return
        if (isClickInProgress) return

        val request = BluetoothSettingsAutomationCoordinator.current() ?: return
        val root = rootInActiveWindow
        if (root == null) {
            scheduleRetry()
            return
        }

        when (
            val match = BluetoothSettingsNodeMatcher.findTarget(
                candidates = collectCandidates(root),
                deviceName = request.deviceName,
                deviceAddress = request.deviceAddress,
            )
        ) {
            is BluetoothSettingsNodeMatch.Found -> {
                clickCandidate(
                    candidate = match.candidate.value,
                    stateBeforeClick = match.state,
                )
            }

            BluetoothSettingsNodeMatch.Ambiguous -> {
                BluetoothSettingsAutomationCoordinator.finish()
                showToast("같은 이름의 Bluetooth 기기가 있어 자동으로 선택하지 못했습니다.")
            }

            BluetoothSettingsNodeMatch.NotFound -> {
                scheduleRetry()
            }
        }
    }

    private fun clickCandidate(
        candidate: AccessibilityNodeInfo,
        stateBeforeClick: BluetoothSettingsDeviceState,
    ) {
        if (candidate.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
            markClickedAndReturn(stateBeforeClick)
            return
        }

        tapNodeCenter(
            node = candidate,
            onCompleted = {
                isClickInProgress = false
                markClickedAndReturn(stateBeforeClick)
            },
            onCancelled = {
                isClickInProgress = false
                finishWithClickFailure()
            },
        )
    }

    private fun tapNodeCenter(
        node: AccessibilityNodeInfo,
        onCompleted: () -> Unit,
        onCancelled: () -> Unit,
    ) {
        val bounds = Rect()
        node.getBoundsInScreen(bounds)
        if (bounds.isEmpty) {
            onCancelled()
            return
        }

        val path = Path().apply {
            moveTo(bounds.exactCenterX(), bounds.exactCenterY())
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0L, TAP_DURATION_MS))
            .build()

        isClickInProgress = true
        val dispatched = dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    onCompleted()
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    onCancelled()
                }
            },
            handler,
        )

        if (!dispatched) {
            isClickInProgress = false
            onCancelled()
        }
    }

    private fun markClickedAndReturn(stateBeforeClick: BluetoothSettingsDeviceState) {
        BluetoothSettingsAutomationCoordinator.markClicked(stateBeforeClick)?.let {
            showToast("Bluetooth 기기를 실행했습니다.")
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            BluetoothSettingsAutomationCoordinator.finish()
        }
    }

    private fun finishWithClickFailure() {
        BluetoothSettingsAutomationCoordinator.finish()
        showToast("기기를 자동으로 선택하지 못했습니다.")
    }

    private fun scheduleRetry() {
        if (isRetryScheduled) return
        isRetryScheduled = true
        handler.postDelayed(
            {
                isRetryScheduled = false
                processPendingRequest()
            },
            RETRY_DELAY_MS,
        )
    }

    private fun collectCandidates(
        root: AccessibilityNodeInfo,
    ): List<BluetoothSettingsRowCandidate<AccessibilityNodeInfo>> {
        val candidates = mutableListOf<BluetoothSettingsRowCandidate<AccessibilityNodeInfo>>()
        val seenKeys = mutableSetOf<String>()

        visitNodes(root) { node ->
            val clickableNode = findClickableNode(node) ?: return@visitNodes
            val key = clickableNode.candidateKey()
            if (!seenKeys.add(key)) return@visitNodes

            val texts = collectTexts(clickableNode)
            if (texts.isNotEmpty()) {
                candidates += BluetoothSettingsRowCandidate(
                    value = clickableNode,
                    texts = texts,
                    isPrimaryClickTarget = hasDeviceTitle(clickableNode),
                )
            }
        }

        return candidates
    }

    private fun visitNodes(
        root: AccessibilityNodeInfo,
        onNode: (AccessibilityNodeInfo) -> Unit,
    ) {
        val nodes = ArrayDeque<AccessibilityNodeInfo>()
        nodes.add(root)
        while (nodes.isNotEmpty()) {
            val node = nodes.removeFirst()
            onNode(node)
            repeat(node.childCount) { index ->
                node.getChild(index)?.let(nodes::add)
            }
        }
    }

    private fun findClickableNode(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        var current: AccessibilityNodeInfo? = node
        var depth = 0
        while (current != null && depth < MAX_CLICKABLE_PARENT_DEPTH) {
            if (current.isClickable) return current
            current = current.parent
            depth += 1
        }
        return null
    }

    private fun hasDeviceTitle(root: AccessibilityNodeInfo): Boolean {
        var hasTitle = false
        visitNodes(root) { node ->
            if (node.viewIdResourceName == DEVICE_TITLE_VIEW_ID) {
                hasTitle = true
            }
        }
        return hasTitle
    }

    private fun collectTexts(root: AccessibilityNodeInfo): List<String> {
        val texts = mutableListOf<String>()
        visitNodes(root) { node ->
            node.text?.toString()?.takeIf { it.isNotBlank() }?.let(texts::add)
            node.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let(texts::add)
        }
        return texts
    }

    private fun AccessibilityNodeInfo.candidateKey(): String {
        val bounds = Rect()
        getBoundsInScreen(bounds)
        return listOf(
            viewIdResourceName.orEmpty(),
            className?.toString().orEmpty(),
            bounds.flattenToString(),
        ).joinToString(separator = "|")
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val RETRY_DELAY_MS = 600L
        const val TAP_DURATION_MS = 80L
        const val MAX_CLICKABLE_PARENT_DEPTH = 6
        const val DEVICE_TITLE_VIEW_ID = "android:id/title"
    }
}
