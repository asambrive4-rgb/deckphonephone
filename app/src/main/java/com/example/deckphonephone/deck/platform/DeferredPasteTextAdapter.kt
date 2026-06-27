package com.example.deckphonephone.deck.platform

import com.example.deckphonephone.deck.application.PasteTextPort
import com.example.deckphonephone.deck.application.PasteTextResult

class DeferredPasteTextAdapter : PasteTextPort {
    override suspend fun pasteText(text: String): PasteTextResult {
        return PasteTextResult.Deferred
    }
}