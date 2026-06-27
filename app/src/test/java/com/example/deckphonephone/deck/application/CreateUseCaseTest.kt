package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.CardAction
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateUseCaseTest {
    private val repository = FakeDeckRepository()
    private val createCategory = CreateCategoryUseCase(repository)
    private val createTextCard = CreateTextCardUseCase(repository)
    private val createWebCard = CreateWebCardUseCase(repository)

    @Test
    fun `category name is required`() = runBlocking {
        val result = createCategory(" ")

        assertEquals(DeckResult.Failure(DeckError.CategoryNameBlank), result)
    }

    @Test
    fun `card title is required for text card`() = runBlocking {
        val result = createTextCard(categoryId = 1, title = "", text = "hello")

        assertEquals(DeckResult.Failure(DeckError.CardTitleBlank), result)
    }

    @Test
    fun `text payload is required`() = runBlocking {
        val result = createTextCard(categoryId = 1, title = "Reply", text = " ")

        assertEquals(DeckResult.Failure(DeckError.TextBlank), result)
    }

    @Test
    fun `web url is required`() = runBlocking {
        val result = createWebCard(categoryId = 1, title = "Search", rawUrl = "")

        assertEquals(DeckResult.Failure(DeckError.UrlBlank), result)
    }

    @Test
    fun `web url must be valid`() = runBlocking {
        val result = createWebCard(categoryId = 1, title = "Bad", rawUrl = "ftp://example.com")

        assertEquals(DeckResult.Failure(DeckError.InvalidUrl), result)
    }

    @Test
    fun `web card stores normalized url`() = runBlocking {
        val result = createWebCard(categoryId = 7, title = "GitHub", rawUrl = "github.com")

        assertTrue(result is DeckResult.Success)
        val card = (result as DeckResult.Success).value
        assertEquals(CardAction.OpenUrl("https://github.com"), card.action)
    }
}