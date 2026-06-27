package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ExecuteCardUseCaseTest {
    private val openUrlPort = FakeOpenUrlPort()
    private val pasteTextPort = FakePasteTextPort()
    private val executeCard = ExecuteCardUseCase(
        openUrlPort = openUrlPort,
        pasteTextPort = pasteTextPort,
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
    fun `text card returns deferred result while paste path is undecided`() = runBlocking {
        val card = textCard(text = "hello")

        val result = executeCard(card)

        assertEquals(ExecuteCardResult.PasteTextDeferred, result)
        assertEquals(listOf("hello"), pasteTextPort.pastedTexts)
    }

    @Test
    fun `disabled card does not call execution ports`() = runBlocking {
        val card = webCard(url = "https://example.com").copy(isEnabled = false)

        val result = executeCard(card)

        assertEquals(ExecuteCardResult.DisabledCard, result)
        assertEquals(emptyList<String>(), openUrlPort.openedUrls)
        assertEquals(emptyList<String>(), pasteTextPort.pastedTexts)
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
        action = CardAction.TextPaste(text),
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

private class FakePasteTextPort : PasteTextPort {
    val pastedTexts = mutableListOf<String>()
    var result: PasteTextResult = PasteTextResult.Deferred

    override suspend fun pasteText(text: String): PasteTextResult {
        pastedTexts += text
        return result
    }
}