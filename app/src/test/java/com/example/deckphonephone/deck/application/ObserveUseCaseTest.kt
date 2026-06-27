package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import com.example.deckphonephone.deck.domain.DeckCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveUseCaseTest {
    @Test
    fun `created category and card can be observed`() = runBlocking {
        val repository = ObservableFakeDeckRepository()
        val createCategory = CreateCategoryUseCase(repository)
        val createTextCard = CreateTextCardUseCase(repository)
        val observeCategories = ObserveCategoriesUseCase(repository)
        val observeCards = ObserveCardsUseCase(repository)

        val category = (createCategory("AI 프롬프트") as DeckResult.Success).value
        createTextCard(category.id, "요약", "다음 내용을 요약해줘")

        assertEquals(listOf(category), observeCategories().first())
        assertEquals(1, observeCards(category.id).first().size)
    }
}

private class ObservableFakeDeckRepository : DeckRepository {
    private val categories = MutableStateFlow<List<DeckCategory>>(emptyList())
    private val cards = MutableStateFlow<List<ActionCard>>(emptyList())
    private var nextCategoryId = 1L
    private var nextCardId = 1L

    override fun observeCategories(): Flow<List<DeckCategory>> = categories

    override fun observeCards(categoryId: Long): Flow<List<ActionCard>> {
        return cards.map { allCards -> allCards.filter { it.categoryId == categoryId } }
    }

    override suspend fun createCategory(
        name: String,
        description: String,
        isEnabled: Boolean,
    ): DeckCategory {
        val category = DeckCategory(
            id = nextCategoryId++,
            name = name,
            description = description,
            isEnabled = isEnabled,
        )
        categories.value = categories.value + category
        return category
    }

    override suspend fun createCard(
        categoryId: Long,
        title: String,
        description: String,
        action: CardAction,
        isEnabled: Boolean,
    ): ActionCard {
        val card = ActionCard(
            id = nextCardId++,
            categoryId = categoryId,
            title = title,
            description = description,
            action = action,
            isEnabled = isEnabled,
        )
        cards.value = cards.value + card
        return card
    }
}
