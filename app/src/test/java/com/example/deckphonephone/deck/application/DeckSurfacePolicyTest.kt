package com.example.deckphonephone.deck.application

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class DeckSurfacePolicyTest {
    @Test
    fun `launcher entry shows overlay`() {
        val effect = DeckSurfacePolicy.effectForEntryPoint(DeckSurfaceEntryPoint.Launcher)

        assertEquals(DeckSurfaceEffect.ShowOverlay, effect)
    }

    @Test
    fun `launcher entry while settings is visible closes settings before showing overlay`() {
        val effect = DeckSurfacePolicy.effectForEntryPoint(
            currentSurface = DeckSurface.Settings,
            entryPoint = DeckSurfaceEntryPoint.Launcher,
        )

        assertEquals(DeckSurfaceEffect.CloseSettingsThenShowOverlay, effect)
    }

    @Test
    fun `settings screen opens only from overlay settings button`() {
        val effect = DeckSurfacePolicy.effectForEntryPoint(DeckSurfaceEntryPoint.OverlaySettingsButton)

        assertEquals(DeckSurfaceEffect.ShowSettings, effect)
    }

    @Test
    fun `settings back returns to overlay`() {
        val effect = DeckSurfacePolicy.effectForSettingsExit(DeckSettingsExit.BackPressed)

        assertEquals(DeckSurfaceEffect.ShowOverlay, effect)
    }

    @Test
    fun `settings covered by another surface closes without showing overlay`() {
        val effect = DeckSurfacePolicy.effectForSettingsExit(DeckSettingsExit.CoveredByAnotherSurface)

        assertEquals(DeckSurfaceEffect.CloseSettings, effect)
    }

    @Test
    fun `settings stays during configuration change`() {
        val effect = DeckSurfacePolicy.effectForSettingsExit(DeckSettingsExit.ConfigurationChanging)

        assertEquals(DeckSurfaceEffect.KeepSettings, effect)
    }

    @Test
    fun `overlay and settings cannot be shown together`() {
        val canShowTogether = DeckSurfacePolicy.canShowTogether(
            DeckSurface.Overlay,
            DeckSurface.Settings,
        )

        assertFalse(canShowTogether)
    }
}