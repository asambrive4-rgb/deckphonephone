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
import com.example.deckphonephone.deck.platform.DeckOverlayService
import com.example.deckphonephone.deck.ui.DeckSettingScreen
import com.example.deckphonephone.deck.ui.DeckSettingViewModel
import com.example.deckphonephone.ui.theme.DeckphonephoneTheme

class MainActivity : ComponentActivity() {
    private val appContainer by lazy {
        DeckAppContainer(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.action == ACTION_OPEN_SETTING) {
            showSettingScreen()
        } else {
            launchOverlayOrPermissionSettings()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.action == ACTION_OPEN_SETTING) {
            showSettingScreen()
        } else {
            launchOverlayOrPermissionSettings()
        }
    }

    private fun showSettingScreen() {
        enableEdgeToEdge()
        setContent {
            DeckphonephoneTheme {
                val viewModel = viewModel<DeckSettingViewModel>(
                    factory = DeckSettingViewModel.Factory(appContainer.useCases),
                )
                DeckSettingScreen(
                    viewModel = viewModel,
                    onExit = ::launchOverlayOrPermissionSettings,
                )
            }
        }
    }

    private fun launchOverlayOrPermissionSettings() {
        if (Settings.canDrawOverlays(this)) {
            DeckOverlayService.start(this)
            finishAndRemoveTask()
        } else {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"),
            )
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val ACTION_OPEN_SETTING = "com.example.deckphonephone.action.OPEN_SETTING"

        fun createSettingIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java).apply {
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