package com.example.deckphonephone.deck.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CategoryEntity::class,
        ActionCardEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class DeckDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao

    companion object {
        @Volatile
        private var instance: DeckDatabase? = null

        fun get(context: Context): DeckDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DeckDatabase::class.java,
                    "deckdeckdeck.db",
                ).build().also { instance = it }
            }
        }
    }
}
