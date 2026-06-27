package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ExecuteCardUseCaseTest {
    private val openUrlPort = FakeOpenUrlPort()
    private val copyTextPort = FakeCopyTextPort()
    private val executeCard = ExecuteCardUseCase(
        openUrlPort = openUrlPort,
        copyTextPort = copyTextPort,
    )

    @Test
    fun `web card opens url through port`() = runBlocking {
        val card = webCard(url = "https://example.com")

        val result = executeCard(card)

        assertEquals(ExecuteCardResult.OpenedUrl, result)
        assertEquals(listOf("https://example.com"), openUrlPort.openedUrls)
    }

    @Test
    fun `web card returns failure when url cannot be opened`() = runBlocking {
        openUrlPort.result = OpenUrlResult.Failure
        val card = webCard(url = "https://example.com")

        val result = executeCard(card)

        assertEquals(ExecuteCardResult.OpenUrlFailed, result)
        assertEquals(listOf("https://example.com"), openUrlPort.openedUrls)
    }

    @Test
    fun `text card copies text through port`() = runBlocking {
        val card = textCard(text = "hello")

        val result = executeCard(card)

        assertEquals(ExecuteCardResult.CopiedText, result)
        assertEquals(listOf("hello"), copyTextPort.copiedTexts)
    }

    @Test
    fun `text card returns failure when text cannot be copied`() = runBlocking {
        copyTextPort.result = CopyTextResult.Failure
        val card = textCard(text = "hello")

        val result = executeCard(card)

        assertEquals(ExecuteCardResult.CopyTextFailed, result)
        assertEquals(listOf("hello"), copyTextPort.copiedTexts)
    }

    @Test
    fun `text card returns blank result without calling port when text is blank`() = runBlocking {
        val card = textCard(text = " ")

        val result = executeCard(card)

        assertEquals(ExecuteCardResult.CopyTextBlank, result)
        assertEquals(emptyList<String>(), copyTextPort.copiedTexts)
    }
    @Test
    fun `disabled card does not call execution ports`() = runBlocking {
        val card = webCard(url = "https://example.com").copy(isEnabled = false)

        val result = executeCard(card)

        assertEquals(ExecuteCardResult.DisabledCard, result)
        assertEquals(emptyList<String>(), openUrlPort.openedUrls)
        assertEquals(emptyList<String>(), copyTextPort.copiedTexts)
    }

    private fun webCard(url: String) = ActionCard(
        id = 1,
        categoryId = 1,
        title = "Web",
        action = CardAction.OpenUrl(url),
    )

    private fun textCard(text: String) = ActionCard(
        id = 1,
        categoryId = 1,
        title = "Text",
        action = CardAction.CopyText(text),
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
