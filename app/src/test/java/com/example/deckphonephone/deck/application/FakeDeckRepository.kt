package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import com.example.deckphonephone.deck.domain.DeckCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeDeckRepository : DeckRepository {
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

    override suspend fun updateCategory(category: DeckCategory): DeckCategory {
        categories.value = categories.value.map { existing ->
            if (existing.id == category.id) category else existing
        }
        return category
    }

    override suspend fun deleteCategory(categoryId: Long) {
        categories.value = categories.value.filterNot { it.id == categoryId }
        cards.value = cards.value.filterNot { it.categoryId == categoryId }
    }

    override suspend fun updateCard(card: ActionCard): ActionCard {
        cards.value = cards.value.map { existing ->
            if (existing.id == card.id) card else existing
        }
        return card
    }

    override suspend fun deleteCard(cardId: Long) {
        cards.value = cards.value.filterNot { it.id == cardId }
    }
}