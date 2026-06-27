package com.example.deckphonephone.deck.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CategoryEntity::class,
        ActionCardEntity::class,
    ],
    version = 2,
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
                ).addMigrations(MIGRATION_1_2).build().also { instance = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE action_cards ADD COLUMN bluetooth_device_name TEXT")
                db.execSQL("ALTER TABLE action_cards ADD COLUMN bluetooth_device_address TEXT")
            }
        }
    }
}