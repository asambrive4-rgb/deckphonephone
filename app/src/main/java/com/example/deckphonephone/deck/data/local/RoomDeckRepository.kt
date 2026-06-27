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

    override suspend fun updateCategory(category: DeckCategory): DeckCategory {
        val existing = requireNotNull(dao.getCategory(category.id)) {
            "Category was not found: ${category.id}"
        }
        dao.updateCategory(
            existing.copy(
                name = category.name,
                description = category.description,
                isEnabled = category.isEnabled,
            ),
        )
        return requireNotNull(dao.getCategory(category.id)) {
            "Updated category was not found: ${category.id}"
        }.toDomain()
    }

    override suspend fun deleteCategory(categoryId: Long) {
        dao.deleteCategory(categoryId)
    }

    override suspend fun updateCard(card: ActionCard): ActionCard {
        val existing = requireNotNull(dao.getCard(card.id)) {
            "Card was not found: ${card.id}"
        }
        dao.updateCard(
            card.toUpdatedEntity(existing),
        )
        return requireNotNull(dao.getCard(card.id)) {
            "Updated card was not found: ${card.id}"
        }.toDomain()
    }

    override suspend fun deleteCard(cardId: Long) {
        dao.deleteCard(cardId)
    }

    private fun ActionCard.toUpdatedEntity(existing: ActionCardEntity): ActionCardEntity {
        return when (val action = action) {
            is CardAction.CopyText -> existing.copy(
                categoryId = categoryId,
                title = title,
                description = description,
                actionType = ACTION_COPY_TEXT,
                textValue = action.text,
                urlValue = null,
                bluetoothDeviceName = null,
                bluetoothDeviceAddress = null,
                isEnabled = isEnabled,
            )

            is CardAction.OpenUrl -> existing.copy(
                categoryId = categoryId,
                title = title,
                description = description,
                actionType = ACTION_OPEN_URL,
                textValue = null,
                urlValue = action.url,
                bluetoothDeviceName = null,
                bluetoothDeviceAddress = null,
                isEnabled = isEnabled,
            )

            is CardAction.BluetoothDevice -> existing.copy(
                categoryId = categoryId,
                title = title,
                description = description,
                actionType = ACTION_BLUETOOTH_DEVICE,
                textValue = null,
                urlValue = null,
                bluetoothDeviceName = action.deviceName,
                bluetoothDeviceAddress = action.deviceAddress,
                isEnabled = isEnabled,
            )
        }
    }
}