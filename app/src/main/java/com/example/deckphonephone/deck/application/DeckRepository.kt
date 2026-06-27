package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import com.example.deckphonephone.deck.domain.DeckCategory
import kotlinx.coroutines.flow.Flow

interface DeckRepository {
    fun observeCategories(): Flow<List<DeckCategory>>

    fun observeCards(categoryId: Long): Flow<List<ActionCard>>

    suspend fun createCategory(
        name: String,
        description: String = "",
        isEnabled: Boolean = true,
    ): DeckCategory

    suspend fun createCard(
        categoryId: Long,
        title: String,
        description: String = "",
        action: CardAction,
        isEnabled: Boolean = true,
    ): ActionCard
}
