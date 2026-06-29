package com.example.deckphonephone.deck.application

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveUseCaseTest {
    @Test
    fun `created category and card can be observed`() = runBlocking {
        val repository = FakeDeckRepository()
        val createCategory = CreateCategoryUseCase(repository)
        val createTextActionCard = CreateTextActionCardUseCase(repository)
        val observeCategories = ObserveCategoriesUseCase(repository)
        val observeActionCards = ObserveActionCardsUseCase(repository)

        val category = (createCategory("AI 프롬프트") as DeckResult.Success).value
        createTextActionCard(category.id, "요약", "다음 내용을 요약해줘")

        assertEquals(listOf(category), observeCategories().first())
        assertEquals(1, observeActionCards(category.id).first().size)
    }
}