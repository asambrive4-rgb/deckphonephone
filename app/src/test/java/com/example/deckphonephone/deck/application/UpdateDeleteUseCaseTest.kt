package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateDeleteUseCaseTest {
    private val repository = FakeDeckRepository()
    private val updateCategory = UpdateCategoryUseCase(repository)
    private val deleteCategory = DeleteCategoryUseCase(repository)
    private val updateTextCard = UpdateTextCardUseCase(repository)
    private val updateWebCard = UpdateWebCardUseCase(repository)
    private val deleteCard = DeleteCardUseCase(repository)
    private val setCardEnabled = SetCardEnabledUseCase(repository)

    @Test
    fun `category update requires name`() = runBlocking {
        val category = repository.createCategory(name = "Work")

        val result = updateCategory(category, " ")

        assertEquals(DeckResult.Failure(DeckError.CategoryNameBlank), result)
    }

    @Test
    fun `category update stores trimmed name`() = runBlocking {
        val category = repository.createCategory(name = "Work")

        val result = updateCategory(category, "  Personal  ")

        assertTrue(result is DeckResult.Success)
        assertEquals("Personal", (result as DeckResult.Success).value.name)
    }

    @Test
    fun `text card update requires title`() = runBlocking {
        val card = textCard()

        val result = updateTextCard(card = card, title = " ", text = "hello")

        assertEquals(DeckResult.Failure(DeckError.CardTitleBlank), result)
    }

    @Test
    fun `text card update requires payload`() = runBlocking {
        val card = textCard()

        val result = updateTextCard(card = card, title = "Reply", text = " ")

        assertEquals(DeckResult.Failure(DeckError.TextBlank), result)
    }

    @Test
    fun `web card update stores normalized url`() = runBlocking {
        val card = textCard()

        val result = updateWebCard(card = card, title = "Search", rawUrl = "google.com")

        assertTrue(result is DeckResult.Success)
        val updatedCard = (result as DeckResult.Success).value
        assertEquals(CardAction.OpenUrl("https://google.com"), updatedCard.action)
    }

    @Test
    fun `web card update rejects invalid url`() = runBlocking {
        val card = textCard()

        val result = updateWebCard(card = card, title = "Search", rawUrl = "ftp://example.com")

        assertEquals(DeckResult.Failure(DeckError.InvalidUrl), result)
    }

    @Test
    fun `card enabled state can be changed`() = runBlocking {
        val card = textCard()

        val updatedCard = setCardEnabled(card = card, isEnabled = false)

        assertFalse(updatedCard.isEnabled)
        assertFalse(repository.observeCards(card.categoryId).first().single().isEnabled)
    }

    @Test
    fun `delete category removes its cards`() = runBlocking {
        val category = repository.createCategory(name = "Work")
        repository.createCard(
            categoryId = category.id,
            title = "Reply",
            action = CardAction.CopyText("hello"),
        )

        deleteCategory(category.id)

        assertEquals(emptyList<ActionCard>(), repository.observeCards(category.id).first())
        assertTrue(repository.observeCategories().first().isEmpty())
    }

    @Test
    fun `delete card removes only selected card`() = runBlocking {
        val category = repository.createCategory(name = "Work")
        val firstCard = repository.createCard(
            categoryId = category.id,
            title = "First",
            action = CardAction.CopyText("one"),
        )
        val secondCard = repository.createCard(
            categoryId = category.id,
            title = "Second",
            action = CardAction.CopyText("two"),
        )

        deleteCard(firstCard.id)

        assertEquals(listOf(secondCard), repository.observeCards(category.id).first())
    }

    private suspend fun textCard(): ActionCard {
        val category = repository.createCategory(name = "Work")
        return repository.createCard(
            categoryId = category.id,
            title = "Reply",
            action = CardAction.CopyText("hello"),
        )
    }
}