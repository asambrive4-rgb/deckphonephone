package com.example.deckphonephone.deck.data.local

import com.example.deckphonephone.deck.domain.CardAction
import org.junit.Assert.assertEquals
import org.junit.Test

class DeckEntityMapperTest {
    @Test
    fun `copy text action is stored with copy text type`() {
        val entity = newCardEntity(
            categoryId = 1,
            title = "Reply",
            description = "",
            action = CardAction.CopyText("hello"),
            isEnabled = true,
        )

        assertEquals("copy_text", entity.actionType)
        assertEquals("hello", entity.textValue)
    }

    @Test
    fun `legacy text paste action is read as copy text`() {
        val entity = ActionCardEntity(
            id = 1,
            categoryId = 1,
            title = "Legacy",
            description = "",
            actionType = "text_paste",
            textValue = "hello",
            isEnabled = true,
        )

        assertEquals(CardAction.CopyText("hello"), entity.toDomain().action)
    }
}