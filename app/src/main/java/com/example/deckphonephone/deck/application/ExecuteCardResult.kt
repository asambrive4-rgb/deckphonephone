package com.example.deckphonephone.deck.application

sealed interface ExecuteCardResult {
    data object OpenedUrl : ExecuteCardResult
    data object PastedText : ExecuteCardResult
    data object PasteTextDeferred : ExecuteCardResult
    data object DisabledCard : ExecuteCardResult
    data object OpenUrlFailed : ExecuteCardResult
    data object PasteTextFailed : ExecuteCardResult
}