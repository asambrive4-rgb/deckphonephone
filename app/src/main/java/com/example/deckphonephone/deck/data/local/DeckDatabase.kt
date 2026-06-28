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
    version = 4,
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
                ).addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                ).build().also { instance = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE action_cards ADD COLUMN bluetooth_device_name TEXT")
                db.execSQL("ALTER TABLE action_cards ADD COLUMN bluetooth_device_address TEXT")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE action_cards ADD COLUMN routine_steps TEXT")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE action_cards_without_routine_steps (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        category_id INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        action_type TEXT NOT NULL,
                        text_value TEXT,
                        url_value TEXT,
                        bluetooth_device_name TEXT,
                        bluetooth_device_address TEXT,
                        is_enabled INTEGER NOT NULL,
                        created_at INTEGER NOT NULL,
                        FOREIGN KEY(category_id) REFERENCES categories(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    INSERT INTO action_cards_without_routine_steps (
                        id,
                        category_id,
                        title,
                        description,
                        action_type,
                        text_value,
                        url_value,
                        bluetooth_device_name,
                        bluetooth_device_address,
                        is_enabled,
                        created_at
                    )
                    SELECT
                        id,
                        category_id,
                        title,
                        description,
                        action_type,
                        text_value,
                        url_value,
                        bluetooth_device_name,
                        bluetooth_device_address,
                        is_enabled,
                        created_at
                    FROM action_cards
                    """.trimIndent(),
                )
                db.execSQL("DROP TABLE action_cards")
                db.execSQL("ALTER TABLE action_cards_without_routine_steps RENAME TO action_cards")
                db.execSQL("CREATE INDEX index_action_cards_category_id ON action_cards(category_id)")
            }
        }
    }
}