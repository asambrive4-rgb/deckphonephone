package com.example.deckphonephone.deck.platform

import java.util.Locale

enum class BluetoothSettingsDeviceState {
    Connected,
    Disconnected,
    Unknown,
}

data class BluetoothSettingsRowCandidate<T>(
    val value: T,
    val texts: List<String>,
    val isPrimaryClickTarget: Boolean = true,
)

sealed interface BluetoothSettingsNodeMatch<out T> {
    data class Found<T>(
        val candidate: BluetoothSettingsRowCandidate<T>,
        val state: BluetoothSettingsDeviceState,
    ) : BluetoothSettingsNodeMatch<T>

    data object NotFound : BluetoothSettingsNodeMatch<Nothing>
    data object Ambiguous : BluetoothSettingsNodeMatch<Nothing>
}

object BluetoothSettingsNodeMatcher {
    fun <T> findTarget(
        candidates: List<BluetoothSettingsRowCandidate<T>>,
        deviceName: String,
        deviceAddress: String,
    ): BluetoothSettingsNodeMatch<T> {
        val normalizedAddress = normalizeAddress(deviceAddress)
        val addressMatches = if (normalizedAddress.isBlank()) {
            emptyList()
        } else {
            candidates.filter { candidate ->
                candidate.texts.any { text ->
                    normalizeAddress(text).contains(normalizedAddress)
                }
            }
        }

        resolvePreferredMatch(addressMatches)?.let { return it }
        if (addressMatches.isNotEmpty()) {
            return BluetoothSettingsNodeMatch.Ambiguous
        }

        val normalizedName = normalizeText(deviceName)
        if (normalizedName.isBlank()) {
            return BluetoothSettingsNodeMatch.NotFound
        }

        val nameMatches = candidates.filter { candidate ->
            candidate.texts.any { text ->
                val normalizedText = normalizeText(text)
                normalizedText == normalizedName || normalizedText.contains(normalizedName)
            }
        }

        return resolvePreferredMatch(nameMatches)
            ?: if (nameMatches.isEmpty()) {
                BluetoothSettingsNodeMatch.NotFound
            } else {
                BluetoothSettingsNodeMatch.Ambiguous
            }
    }

    private fun <T> BluetoothSettingsRowCandidate<T>.toMatch(): BluetoothSettingsNodeMatch<T> {
        return BluetoothSettingsNodeMatch.Found(
            candidate = this,
            state = inferState(texts),
        )
    }

    private fun <T> resolvePreferredMatch(
        matches: List<BluetoothSettingsRowCandidate<T>>,
    ): BluetoothSettingsNodeMatch<T>? {
        val primaryMatches = matches.filter { it.isPrimaryClickTarget }
        return when {
            primaryMatches.size == 1 -> primaryMatches.single().toMatch()
            primaryMatches.size > 1 -> BluetoothSettingsNodeMatch.Ambiguous
            matches.size == 1 -> matches.single().toMatch()
            else -> null
        }
    }

    fun inferState(texts: List<String>): BluetoothSettingsDeviceState {
        val normalized = texts.joinToString(separator = " ") { normalizeText(it) }
        val disconnectedTokens = listOf(
            "notconnected",
            "disconnected",
            "연결되지않음",
            "연결안됨",
            "연결끊김",
            "연결해제됨",
        )
        if (disconnectedTokens.any { normalized.contains(it) }) {
            return BluetoothSettingsDeviceState.Disconnected
        }

        val connectedTokens = listOf(
            "connected",
            "연결됨",
        )
        if (connectedTokens.any { normalized.contains(it) }) {
            return BluetoothSettingsDeviceState.Connected
        }

        return BluetoothSettingsDeviceState.Unknown
    }

    private fun normalizeText(value: String): String {
        return value.lowercase(Locale.US)
            .filterNot { it.isWhitespace() }
    }

    private fun normalizeAddress(value: String): String {
        return value.uppercase(Locale.US)
            .filter { it.isLetterOrDigit() }
    }
}
