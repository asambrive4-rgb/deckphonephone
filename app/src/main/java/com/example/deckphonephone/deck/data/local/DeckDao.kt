package com.example.deckphonephone.deck.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM action_cards WHERE category_id = :categoryId ORDER BY id ASC")
    fun observeCards(categoryId: Long): Flow<List<ActionCardEntity>>

    @Insert
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert
    suspend fun insertCard(card: ActionCardEntity): Long

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategory(id: Long): CategoryEntity?

    @Query("SELECT * FROM action_cards WHERE id = :id")
    suspend fun getCard(id: Long): ActionCardEntity?
}
