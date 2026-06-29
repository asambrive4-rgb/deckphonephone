package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.ExecuteActionCardResult
import org.junit.Assert.assertEquals
import org.junit.Test

class DeckExecutionFeedbackTest {
    @Test
    fun `setting feedback keeps opened url silent`() {
        val feedback = ExecuteActionCardResult.OpenedUrl.toSettingExecutionFeedback()

        assertEquals(DeckSettingExecutionFeedback.None, feedback)
    }

    @Test
    fun `setting feedback maps copied text to message`() {
        val feedback = ExecuteActionCardResult.CopiedText.toSettingExecutionFeedback()

        assertEquals(DeckSettingExecutionFeedback.Message("복사했습니다"), feedback)
    }

    @Test
    fun `overlay feedback finishes after opening url`() {
        val feedback = ExecuteActionCardResult.OpenedUrl.toOverlayExecutionFeedback()

        assertEquals(
            DeckOverlayExecutionFeedback(shouldFinish = true),
            feedback,
        )
    }

    @Test
    fun `overlay feedback shows transient message and finishes after bluetooth automation starts`() {
        val feedback = ExecuteActionCardResult.BluetoothAutomationStarted("Buds").toOverlayExecutionFeedback()

        assertEquals(
            DeckOverlayExecutionFeedback(
                message = "Bluetooth 설정에서 Buds을 찾는 중입니다.",
                isTransientMessage = true,
                shouldFinish = true,
            ),
            feedback,
        )
    }
}
