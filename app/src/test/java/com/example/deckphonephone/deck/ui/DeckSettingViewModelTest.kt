package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.BluetoothDeviceActionPort
import com.example.deckphonephone.deck.application.BluetoothDeviceActionResult
import com.example.deckphonephone.deck.application.CopyTextPort
import com.example.deckphonephone.deck.application.CopyTextResult
import com.example.deckphonephone.deck.application.CreateBluetoothDeviceCardUseCase
import com.example.deckphonephone.deck.application.CreateCategoryUseCase
import com.example.deckphonephone.deck.application.CreateTextCardUseCase
import com.example.deckphonephone.deck.application.CreateWebCardUseCase
import com.example.deckphonephone.deck.application.DeckRepository
import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.application.DeleteCardUseCase
import com.example.deckphonephone.deck.application.DeleteCategoryUseCase
import com.example.deckphonephone.deck.application.ExecuteCardUseCase
import com.example.deckphonephone.deck.application.ListPairedBluetoothDevicesUseCase
import com.example.deckphonephone.deck.application.ObserveCardsUseCase
import com.example.deckphonephone.deck.application.ObserveCategoriesUseCase
import com.example.deckphonephone.deck.application.ObserveDarkThemeUseCase
import com.example.deckphonephone.deck.application.OpenUrlPort
import com.example.deckphonephone.deck.application.OpenUrlResult
import com.example.deckphonephone.deck.application.PairedBluetoothDevice
import com.example.deckphonephone.deck.application.PairedBluetoothDevicesPort
import com.example.deckphonephone.deck.application.PairedBluetoothDevicesResult
import com.example.deckphonephone.deck.application.SetCardEnabledUseCase
import com.example.deckphonephone.deck.application.SetDarkThemeUseCase
import com.example.deckphonephone.deck.application.ThemePreferenceRepository
import com.example.deckphonephone.deck.application.UpdateBluetoothDeviceCardUseCase
import com.example.deckphonephone.deck.application.UpdateCategoryUseCase
import com.example.deckphonephone.deck.application.UpdateTextCardUseCase
import com.example.deckphonephone.deck.application.UpdateWebCardUseCase
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import com.example.deckphonephone.deck.domain.DeckCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Test

class DeckSettingViewModelTest {
    @Test
    fun `requesting and dismissing category creation updates sheet state`() {
        val viewModel = DeckSettingViewModel(
            useCases = FakeSettingDeckRepository().toUseCases(),
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.requestCreateCategory()

        assertEquals(true, viewModel.uiState.value.isCreatingCategory)

        viewModel.dismissCreateCategory()

        assertEquals(false, viewModel.uiState.value.isCreatingCategory)
    }

    @Test
    fun `successful category creation closes creation sheet`() {
        val viewModel = DeckSettingViewModel(
            useCases = FakeSettingDeckRepository().toUseCases(),
            dispatcher = Dispatchers.Unconfined,
        )
        viewModel.requestCreateCategory()
        viewModel.onCategoryNameChanged("자주 쓰는 슬롯")

        viewModel.createCategory()

        assertEquals(false, viewModel.uiState.value.isCreatingCategory)
        assertEquals("", viewModel.uiState.value.categoryNameInput)
    }

    @Test
    fun `category validation error keeps creation sheet open`() {
        val viewModel = DeckSettingViewModel(
            useCases = FakeSettingDeckRepository().toUseCases(),
            dispatcher = Dispatchers.Unconfined,
        )
        viewModel.requestCreateCategory()

        viewModel.createCategory()

        assertEquals(true, viewModel.uiState.value.isCreatingCategory)
        assertEquals("카테고리 이름을 입력해 주세요.", viewModel.uiState.value.message)
    }

    @Test
    fun `successful card creation closes creation sheet`() {
        val repository = FakeSettingDeckRepository()
        val viewModel = DeckSettingViewModel(
            useCases = repository.toUseCases(),
            dispatcher = Dispatchers.Unconfined,
        )
        viewModel.selectCategory(1L)
        viewModel.requestCreateCard()
        viewModel.onCardTitleChanged("복사 슬롯")
        viewModel.onCardPayloadChanged("hello")

        viewModel.createCard()

        assertEquals(false, viewModel.uiState.value.isCreatingCard)
        assertEquals("", viewModel.uiState.value.cardTitleInput)
        assertEquals("", viewModel.uiState.value.cardPayloadInput)
    }

    @Test
    fun `card validation error keeps creation sheet open`() {
        val repository = FakeSettingDeckRepository()
        val viewModel = DeckSettingViewModel(
            useCases = repository.toUseCases(),
            dispatcher = Dispatchers.Unconfined,
        )
        viewModel.selectCategory(1L)
        viewModel.requestCreateCard()

        viewModel.createCard()

        assertEquals(true, viewModel.uiState.value.isCreatingCard)
        assertEquals("슬롯 이름을 입력해 주세요.", viewModel.uiState.value.message)
    }

    @Test
    fun `selecting bluetooth device fills blank card title`() {
        val viewModel = DeckSettingViewModel(
            useCases = FakeSettingDeckRepository().toUseCases(),
            dispatcher = Dispatchers.Unconfined,
        )
        val device = PairedBluetoothDevice("Buds", "AC:80:0A:20:CB:AF")

        viewModel.onBluetoothDeviceSelected(device)

        assertEquals(device, viewModel.uiState.value.selectedBluetoothDevice)
        assertEquals("Buds", viewModel.uiState.value.cardTitleInput)
    }

    @Test
    fun `requesting and dismissing app settings updates dialog state`() {
        val viewModel = DeckSettingViewModel(
            useCases = FakeSettingDeckRepository().toUseCases(),
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.requestAppSettings()

        assertEquals(true, viewModel.uiState.value.isAppSettingsOpen)

        viewModel.dismissAppSettings()

        assertEquals(false, viewModel.uiState.value.isAppSettingsOpen)
    }

    @Test
    fun `changing dark theme updates theme state`() {
        val themePreferenceRepository = FakeSettingThemePreferenceRepository()
        val viewModel = DeckSettingViewModel(
            useCases = FakeSettingDeckRepository().toUseCases(themePreferenceRepository),
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.setDarkTheme(true)

        assertEquals(true, viewModel.uiState.value.isDarkTheme)
    }
}

private class FakeSettingDeckRepository : DeckRepository {
    private val categories = MutableStateFlow(
        listOf(
            DeckCategory(
                id = 1L,
                name = "기본",
            ),
        ),
    )
    private val cards = MutableStateFlow<List<ActionCard>>(emptyList())
    private var nextCategoryId = 2L
    private var nextCardId = 1L

    override fun observeCategories(): Flow<List<DeckCategory>> = categories

    override fun observeCards(categoryId: Long): Flow<List<ActionCard>> = cards

    override suspend fun createCategory(
        name: String,
        description: String,
        isEnabled: Boolean,
    ): DeckCategory {
        val category = DeckCategory(
            id = nextCategoryId++,
            name = name,
            description = description,
            isEnabled = isEnabled,
        )
        categories.value = categories.value + category
        return category
    }

    override suspend fun createCard(
        categoryId: Long,
        title: String,
        description: String,
        action: CardAction,
        isEnabled: Boolean,
    ): ActionCard {
        val card = ActionCard(
            id = nextCardId++,
            categoryId = categoryId,
            title = title,
            description = description,
            action = action,
            isEnabled = isEnabled,
        )
        cards.value = cards.value + card
        return card
    }

    override suspend fun updateCategory(category: DeckCategory): DeckCategory = category

    override suspend fun deleteCategory(categoryId: Long) = Unit

    override suspend fun updateCard(card: ActionCard): ActionCard = card

    override suspend fun deleteCard(cardId: Long) = Unit
}

private class FakeSettingThemePreferenceRepository : ThemePreferenceRepository {
    private val darkTheme = MutableStateFlow(false)

    override val isDarkTheme: StateFlow<Boolean> = darkTheme

    override suspend fun setDarkTheme(isDarkTheme: Boolean) {
        darkTheme.value = isDarkTheme
    }
}

private fun DeckRepository.toUseCases(
    themePreferenceRepository: ThemePreferenceRepository = FakeSettingThemePreferenceRepository(),
): DeckUseCases {
    return DeckUseCases(
        createCategory = CreateCategoryUseCase(this),
        observeCategories = ObserveCategoriesUseCase(this),
        updateCategory = UpdateCategoryUseCase(this),
        deleteCategory = DeleteCategoryUseCase(this),
        createTextCard = CreateTextCardUseCase(this),
        createWebCard = CreateWebCardUseCase(this),
        createBluetoothDeviceCard = CreateBluetoothDeviceCardUseCase(this),
        listPairedBluetoothDevices = ListPairedBluetoothDevicesUseCase(EmptySettingPairedBluetoothDevicesPort),
        observeCards = ObserveCardsUseCase(this),
        updateTextCard = UpdateTextCardUseCase(this),
        updateWebCard = UpdateWebCardUseCase(this),
        updateBluetoothDeviceCard = UpdateBluetoothDeviceCardUseCase(this),
        deleteCard = DeleteCardUseCase(this),
        setCardEnabled = SetCardEnabledUseCase(this),
        executeCard = ExecuteCardUseCase(
            openUrlPort = AlwaysSuccessfulSettingOpenUrlPort,
            copyTextPort = AlwaysSuccessfulSettingCopyTextPort,
            bluetoothDeviceActionPort = AlwaysSuccessfulSettingBluetoothDeviceActionPort,
        ),
        observeDarkTheme = ObserveDarkThemeUseCase(themePreferenceRepository),
        setDarkTheme = SetDarkThemeUseCase(themePreferenceRepository),
    )
}

private object EmptySettingPairedBluetoothDevicesPort : PairedBluetoothDevicesPort {
    override suspend fun listPairedBluetoothDevices(): PairedBluetoothDevicesResult {
        return PairedBluetoothDevicesResult.Success(emptyList())
    }
}

private object AlwaysSuccessfulSettingOpenUrlPort : OpenUrlPort {
    override suspend fun openUrl(url: String): OpenUrlResult = OpenUrlResult.Success
}

private object AlwaysSuccessfulSettingCopyTextPort : CopyTextPort {
    override suspend fun copyText(text: String): CopyTextResult = CopyTextResult.Success
}

private object AlwaysSuccessfulSettingBluetoothDeviceActionPort : BluetoothDeviceActionPort {
    override suspend fun startBluetoothDeviceAction(
        deviceName: String,
        deviceAddress: String,
    ): BluetoothDeviceActionResult = BluetoothDeviceActionResult.Started
}
