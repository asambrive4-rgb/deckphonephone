package com.example.deckphonephone.deck.application

data class DeckUseCases(
    val createCategory: CreateCategoryUseCase,
    val observeCategories: ObserveCategoriesUseCase,
    val updateCategory: UpdateCategoryUseCase,
    val deleteCategory: DeleteCategoryUseCase,
    val createTextCard: CreateTextCardUseCase,
    val createWebCard: CreateWebCardUseCase,
    val createBluetoothDeviceCard: CreateBluetoothDeviceCardUseCase,
    val listPairedBluetoothDevices: ListPairedBluetoothDevicesUseCase,
    val observeCards: ObserveCardsUseCase,
    val updateTextCard: UpdateTextCardUseCase,
    val updateWebCard: UpdateWebCardUseCase,
    val updateBluetoothDeviceCard: UpdateBluetoothDeviceCardUseCase,
    val deleteCard: DeleteCardUseCase,
    val setCardEnabled: SetCardEnabledUseCase,
    val executeCard: ExecuteCardUseCase,
    val observeDarkTheme: ObserveDarkThemeUseCase,
    val setDarkTheme: SetDarkThemeUseCase,
)