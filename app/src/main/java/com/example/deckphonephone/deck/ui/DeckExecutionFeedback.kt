package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.ExecuteActionCardResult

internal sealed interface DeckSettingExecutionFeedback {
    data object None : DeckSettingExecutionFeedback
    data class Message(val message: String) : DeckSettingExecutionFeedback
}

internal data class DeckOverlayExecutionFeedback(
    val message: String? = null,
    val isTransientMessage: Boolean = false,
    val shouldFinish: Boolean = false,
)

internal fun ExecuteActionCardResult.toSettingExecutionFeedback(): DeckSettingExecutionFeedback {
    return when (this) {
        ExecuteActionCardResult.OpenedUrl -> DeckSettingExecutionFeedback.None
        ExecuteActionCardResult.CopiedText -> DeckSettingExecutionFeedback.Message("복사했습니다")
        ExecuteActionCardResult.DisabledActionCard -> DeckSettingExecutionFeedback.Message("비활성화된 액션 카드입니다.")
        ExecuteActionCardResult.CopyTextBlank -> DeckSettingExecutionFeedback.Message("복사할 문구가 없습니다")
        ExecuteActionCardResult.OpenUrlFailed -> DeckSettingExecutionFeedback.Message("웹페이지를 열지 못했습니다.")
        ExecuteActionCardResult.CopyTextFailed -> DeckSettingExecutionFeedback.Message("문구를 복사하지 못했습니다.")
        is ExecuteActionCardResult.BluetoothAutomationStarted -> {
            DeckSettingExecutionFeedback.Message("Bluetooth 설정에서 ${deviceName}을 찾는 중입니다.")
        }

        is ExecuteActionCardResult.BluetoothAccessibilityPermissionRequired -> {
            DeckSettingExecutionFeedback.Message("접근성 설정에서 DeckDeckDeck을 켠 뒤 다시 탭해 주세요.")
        }

        ExecuteActionCardResult.BluetoothSettingsOpenFailed -> {
            DeckSettingExecutionFeedback.Message("설정 화면을 열지 못했습니다.")
        }

        ExecuteActionCardResult.BluetoothAutomationAlreadyRunning -> {
            DeckSettingExecutionFeedback.Message("Bluetooth 자동 실행이 이미 진행 중입니다.")
        }

        ExecuteActionCardResult.BluetoothDeviceAddressBlank -> {
            DeckSettingExecutionFeedback.Message("블루투스 기기 주소를 찾지 못했습니다.")
        }

        ExecuteActionCardResult.BluetoothAutomationFailed -> {
            DeckSettingExecutionFeedback.Message("블루투스 자동 실행을 시작하지 못했습니다.")
        }
    }
}

internal fun ExecuteActionCardResult.toOverlayExecutionFeedback(): DeckOverlayExecutionFeedback {
    return when (this) {
        ExecuteActionCardResult.OpenedUrl -> DeckOverlayExecutionFeedback(shouldFinish = true)
        ExecuteActionCardResult.CopiedText -> DeckOverlayExecutionFeedback(
            message = "복사했습니다",
            isTransientMessage = true,
            shouldFinish = true,
        )

        ExecuteActionCardResult.DisabledActionCard -> DeckOverlayExecutionFeedback(
            message = "비활성화된 액션 카드입니다.",
        )

        ExecuteActionCardResult.CopyTextBlank -> DeckOverlayExecutionFeedback(
            message = "복사할 문구가 없습니다",
        )

        ExecuteActionCardResult.OpenUrlFailed -> DeckOverlayExecutionFeedback(
            message = "웹페이지를 열지 못했습니다.",
        )

        ExecuteActionCardResult.CopyTextFailed -> DeckOverlayExecutionFeedback(
            message = "문구를 복사하지 못했습니다.",
        )

        is ExecuteActionCardResult.BluetoothAutomationStarted -> DeckOverlayExecutionFeedback(
            message = "Bluetooth 설정에서 ${deviceName}을 찾는 중입니다.",
            isTransientMessage = true,
            shouldFinish = true,
        )

        is ExecuteActionCardResult.BluetoothAccessibilityPermissionRequired -> DeckOverlayExecutionFeedback(
            message = "접근성 설정에서 DeckDeckDeck을 켠 뒤 다시 탭해 주세요.",
            isTransientMessage = true,
            shouldFinish = true,
        )

        ExecuteActionCardResult.BluetoothSettingsOpenFailed -> DeckOverlayExecutionFeedback(
            message = "설정 화면을 열지 못했습니다.",
        )

        ExecuteActionCardResult.BluetoothAutomationAlreadyRunning -> DeckOverlayExecutionFeedback(
            message = "Bluetooth 자동 실행이 이미 진행 중입니다.",
        )

        ExecuteActionCardResult.BluetoothDeviceAddressBlank -> DeckOverlayExecutionFeedback(
            message = "블루투스 기기 주소를 찾지 못했습니다.",
        )

        ExecuteActionCardResult.BluetoothAutomationFailed -> DeckOverlayExecutionFeedback(
            message = "블루투스 자동 실행을 시작하지 못했습니다.",
        )
    }
}
