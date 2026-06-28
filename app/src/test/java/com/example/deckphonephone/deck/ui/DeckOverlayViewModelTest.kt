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
import com.example.deckphonephone.deck.application.ObserveOverlayHandPreferenceUseCase
import com.example.deckphonephone.deck.application.OpenUrlPort
import com.example.deckphonephone.deck.application.OverlayHandPreference
import com.example.deckphonephone.deck.application.OpenUrlResult
import com.example.deckphonephone.deck.application.PairedBluetoothDevicesPort
import com.example.deckphonephone.deck.application.PairedBluetoothDevicesResult
import com.example.deckphonephone.deck.application.SetCardEnabledUseCase
import com.example.deckphonephone.deck.application.SetDarkThemeUseCase
import com.example.deckphonephone.deck.application.SetOverlayHandPreferenceUseCase
import com.example.deckphonephone.deck.application.AppPreferenceRepository
import com.example.deckphonephone.deck.application.UpdateBluetoothDeviceCardUseCase
import com.example.deckphonephone.deck.application.UpdateCategoryUseCase
import com.example.deckphonephone.deck.application.UpdateTextCardUseCase
import com.example.deckphonephone.deck.application.UpdateWebCardUseCase
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import com.example.deckphonephone.deck.domain.DeckCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckOverlayViewModelTest {
    @Test
    fun `selecting category keeps category screen until cards are emitted`() {
        val viewModel = DeckOverlayViewModel(
            useCases = FakeDeckRepository(
                cardsFlow = flow { awaitCancellation() },
            ).toUseCases(),
            onFinished = {},
            onTransientMessage = {},
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.selectCategory(1L)

        val state = viewModel.uiState.value
        assertEquals(null, state.selectedCategoryId)
        assertTrue(state.isCardsLoading)
        assertEquals(emptyList<ActionCard>(), state.cards)

        viewModel.clear()
    }

    @Test
    fun `selecting category enters card screen when cards are emitted`() {
        val card = ActionCard(
            id = 10L,
            categoryId = 1L,
            title = "복사 슬롯",
            action = CardAction.CopyText("hello"),
        )
        val viewModel = DeckOverlayViewModel(
            useCases = FakeDeckRepository(
                cardsFlow = flowOf(listOf(card)),
            ).toUseCases(),
            onFinished = {},
            onTransientMessage = {},
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.selectCategory(1L)

        val state = viewModel.uiState.value
        assertEquals(1L, state.selectedCategoryId)
        assertEquals(false, state.isCardsLoading)
        assertEquals(listOf(card), state.cards)

        viewModel.clear()
    }

    @Test
    fun `opening web card finishes overlay without message`() {
        var finishCount = 0
        val transientMessages = mutableListOf<String>()
        val viewModel = DeckOverlayViewModel(
            useCases = FakeDeckRepository(
                cardsFlow = flowOf(emptyList()),
            ).toUseCases(),
            onFinished = { finishCount += 1 },
            onTransientMessage = transientMessages::add,
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.executeCard(webCard())

        assertEquals(1, finishCount)
        assertEquals(emptyList<String>(), transientMessages)
        assertEquals(null, viewModel.uiState.value.message)

        viewModel.clear()
    }

    @Test
    fun `copying text card shows transient message and finishes overlay`() {
        var finishCount = 0
        val transientMessages = mutableListOf<String>()
        val viewModel = DeckOverlayViewModel(
            useCases = FakeDeckRepository(
                cardsFlow = flowOf(emptyList()),
            ).toUseCases(),
            onFinished = { finishCount += 1 },
            onTransientMessage = transientMessages::add,
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.executeCard(textCard())

        assertEquals(1, finishCount)
        assertEquals(listOf("복사했습니다"), transientMessages)
        assertEquals(null, viewModel.uiState.value.message)

        viewModel.clear()
    }

    @Test
    fun `starting bluetooth automation shows transient message and finishes overlay`() {
        var finishCount = 0
        val transientMessages = mutableListOf<String>()
        val viewModel = DeckOverlayViewModel(
            useCases = FakeDeckRepository(
                cardsFlow = flowOf(emptyList()),
            ).toUseCases(),
            onFinished = { finishCount += 1 },
            onTransientMessage = transientMessages::add,
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.executeCard(bluetoothCard())

        assertEquals(1, finishCount)
        assertEquals(listOf("Bluetooth 설정에서 Buds을 찾는 중입니다."), transientMessages)
        assertEquals(null, viewModel.uiState.value.message)

        viewModel.clear()
    }
}

private fun webCard() = ActionCard(
    id = 20L,
    categoryId = 1L,
    title = "Web",
    action = CardAction.OpenUrl("https://example.com"),
)

private fun textCard() = ActionCard(
    id = 21L,
    categoryId = 1L,
    title = "Copy",
    action = CardAction.CopyText("hello"),
)

private fun bluetoothCard() = ActionCard(
    id = 22L,
    categoryId = 1L,
    title = "Buds",
    action = CardAction.BluetoothDevice(
        deviceName = "Buds",
        deviceAddress = "AC:80:0A:20:CB:AF",
    ),
)

private class FakeDeckRepository(
    private val cardsFlow: Flow<List<ActionCard>>,
) : DeckRepository {
    override fun observeCategories(): Flow<List<DeckCategory>> {
        return flowOf(
            listOf(
                DeckCategory(
                    id = 1L,
                    name = "자주 쓰는 슬롯",
                ),
            ),
        )
    }

    override fun observeCards(categoryId: Long): Flow<List<ActionCard>> {
        return cardsFlow
    }

    override suspend fun createCategory(
        name: String,
        description: String,
        isEnabled: Boolean,
    ): DeckCategory = error("Not used")

    override suspend fun createCard(
        categoryId: Long,
        title: String,
        description: String,
        action: CardAction,
        isEnabled: Boolean,
    ): ActionCard = error("Not used")

    override suspend fun updateCategory(category: DeckCategory): DeckCategory = error("Not used")

    override suspend fun deleteCategory(categoryId: Long) = error("Not used")

    override suspend fun updateCard(card: ActionCard): ActionCard = error("Not used")

    override suspend fun deleteCard(cardId: Long) = error("Not used")
}

private class FakeAppPreferenceRepository : AppPreferenceRepository {
    private val darkTheme = MutableStateFlow(false)
    private val handPreference = MutableStateFlow(OverlayHandPreference.Right)

    override val isDarkTheme: StateFlow<Boolean> = darkTheme
    override val overlayHandPreference: StateFlow<OverlayHandPreference> = handPreference

    override suspend fun setDarkTheme(isDarkTheme: Boolean) {
        darkTheme.value = isDarkTheme
    }

    override suspend fun setOverlayHandPreference(overlayHandPreference: OverlayHandPreference) {
        handPreference.value = overlayHandPreference
    }
}

private fun DeckRepository.toUseCases(): DeckUseCases {
    val appPreferenceRepository = FakeAppPreferenceRepository()

    return DeckUseCases(
        createCategory = CreateCategoryUseCase(this),
        observeCategories = ObserveCategoriesUseCase(this),
        updateCategory = UpdateCategoryUseCase(this),
        deleteCategory = DeleteCategoryUseCase(this),
        createTextCard = CreateTextCardUseCase(this),
        createWebCard = CreateWebCardUseCase(this),
        createBluetoothDeviceCard = CreateBluetoothDeviceCardUseCase(this),
        listPairedBluetoothDevices = ListPairedBluetoothDevicesUseCase(EmptyPairedBluetoothDevicesPort),
        observeCards = ObserveCardsUseCase(this),
        updateTextCard = UpdateTextCardUseCase(this),
        updateWebCard = UpdateWebCardUseCase(this),
        updateBluetoothDeviceCard = UpdateBluetoothDeviceCardUseCase(this),
        deleteCard = DeleteCardUseCase(this),
        setCardEnabled = SetCardEnabledUseCase(this),
        executeCard = ExecuteCardUseCase(
            openUrlPort = AlwaysSuccessfulOpenUrlPort,
            copyTextPort = AlwaysSuccessfulCopyTextPort,
            bluetoothDeviceActionPort = AlwaysSuccessfulBluetoothDeviceActionPort,
        ),
        observeDarkTheme = ObserveDarkThemeUseCase(appPreferenceRepository),
        setDarkTheme = SetDarkThemeUseCase(appPreferenceRepository),
        observeOverlayHandPreference = ObserveOverlayHandPreferenceUseCase(appPreferenceRepository),
        setOverlayHandPreference = SetOverlayHandPreferenceUseCase(appPreferenceRepository),
    )
}

private object EmptyPairedBluetoothDevicesPort : PairedBluetoothDevicesPort {
    override suspend fun listPairedBluetoothDevices(): PairedBluetoothDevicesResult {
        return PairedBluetoothDevicesResult.Success(emptyList())
    }
}

private object AlwaysSuccessfulOpenUrlPort : OpenUrlPort {
    override suspend fun openUrl(url: String): OpenUrlResult = OpenUrlResult.Success
}

private object AlwaysSuccessfulCopyTextPort : CopyTextPort {
    override suspend fun copyText(text: String): CopyTextResult = CopyTextResult.Success
}

private object AlwaysSuccessfulBluetoothDeviceActionPort : BluetoothDeviceActionPort {
    override suspend fun startBluetoothDeviceAction(
        deviceName: String,
        deviceAddress: String,
    ): BluetoothDeviceActionResult = BluetoothDeviceActionResult.Started
}
