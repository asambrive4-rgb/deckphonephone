package com.example.deckphonephone.deck.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "action_cards",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["category_id"])],
)
data class ActionCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    val title: String,
    val description: String = "",
    @ColumnInfo(name = "action_type")
    val actionType: String,
    @ColumnInfo(name = "text_value")
    val textValue: String? = null,
    @ColumnInfo(name = "url_value")
    val urlValue: String? = null,
    @ColumnInfo(name = "bluetooth_device_name")
    val bluetoothDeviceName: String? = null,
    @ColumnInfo(name = "bluetooth_device_address")
    val bluetoothDeviceAddress: String? = null,
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)