package com.example.deckphonephone.deck.data.local

import com.example.deckphonephone.deck.domain.ActionCardOperation
import org.junit.Assert.assertEquals
import org.junit.Test

class DeckEntityMapperTest {
    @Test
    fun `copy text operation is stored with copy text type`() {
        val entity = newActionCardEntity(
            categoryId = 1,
            title = "Reply",
            description = "",
            operation = ActionCardOperation.CopyText("hello"),
            isEnabled = true,
        )

        assertEquals("copy_text", entity.operationType)
        assertEquals("hello", entity.textValue)
    }

    @Test
    fun `legacy text paste operation is read as copy text`() {
        val entity = ActionCardEntity(
            id = 1,
            categoryId = 1,
            title = "Legacy",
            description = "",
            operationType = "text_paste",
            textValue = "hello",
            isEnabled = true,
        )

        assertEquals(ActionCardOperation.CopyText("hello"), entity.toDomain().operation)
    }

    @Test
    fun `bluetooth operation is stored with device fields`() {
        val entity = newActionCardEntity(
            categoryId = 1,
            title = "Buds",
            description = "",
            operation = ActionCardOperation.BluetoothDevice(
                deviceName = "Buds",
                deviceAddress = "AC:80:0A:20:CB:AF",
            ),
            isEnabled = true,
        )

        assertEquals("bluetooth_device", entity.operationType)
        assertEquals("Buds", entity.bluetoothDeviceName)
        assertEquals("AC:80:0A:20:CB:AF", entity.bluetoothDeviceAddress)
    }

    @Test
    fun `bluetooth operation is read from device fields`() {
        val entity = ActionCardEntity(
            id = 1,
            categoryId = 1,
            title = "Buds",
            description = "",
            operationType = "bluetooth_device",
            bluetoothDeviceName = "Buds",
            bluetoothDeviceAddress = "AC:80:0A:20:CB:AF",
            isEnabled = true,
        )

        assertEquals(
            ActionCardOperation.BluetoothDevice(
                deviceName = "Buds",
                deviceAddress = "AC:80:0A:20:CB:AF",
            ),
            entity.toDomain().operation,
        )
    }
}