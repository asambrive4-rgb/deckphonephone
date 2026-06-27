package com.example.deckphonephone.deck.domain

import java.net.URI

sealed interface UrlNormalizationResult {
    data class Success(val url: String) : UrlNormalizationResult
    data object Invalid : UrlNormalizationResult
}

object UrlNormalizer {
    private val whitespace = Regex("\\s")
    private val ipv4 = Regex("""^(\d{1,3}\.){3}\d{1,3}$""")

    fun normalize(rawUrl: String): UrlNormalizationResult {
        val trimmed = rawUrl.trim()
        if (trimmed.isEmpty() || whitespace.containsMatchIn(trimmed)) {
            return UrlNormalizationResult.Invalid
        }

        val candidate = if ("://" in trimmed) trimmed else "https://$trimmed"
        val uri = runCatching { URI(candidate) }.getOrNull() ?: return UrlNormalizationResult.Invalid
        val scheme = uri.scheme?.lowercase() ?: return UrlNormalizationResult.Invalid
        val host = uri.host ?: return UrlNormalizationResult.Invalid

        if (scheme != "http" && scheme != "https") {
            return UrlNormalizationResult.Invalid
        }
        if (!isAllowedHost(host)) {
            return UrlNormalizationResult.Invalid
        }

        return UrlNormalizationResult.Success(uri.toString())
    }

    private fun isAllowedHost(host: String): Boolean {
        if (host.equals("localhost", ignoreCase = true)) {
            return true
        }
        if (ipv4.matches(host)) {
            return hasValidIpv4Octets(host)
        }
        if (host.all { it.isDigit() || it == '.' }) {
            return false
        }
        if (host.contains(":")) {
            return true
        }
        return host.contains(".")
    }

    private fun hasValidIpv4Octets(host: String): Boolean {
        return host.split(".").all { octet ->
            octet.toIntOrNull()?.let { it in 0..255 } == true
        }
    }
}
