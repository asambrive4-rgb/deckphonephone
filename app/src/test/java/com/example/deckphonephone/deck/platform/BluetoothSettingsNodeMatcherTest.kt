package com.example.deckphonephone.deck.platform

import org.junit.Assert.assertEquals
import org.junit.Test

class BluetoothSettingsNodeMatcherTest {
    @Test
    fun `finds target by visible bluetooth address before name`() {
        val target = BluetoothSettingsRowCandidate(
            value = "target",
            texts = listOf("Buds", "AC:80:0A:20:CB:AF"),
        )
        val sameName = BluetoothSettingsRowCandidate(
            value = "sameName",
            texts = listOf("Buds", "12:34:56:78:90:AB"),
        )

        val result = BluetoothSettingsNodeMatcher.findTarget(
            candidates = listOf(sameName, target),
            deviceName = "Buds",
            deviceAddress = "AC:80:0A:20:CB:AF",
        )

        assertEquals(
            BluetoothSettingsNodeMatch.Found(
                candidate = target,
                state = BluetoothSettingsDeviceState.Unknown,
            ),
            result,
        )
    }

    @Test
    fun `finds target by unique normalized name when address is not visible`() {
        val target = BluetoothSettingsRowCandidate(
            value = "target",
            texts = listOf("Galaxy Buds", "연결되지 않음"),
        )

        val result = BluetoothSettingsNodeMatcher.findTarget(
            candidates = listOf(target),
            deviceName = "galaxybuds",
            deviceAddress = "AC:80:0A:20:CB:AF",
        )

        assertEquals(
            BluetoothSettingsNodeMatch.Found(
                candidate = target,
                state = BluetoothSettingsDeviceState.Disconnected,
            ),
            result,
        )
    }

    @Test
    fun `prefers bluetooth row over device settings button when name appears twice`() {
        val row = BluetoothSettingsRowCandidate(
            value = "row",
            texts = listOf("주상의 Buds3 Pro", "통화 및 오디오를 위해 연결됨"),
            isPrimaryClickTarget = true,
        )
        val settingsButton = BluetoothSettingsRowCandidate(
            value = "settingsButton",
            texts = listOf("주상의 Buds3 Pro, 기기 설정"),
            isPrimaryClickTarget = false,
        )

        val result = BluetoothSettingsNodeMatcher.findTarget(
            candidates = listOf(row, settingsButton),
            deviceName = "주상의 Buds3 Pro",
            deviceAddress = "AC:80:0A:20:CB:AF",
        )

        assertEquals(
            BluetoothSettingsNodeMatch.Found(
                candidate = row,
                state = BluetoothSettingsDeviceState.Connected,
            ),
            result,
        )
    }
    @Test
    fun `returns ambiguous when same name appears without visible address`() {
        val first = BluetoothSettingsRowCandidate(
            value = "first",
            texts = listOf("Buds"),
        )
        val second = BluetoothSettingsRowCandidate(
            value = "second",
            texts = listOf("Buds"),
        )

        val result = BluetoothSettingsNodeMatcher.findTarget(
            candidates = listOf(first, second),
            deviceName = "Buds",
            deviceAddress = "AC:80:0A:20:CB:AF",
        )

        assertEquals(BluetoothSettingsNodeMatch.Ambiguous, result)
    }

    @Test
    fun `infers common korean and english connection states`() {
        assertEquals(
            BluetoothSettingsDeviceState.Connected,
            BluetoothSettingsNodeMatcher.inferState(listOf("Connected")),
        )
        assertEquals(
            BluetoothSettingsDeviceState.Disconnected,
            BluetoothSettingsNodeMatcher.inferState(listOf("연결되지 않음")),
        )
        assertEquals(
            BluetoothSettingsDeviceState.Unknown,
            BluetoothSettingsNodeMatcher.inferState(listOf("페어링된 기기")),
        )
    }
}
