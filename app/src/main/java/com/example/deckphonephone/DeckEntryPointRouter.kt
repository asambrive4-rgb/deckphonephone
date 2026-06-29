package com.example.deckphonephone

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deckphonephone.deck.application.DeckSettingsExit
import com.example.deckphonephone.deck.application.DeckSurface
import com.example.deckphonephone.deck.application.DeckSurfaceEffect
import com.example.deckphonephone.deck.application.DeckSurfaceEntryPoint
import com.example.deckphonephone.deck.application.DeckSurfacePolicy
import com.example.deckphonephone.deck.platform.DeckOverlayService
import com.example.deckphonephone.deck.ui.DeckOverlayPermissionScreen
import com.example.deckphonephone.deck.ui.DeckSettingRoute
import com.example.deckphonephone.deck.ui.DeckSettingViewModel
import com.example.deckphonephone.ui.theme.DeckphonephoneTheme
import com.example.deckphonephone.ui.theme.systemBarSurfaceColor

class DeckEntryPointRouter : ComponentActivity() {
    private val appContainer by lazy {
        DeckAppContainer(applicationContext)
    }
    private var currentSurface: DeckSurface? = null
    private var isWaitingForOverlayPermissionResult = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleEntryPoint(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleEntryPoint(intent)
    }

    override fun onResume() {
        super.onResume()
        if (!isWaitingForOverlayPermissionResult) return

        isWaitingForOverlayPermissionResult = false
        if (Settings.canDrawOverlays(this)) {
            launchOverlayOrShowPermissionScreen()
        } else {
            showOverlayPermissionScreen()
        }
    }

    override fun onStop() {
        super.onStop()
        if (shouldCloseSettingsAfterStop()) {
            currentSurface = null
            closeRouterTask()
        }
    }

    private fun handleEntryPoint(intent: Intent?) {
        when (DeckSurfacePolicy.effectForEntryPoint(currentSurface, intent.toSurfaceEntryPoint())) {
            DeckSurfaceEffect.ShowSettings -> showSettingScreen()
            DeckSurfaceEffect.ShowOverlay,
            DeckSurfaceEffect.CloseSettingsThenShowOverlay -> launchOverlayOrShowPermissionScreen()
            DeckSurfaceEffect.CloseSettings -> closeRouterTask()
            DeckSurfaceEffect.KeepSettings -> Unit
        }
    }

    private fun showSettingScreen() {
        isWaitingForOverlayPermissionResult = false
        currentSurface = DeckSurface.Settings
        setContent {
            val isDarkTheme by appContainer.useCases.observeDarkTheme().collectAsState()
            val colorTheme by appContainer.useCases.observeColorTheme().collectAsState()
            LaunchedEffect(isDarkTheme, colorTheme) {
                enableEdgeToEdgeForTheme(
                    isDarkTheme = isDarkTheme,
                    surfaceColor = systemBarSurfaceColor(
                        darkTheme = isDarkTheme,
                        colorTheme = colorTheme,
                    ).toArgb(),
                )
            }
            DeckphonephoneTheme(
                darkTheme = isDarkTheme,
                colorTheme = colorTheme,
            ) {
                val viewModel = viewModel<DeckSettingViewModel>(
                    factory = DeckSettingViewModel.Factory(appContainer.useCases),
                )
                DeckSettingRoute(
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
                launchOverlayOrShowPermissionScreen()
            }

            DeckSurfaceEffect.ShowSettings,
            DeckSurfaceEffect.KeepSettings,
            DeckSurfaceEffect.CloseSettings,
            DeckSurfaceEffect.CloseSettingsThenShowOverlay -> Unit
        }
    }

    private fun launchOverlayOrShowPermissionScreen() {
        currentSurface = DeckSurface.Overlay
        if (Settings.canDrawOverlays(this)) {
            DeckOverlayService.start(this)
            currentSurface = null
            closeRouterTask()
        } else {
            showOverlayPermissionScreen()
        }
    }

    private fun showOverlayPermissionScreen() {
        currentSurface = null
        setContent {
            val isDarkTheme by appContainer.useCases.observeDarkTheme().collectAsState()
            val colorTheme by appContainer.useCases.observeColorTheme().collectAsState()
            LaunchedEffect(isDarkTheme, colorTheme) {
                enableEdgeToEdgeForTheme(
                    isDarkTheme = isDarkTheme,
                    surfaceColor = systemBarSurfaceColor(
                        darkTheme = isDarkTheme,
                        colorTheme = colorTheme,
                    ).toArgb(),
                )
            }
            DeckphonephoneTheme(
                darkTheme = isDarkTheme,
                colorTheme = colorTheme,
            ) {
                DeckOverlayPermissionScreen(
                    onOpenPermissionSettings = ::openOverlayPermissionSettings,
                    onClose = ::closeRouterTask,
                )
            }
        }
    }

    private fun openOverlayPermissionSettings() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName"),
        )
        isWaitingForOverlayPermissionResult = true
        runCatching {
            startActivity(intent)
        }.onFailure {
            isWaitingForOverlayPermissionResult = false
            Toast.makeText(this, "권한 설정 화면을 열지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableEdgeToEdgeForTheme(
        isDarkTheme: Boolean,
        surfaceColor: Int,
    ) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = surfaceColor,
                darkScrim = surfaceColor,
                detectDarkMode = { isDarkTheme },
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = surfaceColor,
                darkScrim = surfaceColor,
                detectDarkMode = { isDarkTheme },
            ),
        )
    }

    private fun closeRouterTask() {
        finishAndRemoveTask()
        disableTransitionAnimation()
    }

    @Suppress("DEPRECATION")
    private fun disableTransitionAnimation() {
        overridePendingTransition(0, 0)
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
