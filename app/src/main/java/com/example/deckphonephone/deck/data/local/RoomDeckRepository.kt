package com.example.deckphonephone.deck.data.local

import com.example.deckphonephone.deck.application.DeckRepository
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import com.example.deckphonephone.deck.domain.DeckCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDeckRepository(
    private val dao: DeckDao,
) : DeckRepository {
    override fun observeCategories(): Flow<List<DeckCategory>> {
        return dao.observeCategories().map { categories ->
            categories.map { it.toDomain() }
        }
    }

    override fun observeCards(categoryId: Long): Flow<List<ActionCard>> {
        return dao.observeCards(categoryId).map { cards ->
            cards.map { it.toDomain() }
        }
    }

    override suspend fun createCategory(
        name: String,
        description: String,
        isEnabled: Boolean,
    ): DeckCategory {
        val id = dao.insertCategory(
            newCategoryEntity(
                name = name,
                description = description,
                isEnabled = isEnabled,
            ),
        )
        return requireNotNull(dao.getCategory(id)) {
            "Inserted category was not found."
        }.toDomain()
    }

    override suspend fun createCard(
        categoryId: Long,
        title: String,
        description: String,
        action: CardAction,
        isEnabled: Boolean,
    ): ActionCard {
        val id = dao.insertCard(
            newCardEntity(
                categoryId = categoryId,
                title = title,
                description = description,
                action = action,
                isEnabled = isEnabled,
            ),
        )
        return requireNotNull(dao.getCard(id)) {
            "Inserted card was not found."
        }.toDomain()
    }
}
