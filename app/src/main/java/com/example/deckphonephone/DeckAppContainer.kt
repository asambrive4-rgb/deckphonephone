package com.example.deckphonephone

import android.content.Context
import com.example.deckphonephone.deck.application.CreateBluetoothDeviceCardUseCase
import com.example.deckphonephone.deck.application.CreateCategoryUseCase
import com.example.deckphonephone.deck.application.CreateTextCardUseCase
import com.example.deckphonephone.deck.application.CreateWebCardUseCase
import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.application.DeleteCardUseCase
import com.example.deckphonephone.deck.application.DeleteCategoryUseCase
import com.example.deckphonephone.deck.application.ExecuteCardUseCase
import com.example.deckphonephone.deck.application.ListPairedBluetoothDevicesUseCase
import com.example.deckphonephone.deck.application.ObserveCardsUseCase
import com.example.deckphonephone.deck.application.ObserveCategoriesUseCase
import com.example.deckphonephone.deck.application.ObserveDarkThemeUseCase
import com.example.deckphonephone.deck.application.SetCardEnabledUseCase
import com.example.deckphonephone.deck.application.SetDarkThemeUseCase
import com.example.deckphonephone.deck.application.UpdateBluetoothDeviceCardUseCase
import com.example.deckphonephone.deck.application.UpdateCategoryUseCase
import com.example.deckphonephone.deck.application.UpdateTextCardUseCase
import com.example.deckphonephone.deck.application.UpdateWebCardUseCase
import com.example.deckphonephone.deck.data.local.DeckDatabase
import com.example.deckphonephone.deck.data.local.RoomDeckRepository
import com.example.deckphonephone.deck.data.local.SharedPreferencesThemePreferenceRepository
import com.example.deckphonephone.deck.platform.AndroidBluetoothDeviceActionAdapter
import com.example.deckphonephone.deck.platform.AndroidClipboardCopyTextAdapter
import com.example.deckphonephone.deck.platform.AndroidOpenUrlAdapter
import com.example.deckphonephone.deck.platform.AndroidPairedBluetoothDevicesAdapter

class DeckAppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = DeckDatabase.get(appContext)
    private val repository = RoomDeckRepository(database.deckDao())
    private val themePreferenceRepository = SharedPreferencesThemePreferenceRepository(appContext)
    private val openUrlAdapter = AndroidOpenUrlAdapter(appContext)
    private val copyTextAdapter = AndroidClipboardCopyTextAdapter(appContext)
    private val pairedBluetoothDevicesAdapter = AndroidPairedBluetoothDevicesAdapter(appContext)
    private val bluetoothDeviceActionAdapter = AndroidBluetoothDeviceActionAdapter(appContext)

    val useCases = DeckUseCases(
        createCategory = CreateCategoryUseCase(repository),
        observeCategories = ObserveCategoriesUseCase(repository),
        updateCategory = UpdateCategoryUseCase(repository),
        deleteCategory = DeleteCategoryUseCase(repository),
        createTextCard = CreateTextCardUseCase(repository),
        createWebCard = CreateWebCardUseCase(repository),
        createBluetoothDeviceCard = CreateBluetoothDeviceCardUseCase(repository),
        listPairedBluetoothDevices = ListPairedBluetoothDevicesUseCase(pairedBluetoothDevicesAdapter),
        observeCards = ObserveCardsUseCase(repository),
        updateTextCard = UpdateTextCardUseCase(repository),
        updateWebCard = UpdateWebCardUseCase(repository),
        updateBluetoothDeviceCard = UpdateBluetoothDeviceCardUseCase(repository),
        deleteCard = DeleteCardUseCase(repository),
        setCardEnabled = SetCardEnabledUseCase(repository),
        executeCard = ExecuteCardUseCase(
            openUrlPort = openUrlAdapter,
            copyTextPort = copyTextAdapter,
            bluetoothDeviceActionPort = bluetoothDeviceActionAdapter,
        ),
        observeDarkTheme = ObserveDarkThemeUseCase(themePreferenceRepository),
        setDarkTheme = SetDarkThemeUseCase(themePreferenceRepository),
    )
}
