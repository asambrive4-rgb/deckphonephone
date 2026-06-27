package com.example.deckphonephone

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deckphonephone.deck.application.DeckSettingsExit
import com.example.deckphonephone.deck.application.DeckSurface
import com.example.deckphonephone.deck.application.DeckSurfaceEffect
import com.example.deckphonephone.deck.application.DeckSurfaceEntryPoint
import com.example.deckphonephone.deck.application.DeckSurfacePolicy
import com.example.deckphonephone.deck.platform.DeckOverlayService
import com.example.deckphonephone.deck.ui.DeckSettingScreen
import com.example.deckphonephone.deck.ui.DeckSettingViewModel
import com.example.deckphonephone.ui.theme.DeckphonephoneTheme

class DeckEntryPointRouter : ComponentActivity() {
    private val appContainer by lazy {
        DeckAppContainer(applicationContext)
    }
    private var currentSurface: DeckSurface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleEntryPoint(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleEntryPoint(intent)
    }

    override fun onStop() {
        super.onStop()
        if (shouldCloseSettingsAfterStop()) {
            currentSurface = null
            finishAndRemoveTask()
        }
    }

    private fun handleEntryPoint(intent: Intent?) {
        when (DeckSurfacePolicy.effectForEntryPoint(intent.toSurfaceEntryPoint())) {
            DeckSurfaceEffect.ShowSettings -> showSettingScreen()
            DeckSurfaceEffect.ShowOverlay -> launchOverlayOrPermissionSettings()
            DeckSurfaceEffect.KeepSettings,
            DeckSurfaceEffect.CloseSettings,
            DeckSurfaceEffect.CloseSettingsThenShowOverlay -> Unit
        }
    }

    private fun showSettingScreen() {
        currentSurface = DeckSurface.Settings
        enableEdgeToEdge()
        setContent {
            DeckphonephoneTheme {
                val viewModel = viewModel<DeckSettingViewModel>(
                    factory = DeckSettingViewModel.Factory(appContainer.useCases),
                )
                DeckSettingScreen(
                    viewModel = viewModel,
                    onExit = ::exitSettingScreen,
                )
            }
        }
    }

    private fun exitSettingScreen() {
        when (DeckSurfacePolicy.effectForSettingsExit(DeckSettingsExit.BackPressed)) {
            DeckSurfaceEffect.ShowOverlay -> {
                currentSurface = null
                launchOverlayOrPermissionSettings()
            }

            DeckSurfaceEffect.ShowSettings,
            DeckSurfaceEffect.KeepSettings,
            DeckSurfaceEffect.CloseSettings,
            DeckSurfaceEffect.CloseSettingsThenShowOverlay -> Unit
        }
    }

    private fun launchOverlayOrPermissionSettings() {
        currentSurface = DeckSurface.Overlay
        if (Settings.canDrawOverlays(this)) {
            DeckOverlayService.start(this)
            currentSurface = null
            finishAndRemoveTask()
        } else {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"),
            )
            startActivity(intent)
            currentSurface = null
            finish()
        }
    }

    private fun shouldCloseSettingsAfterStop(): Boolean {
        if (isFinishing || currentSurface != DeckSurface.Settings) return false
        val exit = if (isChangingConfigurations) {
            DeckSettingsExit.ConfigurationChanging
        } else {
            DeckSettingsExit.CoveredByAnotherSurface
        }
        return DeckSurfacePolicy.effectForSettingsExit(exit) == DeckSurfaceEffect.CloseSettings
    }

    private fun Intent?.toSurfaceEntryPoint(): DeckSurfaceEntryPoint {
        return if (this?.action == ACTION_OPEN_SETTING) {
            DeckSurfaceEntryPoint.OverlaySettingsButton
        } else {
            DeckSurfaceEntryPoint.Launcher
        }
    }

    companion object {
        private const val ACTION_OPEN_SETTING = "com.example.deckphonephone.action.OPEN_SETTING"

        fun createSettingIntent(context: Context): Intent {
            return Intent(context, DeckEntryPointRouter::class.java).apply {
                action = ACTION_OPEN_SETTING
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeckPreview() {
    DeckphonephoneTheme {}
}
