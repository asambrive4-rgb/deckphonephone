package com.example.deckphonephone.deck.data.local

import com.example.deckphonephone.deck.domain.CardAction
import org.junit.Assert.assertEquals
import org.junit.Test

class DeckEntityMapperTest {
    @Test
    fun `copy text action is stored with copy text type`() {
        val entity = newCardEntity(
            categoryId = 1,
            title = "Reply",
            description = "",
            action = CardAction.CopyText("hello"),
            isEnabled = true,
        )

        assertEquals("copy_text", entity.actionType)
        assertEquals("hello", entity.textValue)
    }

    @Test
    fun `legacy text paste action is read as copy text`() {
        val entity = ActionCardEntity(
            id = 1,
            categoryId = 1,
            title = "Legacy",
            description = "",
            actionType = "text_paste",
            textValue = "hello",
            isEnabled = true,
        )

        assertEquals(CardAction.CopyText("hello"), entity.toDomain().action)
    }

    @Test
    fun `bluetooth action is stored with device fields`() {
        val entity = newCardEntity(
            categoryId = 1,
            title = "Buds",
            description = "",
            action = CardAction.BluetoothDevice(
                deviceName = "Buds",
                deviceAddress = "AC:80:0A:20:CB:AF",
            ),
            isEnabled = true,
        )

        assertEquals("bluetooth_device", entity.actionType)
        assertEquals("Buds", entity.bluetoothDeviceName)
        assertEquals("AC:80:0A:20:CB:AF", entity.bluetoothDeviceAddress)
    }

    @Test
    fun `bluetooth action is read from device fields`() {
        val entity = ActionCardEntity(
            id = 1,
            categoryId = 1,
            title = "Buds",
            description = "",
            actionType = "bluetooth_device",
            bluetoothDeviceName = "Buds",
            bluetoothDeviceAddress = "AC:80:0A:20:CB:AF",
            isEnabled = true,
        )

        assertEquals(
            CardAction.BluetoothDevice(
                deviceName = "Buds",
                deviceAddress = "AC:80:0A:20:CB:AF",
            ),
            entity.toDomain().action,
        )
    }
}