package com.example.deckphonephone.deck.application

sealed interface ExecuteCardResult {
    data object OpenedUrl : ExecuteCardResult
    data object CopiedText : ExecuteCardResult
    data object DisabledCard : ExecuteCardResult
    data object CopyTextBlank : ExecuteCardResult
    data object OpenUrlFailed : ExecuteCardResult
    data object CopyTextFailed : ExecuteCardResult
}
