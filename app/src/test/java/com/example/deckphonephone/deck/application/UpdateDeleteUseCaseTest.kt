package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation
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
    private val updateTextActionCard = UpdateTextActionCardUseCase(repository)
    private val updateWebActionCard = UpdateWebActionCardUseCase(repository)
    private val deleteActionCard = DeleteActionCardUseCase(repository)
    private val setActionCardEnabled = SetActionCardEnabledUseCase(repository)

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

        val result = updateTextActionCard(card = card, title = " ", text = "hello")

        assertEquals(DeckResult.Failure(DeckError.ActionCardTitleBlank), result)
    }

    @Test
    fun `text card update requires payload`() = runBlocking {
        val card = textCard()

        val result = updateTextActionCard(card = card, title = "Reply", text = " ")

        assertEquals(DeckResult.Failure(DeckError.TextBlank), result)
    }

    @Test
    fun `web card update stores normalized url`() = runBlocking {
        val card = textCard()

        val result = updateWebActionCard(card = card, title = "Search", rawUrl = "google.com")

        assertTrue(result is DeckResult.Success)
        val updatedCard = (result as DeckResult.Success).value
        assertEquals(ActionCardOperation.OpenUrl("https://google.com"), updatedCard.operation)
    }

    @Test
    fun `web card update rejects invalid url`() = runBlocking {
        val card = textCard()

        val result = updateWebActionCard(card = card, title = "Search", rawUrl = "ftp://example.com")

        assertEquals(DeckResult.Failure(DeckError.InvalidUrl), result)
    }

    @Test
    fun `card enabled state can be changed`() = runBlocking {
        val card = textCard()

        val updatedCard = setActionCardEnabled(card = card, isEnabled = false)

        assertFalse(updatedCard.isEnabled)
        assertFalse(repository.observeActionCards(card.categoryId).first().single().isEnabled)
    }

    @Test
    fun `delete category removes its actionCards`() = runBlocking {
        val category = repository.createCategory(name = "Work")
        repository.createActionCard(
            categoryId = category.id,
            title = "Reply",
            operation = ActionCardOperation.CopyText("hello"),
        )

        deleteCategory(category.id)

        assertEquals(emptyList<ActionCard>(), repository.observeActionCards(category.id).first())
        assertTrue(repository.observeCategories().first().isEmpty())
    }

    @Test
    fun `delete card removes only selected card`() = runBlocking {
        val category = repository.createCategory(name = "Work")
        val firstCard = repository.createActionCard(
            categoryId = category.id,
            title = "First",
            operation = ActionCardOperation.CopyText("one"),
        )
        val secondCard = repository.createActionCard(
            categoryId = category.id,
            title = "Second",
            operation = ActionCardOperation.CopyText("two"),
        )

        deleteActionCard(firstCard.id)

        assertEquals(listOf(secondCard), repository.observeActionCards(category.id).first())
    }

    private suspend fun textCard(): ActionCard {
        val category = repository.createCategory(name = "Work")
        return repository.createActionCard(
            categoryId = category.id,
            title = "Reply",
            operation = ActionCardOperation.CopyText("hello"),
        )
    }
}