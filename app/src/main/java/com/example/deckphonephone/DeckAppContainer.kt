package com.example.deckphonephone

import android.content.Context
import com.example.deckphonephone.deck.application.CreateBluetoothDeviceActionCardUseCase
import com.example.deckphonephone.deck.application.CreateCategoryUseCase
import com.example.deckphonephone.deck.application.CreateTextActionCardUseCase
import com.example.deckphonephone.deck.application.CreateWebActionCardUseCase
import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.application.DeleteActionCardUseCase
import com.example.deckphonephone.deck.application.DeleteCategoryUseCase
import com.example.deckphonephone.deck.application.ExecuteActionCardUseCase
import com.example.deckphonephone.deck.application.ListPairedBluetoothDevicesUseCase
import com.example.deckphonephone.deck.application.ObserveConnectedBluetoothDevicesUseCase
import com.example.deckphonephone.deck.application.ObserveActionCardsUseCase
import com.example.deckphonephone.deck.application.ObserveCategoriesUseCase
import com.example.deckphonephone.deck.application.ObserveDarkThemeUseCase
import com.example.deckphonephone.deck.application.ObserveOverlayHandPreferenceUseCase
import com.example.deckphonephone.deck.application.SetActionCardEnabledUseCase
import com.example.deckphonephone.deck.application.SetDarkThemeUseCase
import com.example.deckphonephone.deck.application.SetOverlayHandPreferenceUseCase
import com.example.deckphonephone.deck.application.UpdateBluetoothDeviceActionCardUseCase
import com.example.deckphonephone.deck.application.UpdateCategoryUseCase
import com.example.deckphonephone.deck.application.UpdateTextActionCardUseCase
import com.example.deckphonephone.deck.application.UpdateWebActionCardUseCase
import com.example.deckphonephone.deck.data.local.DeckDatabase
import com.example.deckphonephone.deck.data.local.RoomDeckRepository
import com.example.deckphonephone.deck.data.local.SharedPreferencesAppPreferenceRepository
import com.example.deckphonephone.deck.platform.AndroidBluetoothDeviceActionAdapter
import com.example.deckphonephone.deck.platform.AndroidClipboardCopyTextAdapter
import com.example.deckphonephone.deck.platform.AndroidConnectedBluetoothDevicesAdapter
import com.example.deckphonephone.deck.platform.AndroidOpenUrlAdapter
import com.example.deckphonephone.deck.platform.AndroidPairedBluetoothDevicesAdapter

class DeckAppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = DeckDatabase.get(appContext)
    private val repository = RoomDeckRepository(database.deckDao())
    private val appPreferenceRepository = SharedPreferencesAppPreferenceRepository(appContext)
    private val openUrlAdapter = AndroidOpenUrlAdapter(appContext)
    private val copyTextAdapter = AndroidClipboardCopyTextAdapter(appContext)
    private val pairedBluetoothDevicesAdapter = AndroidPairedBluetoothDevicesAdapter(appContext)
    private val connectedBluetoothDevicesAdapter = AndroidConnectedBluetoothDevicesAdapter(appContext)
    private val bluetoothDeviceActionAdapter = AndroidBluetoothDeviceActionAdapter(appContext)

    val useCases = DeckUseCases(
        createCategory = CreateCategoryUseCase(repository),
        observeCategories = ObserveCategoriesUseCase(repository),
        updateCategory = UpdateCategoryUseCase(repository),
        deleteCategory = DeleteCategoryUseCase(repository),
        createTextActionCard = CreateTextActionCardUseCase(repository),
        createWebActionCard = CreateWebActionCardUseCase(repository),
        createBluetoothDeviceActionCard = CreateBluetoothDeviceActionCardUseCase(repository),
        listPairedBluetoothDevices = ListPairedBluetoothDevicesUseCase(pairedBluetoothDevicesAdapter),
        observeConnectedBluetoothDevices = ObserveConnectedBluetoothDevicesUseCase(
            connectedBluetoothDevicesAdapter,
        ),
        observeActionCards = ObserveActionCardsUseCase(repository),
        updateTextActionCard = UpdateTextActionCardUseCase(repository),
        updateWebActionCard = UpdateWebActionCardUseCase(repository),
        updateBluetoothDeviceActionCard = UpdateBluetoothDeviceActionCardUseCase(repository),
        deleteActionCard = DeleteActionCardUseCase(repository),
        setActionCardEnabled = SetActionCardEnabledUseCase(repository),
        executeActionCard = ExecuteActionCardUseCase(
            openUrlPort = openUrlAdapter,
            copyTextPort = copyTextAdapter,
            bluetoothDeviceActionPort = bluetoothDeviceActionAdapter,
        ),
        observeDarkTheme = ObserveDarkThemeUseCase(appPreferenceRepository),
        setDarkTheme = SetDarkThemeUseCase(appPreferenceRepository),
        observeOverlayHandPreference = ObserveOverlayHandPreferenceUseCase(appPreferenceRepository),
        setOverlayHandPreference = SetOverlayHandPreferenceUseCase(appPreferenceRepository),
    )
}
