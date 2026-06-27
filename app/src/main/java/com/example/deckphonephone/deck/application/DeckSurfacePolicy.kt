package com.example.deckphonephone.deck.application

enum class DeckSurface {
    Overlay,
    Settings,
}

enum class DeckSurfaceEntryPoint {
    Launcher,
    OverlaySettingsButton,
}

enum class DeckSurfaceEffect {
    ShowOverlay,
    ShowSettings,
    KeepSettings,
    CloseSettings,
    CloseSettingsThenShowOverlay,
}

enum class DeckSettingsExit {
    BackPressed,
    CoveredByAnotherSurface,
    ConfigurationChanging,
}

object DeckSurfacePolicy {
    fun effectForEntryPoint(
        currentSurface: DeckSurface?,
        entryPoint: DeckSurfaceEntryPoint,
    ): DeckSurfaceEffect {
        return when {
            currentSurface == DeckSurface.Settings && entryPoint == DeckSurfaceEntryPoint.Launcher -> {
                DeckSurfaceEffect.CloseSettingsThenShowOverlay
            }

            else -> effectForEntryPoint(entryPoint)
        }
    }

    fun effectForEntryPoint(entryPoint: DeckSurfaceEntryPoint): DeckSurfaceEffect {
        return when (entryPoint) {
            DeckSurfaceEntryPoint.Launcher -> DeckSurfaceEffect.ShowOverlay
            DeckSurfaceEntryPoint.OverlaySettingsButton -> DeckSurfaceEffect.ShowSettings
        }
    }

    fun effectForSettingsExit(exit: DeckSettingsExit): DeckSurfaceEffect {
        return when (exit) {
            DeckSettingsExit.BackPressed -> DeckSurfaceEffect.ShowOverlay
            DeckSettingsExit.CoveredByAnotherSurface -> DeckSurfaceEffect.CloseSettings
            DeckSettingsExit.ConfigurationChanging -> DeckSurfaceEffect.KeepSettings
        }
    }

    fun canShowTogether(first: DeckSurface, second: DeckSurface): Boolean {
        return first == second
    }
}