package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation
import com.example.deckphonephone.deck.domain.DeckCategory
import kotlinx.coroutines.flow.Flow

interface DeckRepository {
    fun observeCategories(): Flow<List<DeckCategory>>

    fun observeActionCards(categoryId: Long): Flow<List<ActionCard>>

    suspend fun createCategory(
        name: String,
        description: String = "",
        isEnabled: Boolean = true,
    ): DeckCategory

    suspend fun createActionCard(
        categoryId: Long,
        title: String,
        description: String = "",
        operation: ActionCardOperation,
        isEnabled: Boolean = true,
    ): ActionCard

    suspend fun updateCategory(category: DeckCategory): DeckCategory

    suspend fun deleteCategory(categoryId: Long)

    suspend fun updateActionCard(card: ActionCard): ActionCard

    suspend fun deleteActionCard(actionCardId: Long)
}