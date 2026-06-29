package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation
import com.example.deckphonephone.deck.domain.DeckCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeDeckRepository : DeckRepository {
    private val categories = MutableStateFlow<List<DeckCategory>>(emptyList())
    private val actionCards = MutableStateFlow<List<ActionCard>>(emptyList())
    private var nextCategoryId = 1L
    private var nextActionCardId = 1L

    override fun observeCategories(): Flow<List<DeckCategory>> = categories

    override fun observeActionCards(categoryId: Long): Flow<List<ActionCard>> {
        return actionCards.map { allCards -> allCards.filter { it.categoryId == categoryId } }
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

    override suspend fun createActionCard(
        categoryId: Long,
        title: String,
        description: String,
        operation: ActionCardOperation,
        isEnabled: Boolean,
    ): ActionCard {
        val card = ActionCard(
            id = nextActionCardId++,
            categoryId = categoryId,
            title = title,
            description = description,
            operation = operation,
            isEnabled = isEnabled,
        )
        actionCards.value = actionCards.value + card
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
        actionCards.value = actionCards.value.filterNot { it.categoryId == categoryId }
    }

    override suspend fun updateActionCard(card: ActionCard): ActionCard {
        actionCards.value = actionCards.value.map { existing ->
            if (existing.id == card.id) card else existing
        }
        return card
    }

    override suspend fun deleteActionCard(actionCardId: Long) {
        actionCards.value = actionCards.value.filterNot { it.id == actionCardId }
    }
}