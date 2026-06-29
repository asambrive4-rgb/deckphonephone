package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.DeckError
import com.example.deckphonephone.deck.application.PairedBluetoothDevice
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation
import org.junit.Assert.assertEquals
import org.junit.Test

class DeckSettingUiMapperTest {
    @Test
    fun `bluetooth card maps to edit form state`() {
        val card = ActionCard(
            id = 10L,
            categoryId = 1L,
            title = "Buds",
            operation = ActionCardOperation.BluetoothDevice(
                deviceName = "Buds",
                deviceAddress = "AC:80:0A:20:CB:AF",
            ),
            isEnabled = false,
        )

        val editState = card.toActionCardEditState()

        assertEquals(
            ActionCardEditState(
                actionCardId = 10L,
                title = "Buds",
                payload = "AC:80:0A:20:CB:AF",
                selectedActionCardType = ActionCardType.Bluetooth,
                selectedBluetoothDevice = PairedBluetoothDevice(
                    name = "Buds",
                    address = "AC:80:0A:20:CB:AF",
                ),
                isEnabled = false,
            ),
            editState,
        )
    }

    @Test
    fun `deck errors map to setting messages`() {
        assertEquals(
            "카테고리 이름을 입력해 주세요.",
            DeckError.CategoryNameBlank.toDeckSettingMessage(),
        )
        assertEquals(
            "웹 주소 형식이 올바르지 않습니다.",
            DeckError.InvalidUrl.toDeckSettingMessage(),
        )
    }
}
