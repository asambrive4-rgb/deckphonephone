@file:Suppress("DEPRECATION")

package com.example.deckphonephone.deck.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.deckphonephone.DeckAppContainer
import com.example.deckphonephone.DeckEntryPointRouter
import com.example.deckphonephone.deck.application.DeckSurface
import com.example.deckphonephone.deck.application.DeckSurfacePolicy
import com.example.deckphonephone.deck.ui.DeckOverlayScreen
import com.example.deckphonephone.deck.ui.DeckOverlayViewModel
import com.example.deckphonephone.ui.theme.DeckphonephoneTheme

class DeckOverlayService : LifecycleService(), SavedStateRegistryOwner {
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private val windowManager by lazy {
        getSystemService(WindowManager::class.java)
    }
    private val appContainer by lazy {
        DeckAppContainer(applicationContext)
    }
    private val closeSystemDialogsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                finishOverlay()
            }
        }
    }

    private var overlayView: View? = null
    private var overlayViewModel: DeckOverlayViewModel? = null
    private var isFinishingOverlay = false
    private var isCloseSystemDialogsReceiverRegistered = false

    override fun onCreate() {
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        super.onCreate()
        registerCloseSystemDialogsReceiver()
        showOverlay()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        unregisterCloseSystemDialogsReceiver()
        removeOverlay()
        super.onDestroy()
    }

    private fun showOverlay() {
        if (overlayView != null) return

        val viewModel = DeckOverlayViewModel(
            useCases = appContainer.useCases,
            onFinished = ::finishOverlay,
            onTransientMessage = ::showToast,
        )
        overlayViewModel = viewModel

        val rootView = DeckOverlayRootView(
            context = this,
            onBackPressed = viewModel::goBack,
        ).apply {
            setViewTreeLifecycleOwner(this@DeckOverlayService)
            setViewTreeSavedStateRegistryOwner(this@DeckOverlayService)
            isFocusable = true
            isFocusableInTouchMode = true
        }

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@DeckOverlayService)
            setViewTreeSavedStateRegistryOwner(this@DeckOverlayService)
            setContent {
                DeckphonephoneTheme {
                    DeckOverlayScreen(
                        viewModel = viewModel,
                        onSettingsClicked = ::openSettings,
                    )
                }
            }
        }
        rootView.addView(
            composeView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        windowManager.addView(rootView, params)
        overlayView = rootView
        rootView.post { rootView.requestFocus() }
    }

    private fun openSettings() {
        if (isFinishingOverlay) return
        isFinishingOverlay = true
        if (!DeckSurfacePolicy.canShowTogether(DeckSurface.Overlay, DeckSurface.Settings)) {
            removeOverlay()
        }
        val intent = DeckEntryPointRouter.createSettingIntent(this).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        stopSelf()
    }

    private fun finishOverlay() {
        if (isFinishingOverlay) return
        isFinishingOverlay = true
        removeOverlay()
        stopSelf()
    }

    private fun removeOverlay() {
        overlayViewModel?.clear()
        overlayViewModel = null

        val view = overlayView ?: return
        overlayView = null
        runCatching {
            windowManager.removeView(view)
        }
    }

    private fun registerCloseSystemDialogsReceiver() {
        if (isCloseSystemDialogsReceiverRegistered) return
        val filter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(closeSystemDialogsReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(closeSystemDialogsReceiver, filter)
        }
        isCloseSystemDialogsReceiverRegistered = true
    }

    private fun unregisterCloseSystemDialogsReceiver() {
        if (!isCloseSystemDialogsReceiverRegistered) return
        runCatching {
            unregisterReceiver(closeSystemDialogsReceiver)
        }
        isCloseSystemDialogsReceiverRegistered = false
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private class DeckOverlayRootView(
        context: Context,
        private val onBackPressed: () -> Unit,
    ) : FrameLayout(context) {
        override fun dispatchKeyEvent(event: KeyEvent): Boolean {
            if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                if (event.action == KeyEvent.ACTION_UP) {
                    onBackPressed()
                }
                return true
            }
            return super.dispatchKeyEvent(event)
        }
    }

    companion object {
        fun start(context: Context) {
            context.startService(Intent(context, DeckOverlayService::class.java))
        }
    }
}