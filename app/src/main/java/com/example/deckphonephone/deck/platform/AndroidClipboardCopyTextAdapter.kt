package com.example.deckphonephone.deck.platform

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.example.deckphonephone.deck.application.CopyTextPort
import com.example.deckphonephone.deck.application.CopyTextResult

class AndroidClipboardCopyTextAdapter(
    private val context: Context,
) : CopyTextPort {
    override suspend fun copyText(text: String): CopyTextResult {
        return try {
            val clipboard = context.getSystemService(ClipboardManager::class.java)
                ?: return CopyTextResult.Failure
            clipboard.setPrimaryClip(ClipData.newPlainText(CLIP_LABEL, text))
            CopyTextResult.Success
        } catch (error: RuntimeException) {
            CopyTextResult.Failure
        }
    }

    private companion object {
        const val CLIP_LABEL = "DeckDeckDeck"
    }
}