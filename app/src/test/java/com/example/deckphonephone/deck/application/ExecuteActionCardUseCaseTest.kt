package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ExecuteActionCardUseCaseTest {
    private val openUrlPort = FakeOpenUrlPort()
    private val copyTextPort = FakeCopyTextPort()
    private val bluetoothDeviceActionPort = FakeBluetoothDeviceActionPort()
    private val executeActionCard = ExecuteActionCardUseCase(
        openUrlPort = openUrlPort,
        copyTextPort = copyTextPort,
        bluetoothDeviceActionPort = bluetoothDeviceActionPort,
    )

    @Test
    fun `web card opens url through port`() = runBlocking {
        val card = webCard(url = "https://example.com")

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.OpenedUrl, result)
        assertEquals(listOf("https://example.com"), openUrlPort.openedUrls)
    }

    @Test
    fun `web card returns failure when url cannot be opened`() = runBlocking {
        openUrlPort.result = OpenUrlResult.Failure
        val card = webCard(url = "https://example.com")

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.OpenUrlFailed, result)
        assertEquals(listOf("https://example.com"), openUrlPort.openedUrls)
    }

    @Test
    fun `text card copies text through port`() = runBlocking {
        val card = textCard(text = "hello")

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.CopiedText, result)
        assertEquals(listOf("hello"), copyTextPort.copiedTexts)
    }

    @Test
    fun `text card returns failure when text cannot be copied`() = runBlocking {
        copyTextPort.result = CopyTextResult.Failure
        val card = textCard(text = "hello")

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.CopyTextFailed, result)
        assertEquals(listOf("hello"), copyTextPort.copiedTexts)
    }

    @Test
    fun `text card returns blank result without calling port when text is blank`() = runBlocking {
        val card = textCard(text = " ")

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.CopyTextBlank, result)
        assertEquals(emptyList<String>(), copyTextPort.copiedTexts)
    }

    @Test
    fun `bluetooth card starts operation through port`() = runBlocking {
        val card = bluetoothCard()

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.BluetoothAutomationStarted("Buds"), result)
        assertEquals(listOf("Buds" to "AC:80:0A:20:CB:AF"), bluetoothDeviceActionPort.requests)
        assertEquals(emptyList<String>(), openUrlPort.openedUrls)
        assertEquals(emptyList<String>(), copyTextPort.copiedTexts)
    }

    @Test
    fun `bluetooth card maps accessibility permission required result`() = runBlocking {
        bluetoothDeviceActionPort.result = BluetoothDeviceActionResult.AccessibilityPermissionRequired
        val card = bluetoothCard()

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.BluetoothAccessibilityPermissionRequired("Buds"), result)
    }

    @Test
    fun `bluetooth card maps settings open failure result`() = runBlocking {
        bluetoothDeviceActionPort.result = BluetoothDeviceActionResult.SettingsOpenFailed
        val card = bluetoothCard()

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.BluetoothSettingsOpenFailed, result)
    }

    @Test
    fun `bluetooth card maps already running result`() = runBlocking {
        bluetoothDeviceActionPort.result = BluetoothDeviceActionResult.AlreadyRunning
        val card = bluetoothCard()

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.BluetoothAutomationAlreadyRunning, result)
    }

    @Test
    fun `bluetooth card maps failure result`() = runBlocking {
        bluetoothDeviceActionPort.result = BluetoothDeviceActionResult.Failure
        val card = bluetoothCard()

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.BluetoothAutomationFailed, result)
    }

    @Test
    fun `bluetooth card returns blank address without calling port`() = runBlocking {
        val card = bluetoothCard(address = " ")

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.BluetoothDeviceAddressBlank, result)
        assertEquals(emptyList<Pair<String, String>>(), bluetoothDeviceActionPort.requests)
    }

    @Test
    fun `disabled card does not call execution ports`() = runBlocking {
        val card = webCard(url = "https://example.com").copy(isEnabled = false)

        val result = executeActionCard(card)

        assertEquals(ExecuteActionCardResult.DisabledActionCard, result)
        assertEquals(emptyList<String>(), openUrlPort.openedUrls)
        assertEquals(emptyList<String>(), copyTextPort.copiedTexts)
        assertEquals(emptyList<Pair<String, String>>(), bluetoothDeviceActionPort.requests)
    }

    private fun webCard(url: String) = ActionCard(
        id = 1,
        categoryId = 1,
        title = "Web",
        operation = ActionCardOperation.OpenUrl(url),
    )

    private fun textCard(text: String) = ActionCard(
        id = 1,
        categoryId = 1,
        title = "Text",
        operation = ActionCardOperation.CopyText(text),
    )

    private fun bluetoothCard(
        name: String = "Buds",
        address: String = "AC:80:0A:20:CB:AF",
    ) = ActionCard(
        id = 1,
        categoryId = 1,
        title = "Buds",
        operation = ActionCardOperation.BluetoothDevice(
            deviceName = name,
            deviceAddress = address,
        ),
    )
}

private class FakeOpenUrlPort : OpenUrlPort {
    val openedUrls = mutableListOf<String>()
    var result: OpenUrlResult = OpenUrlResult.Success

    override suspend fun openUrl(url: String): OpenUrlResult {
        openedUrls += url
        return result
    }
}

private class FakeCopyTextPort : CopyTextPort {
    val copiedTexts = mutableListOf<String>()
    var result: CopyTextResult = CopyTextResult.Success

    override suspend fun copyText(text: String): CopyTextResult {
        copiedTexts += text
        return result
    }
}

private class FakeBluetoothDeviceActionPort : BluetoothDeviceActionPort {
    val requests = mutableListOf<Pair<String, String>>()
    var result: BluetoothDeviceActionResult = BluetoothDeviceActionResult.Started

    override suspend fun startBluetoothDeviceAction(
        deviceName: String,
        deviceAddress: String,
    ): BluetoothDeviceActionResult {
        requests += deviceName to deviceAddress
        return result
    }
}
