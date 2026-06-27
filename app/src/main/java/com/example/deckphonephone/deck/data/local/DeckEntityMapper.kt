package com.example.deckphonephone.deck.data.local

import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import com.example.deckphonephone.deck.domain.DeckCategory

private const val ACTION_TEXT_PASTE = "text_paste"
private const val ACTION_OPEN_URL = "open_url"

fun CategoryEntity.toDomain() = DeckCategory(
    id = id,
    name = name,
    description = description,
    isEnabled = isEnabled,
)

fun ActionCardEntity.toDomain() = ActionCard(
    id = id,
    categoryId = categoryId,
    title = title,
    description = description,
    action = when (actionType) {
        ACTION_TEXT_PASTE -> CardAction.TextPaste(textValue.orEmpty())
        ACTION_OPEN_URL -> CardAction.OpenUrl(urlValue.orEmpty())
        else -> error("Unknown card action type: $actionType")
    },
    isEnabled = isEnabled,
)

fun newCategoryEntity(
    name: String,
    description: String,
    isEnabled: Boolean,
) = CategoryEntity(
    name = name,
    description = description,
    isEnabled = isEnabled,
)

fun newCardEntity(
    categoryId: Long,
    title: String,
    description: String,
    action: CardAction,
    isEnabled: Boolean,
) = when (action) {
    is CardAction.TextPaste -> ActionCardEntity(
        categoryId = categoryId,
        title = title,
        description = description,
        actionType = ACTION_TEXT_PASTE,
        textValue = action.text,
        isEnabled = isEnabled,
    )

    is CardAction.OpenUrl -> ActionCardEntity(
        categoryId = categoryId,
        title = title,
        description = description,
        actionType = ACTION_OPEN_URL,
        urlValue = action.url,
        isEnabled = isEnabled,
    )
}
