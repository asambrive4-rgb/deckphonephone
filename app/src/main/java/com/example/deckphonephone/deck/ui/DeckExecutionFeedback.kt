package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.ExecuteCardResult

internal sealed interface DeckSettingExecutionFeedback {
    data object None : DeckSettingExecutionFeedback
    data class Message(val message: String) : DeckSettingExecutionFeedback
}

internal data class DeckOverlayExecutionFeedback(
    val message: String? = null,
    val isTransientMessage: Boolean = false,
    val shouldFinish: Boolean = false,
)

internal fun ExecuteCardResult.toSettingExecutionFeedback(): DeckSettingExecutionFeedback {
    return when (this) {
        ExecuteCardResult.OpenedUrl -> DeckSettingExecutionFeedback.None
        ExecuteCardResult.CopiedText -> DeckSettingExecutionFeedback.Message("복사했습니다")
        ExecuteCardResult.DisabledCard -> DeckSettingExecutionFeedback.Message("비활성화된 카드입니다.")
        ExecuteCardResult.CopyTextBlank -> DeckSettingExecutionFeedback.Message("복사할 문구가 없습니다")
        ExecuteCardResult.OpenUrlFailed -> DeckSettingExecutionFeedback.Message("웹페이지를 열지 못했습니다.")
        ExecuteCardResult.CopyTextFailed -> DeckSettingExecutionFeedback.Message("문구를 복사하지 못했습니다.")
        is ExecuteCardResult.BluetoothAutomationStarted -> {
            DeckSettingExecutionFeedback.Message("Bluetooth 설정에서 ${deviceName}을 찾는 중입니다.")
        }

        is ExecuteCardResult.BluetoothAccessibilityPermissionRequired -> {
            DeckSettingExecutionFeedback.Message("접근성 설정에서 DeckDeckDeck을 켠 뒤 다시 탭해 주세요.")
        }

        ExecuteCardResult.BluetoothSettingsOpenFailed -> {
            DeckSettingExecutionFeedback.Message("설정 화면을 열지 못했습니다.")
        }

        ExecuteCardResult.BluetoothAutomationAlreadyRunning -> {
            DeckSettingExecutionFeedback.Message("Bluetooth 자동 실행이 이미 진행 중입니다.")
        }

        ExecuteCardResult.BluetoothDeviceAddressBlank -> {
            DeckSettingExecutionFeedback.Message("블루투스 기기 주소를 찾지 못했습니다.")
        }

        ExecuteCardResult.BluetoothAutomationFailed -> {
            DeckSettingExecutionFeedback.Message("블루투스 자동 실행을 시작하지 못했습니다.")
        }
    }
}

internal fun ExecuteCardResult.toOverlayExecutionFeedback(): DeckOverlayExecutionFeedback {
    return when (this) {
        ExecuteCardResult.OpenedUrl -> DeckOverlayExecutionFeedback(shouldFinish = true)
        ExecuteCardResult.CopiedText -> DeckOverlayExecutionFeedback(
            message = "복사했습니다",
            isTransientMessage = true,
            shouldFinish = true,
        )

        ExecuteCardResult.DisabledCard -> DeckOverlayExecutionFeedback(
            message = "비활성화된 카드입니다.",
        )

        ExecuteCardResult.CopyTextBlank -> DeckOverlayExecutionFeedback(
            message = "복사할 문구가 없습니다",
        )

        ExecuteCardResult.OpenUrlFailed -> DeckOverlayExecutionFeedback(
            message = "웹페이지를 열지 못했습니다.",
        )

        ExecuteCardResult.CopyTextFailed -> DeckOverlayExecutionFeedback(
            message = "문구를 복사하지 못했습니다.",
        )

        is ExecuteCardResult.BluetoothAutomationStarted -> DeckOverlayExecutionFeedback(
            message = "Bluetooth 설정에서 ${deviceName}을 찾는 중입니다.",
            isTransientMessage = true,
            shouldFinish = true,
        )

        is ExecuteCardResult.BluetoothAccessibilityPermissionRequired -> DeckOverlayExecutionFeedback(
            message = "접근성 설정에서 DeckDeckDeck을 켠 뒤 다시 탭해 주세요.",
            isTransientMessage = true,
            shouldFinish = true,
        )

        ExecuteCardResult.BluetoothSettingsOpenFailed -> DeckOverlayExecutionFeedback(
            message = "설정 화면을 열지 못했습니다.",
        )

        ExecuteCardResult.BluetoothAutomationAlreadyRunning -> DeckOverlayExecutionFeedback(
            message = "Bluetooth 자동 실행이 이미 진행 중입니다.",
        )

        ExecuteCardResult.BluetoothDeviceAddressBlank -> DeckOverlayExecutionFeedback(
            message = "블루투스 기기 주소를 찾지 못했습니다.",
        )

        ExecuteCardResult.BluetoothAutomationFailed -> DeckOverlayExecutionFeedback(
            message = "블루투스 자동 실행을 시작하지 못했습니다.",
        )
    }
}
