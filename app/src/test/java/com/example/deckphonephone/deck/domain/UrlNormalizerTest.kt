package com.example.deckphonephone.deck.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlNormalizerTest {
    @Test
    fun `adds https when scheme is missing`() {
        val result = UrlNormalizer.normalize("google.com")

        assertEquals(UrlNormalizationResult.Success("https://google.com"), result)
    }

    @Test
    fun `keeps existing https url`() {
        val result = UrlNormalizer.normalize("https://google.com")

        assertEquals(UrlNormalizationResult.Success("https://google.com"), result)
    }

    @Test
    fun `allows localhost with http`() {
        val result = UrlNormalizer.normalize("http://localhost:3000")

        assertEquals(UrlNormalizationResult.Success("http://localhost:3000"), result)
    }

    @Test
    fun `allows ipv4 address`() {
        val result = UrlNormalizer.normalize("192.168.0.1")

        assertEquals(UrlNormalizationResult.Success("https://192.168.0.1"), result)
    }

    @Test
    fun `rejects whitespace inside url`() {
        val result = UrlNormalizer.normalize("hello world.com")

        assertTrue(result is UrlNormalizationResult.Invalid)
    }

    @Test
    fun `rejects unsupported scheme`() {
        val result = UrlNormalizer.normalize("ftp://example.com")

        assertTrue(result is UrlNormalizationResult.Invalid)
    }

    @Test
    fun `rejects domain without dot`() {
        val result = UrlNormalizer.normalize("example")

        assertTrue(result is UrlNormalizationResult.Invalid)
    }
}
