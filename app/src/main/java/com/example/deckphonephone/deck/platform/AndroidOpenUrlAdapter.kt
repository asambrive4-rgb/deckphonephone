package com.example.deckphonephone.deck.platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.deckphonephone.deck.application.OpenUrlPort
import com.example.deckphonephone.deck.application.OpenUrlResult

class AndroidOpenUrlAdapter(
    private val context: Context,
) : OpenUrlPort {
    override suspend fun openUrl(url: String): OpenUrlResult {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            OpenUrlResult.Success
        } catch (error: ActivityNotFoundException) {
            OpenUrlResult.Failure
        } catch (error: RuntimeException) {
            OpenUrlResult.Failure
        }
    }
}