package com.example.deckphonephone.deck.data.local

import com.example.deckphonephone.deck.application.DeckRepository
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation
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

    override fun observeActionCards(categoryId: Long): Flow<List<ActionCard>> {
        return dao.observeActionCards(categoryId).map { actionCards ->
            actionCards.map { it.toDomain() }
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

    override suspend fun createActionCard(
        categoryId: Long,
        title: String,
        description: String,
        operation: ActionCardOperation,
        isEnabled: Boolean,
    ): ActionCard {
        val id = dao.insertActionCard(
            newActionCardEntity(
                categoryId = categoryId,
                title = title,
                description = description,
                operation = operation,
                isEnabled = isEnabled,
            ),
        )
        return requireNotNull(dao.getActionCard(id)) {
            "Inserted action card was not found."
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

    override suspend fun updateActionCard(card: ActionCard): ActionCard {
        val existing = requireNotNull(dao.getActionCard(card.id)) {
            "Action card was not found: ${card.id}"
        }
        dao.updateActionCard(
            card.toUpdatedEntity(existing),
        )
        return requireNotNull(dao.getActionCard(card.id)) {
            "Updated action card was not found: ${card.id}"
        }.toDomain()
    }

    override suspend fun deleteActionCard(actionCardId: Long) {
        dao.deleteActionCard(actionCardId)
    }

    private fun ActionCard.toUpdatedEntity(existing: ActionCardEntity): ActionCardEntity {
        return when (val operation = operation) {
            is ActionCardOperation.CopyText -> existing.copy(
                categoryId = categoryId,
                title = title,
                description = description,
                operationType = OPERATION_COPY_TEXT,
                textValue = operation.text,
                urlValue = null,
                bluetoothDeviceName = null,
                bluetoothDeviceAddress = null,
                isEnabled = isEnabled,
            )

            is ActionCardOperation.OpenUrl -> existing.copy(
                categoryId = categoryId,
                title = title,
                description = description,
                operationType = OPERATION_OPEN_URL,
                textValue = null,
                urlValue = operation.url,
                bluetoothDeviceName = null,
                bluetoothDeviceAddress = null,
                isEnabled = isEnabled,
            )

            is ActionCardOperation.BluetoothDevice -> existing.copy(
                categoryId = categoryId,
                title = title,
                description = description,
                operationType = OPERATION_BLUETOOTH_DEVICE,
                textValue = null,
                urlValue = null,
                bluetoothDeviceName = operation.deviceName,
                bluetoothDeviceAddress = operation.deviceAddress,
                isEnabled = isEnabled,
            )
        }
    }
}