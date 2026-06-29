package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.BluetoothDeviceActionPort
import com.example.deckphonephone.deck.application.BluetoothDeviceActionResult
import com.example.deckphonephone.deck.application.ConnectedBluetoothDevice
import com.example.deckphonephone.deck.application.ConnectedBluetoothDevicesPort
import com.example.deckphonephone.deck.application.CopyTextPort
import com.example.deckphonephone.deck.application.CopyTextResult
import com.example.deckphonephone.deck.application.CreateBluetoothDeviceActionCardUseCase
import com.example.deckphonephone.deck.application.CreateCategoryUseCase
import com.example.deckphonephone.deck.application.CreateTextActionCardUseCase
import com.example.deckphonephone.deck.application.CreateWebActionCardUseCase
import com.example.deckphonephone.deck.application.DeckRepository
import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.application.DeleteActionCardUseCase
import com.example.deckphonephone.deck.application.DeleteCategoryUseCase
import com.example.deckphonephone.deck.application.ExecuteActionCardUseCase
import com.example.deckphonephone.deck.application.ListPairedBluetoothDevicesUseCase
import com.example.deckphonephone.deck.application.ObserveActionCardsUseCase
import com.example.deckphonephone.deck.application.ObserveCategoriesUseCase
import com.example.deckphonephone.deck.application.ObserveConnectedBluetoothDevicesUseCase
import com.example.deckphonephone.deck.application.ObserveDarkThemeUseCase
import com.example.deckphonephone.deck.application.ObserveOverlayHandPreferenceUseCase
import com.example.deckphonephone.deck.application.OpenUrlPort
import com.example.deckphonephone.deck.application.OverlayHandPreference
import com.example.deckphonephone.deck.application.OpenUrlResult
import com.example.deckphonephone.deck.application.PairedBluetoothDevicesPort
import com.example.deckphonephone.deck.application.PairedBluetoothDevicesResult
import com.example.deckphonephone.deck.application.SetActionCardEnabledUseCase
import com.example.deckphonephone.deck.application.SetDarkThemeUseCase
import com.example.deckphonephone.deck.application.SetOverlayHandPreferenceUseCase
import com.example.deckphonephone.deck.application.AppPreferenceRepository
import com.example.deckphonephone.deck.application.UpdateBluetoothDeviceActionCardUseCase
import com.example.deckphonephone.deck.application.UpdateCategoryUseCase
import com.example.deckphonephone.deck.application.UpdateTextActionCardUseCase
import com.example.deckphonephone.deck.application.UpdateWebActionCardUseCase
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation
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
    fun `selecting category keeps category screen until actionCards are emitted`() {
        val viewModel = DeckOverlayViewModel(
            useCases = FakeDeckRepository(
                actionCardsFlow = flow { awaitCancellation() },
            ).toUseCases(),
            onFinished = {},
            onTransientMessage = {},
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.selectCategory(1L)

        val state = viewModel.uiState.value
        assertEquals(null, state.selectedCategoryId)
        assertTrue(state.isActionCardsLoading)
        assertEquals(emptyList<ActionCard>(), state.actionCards)

        viewModel.clear()
    }

    @Test
    fun `selecting category enters card screen when actionCards are emitted`() {
        val card = ActionCard(
            id = 10L,
            categoryId = 1L,
            title = "복사 슬롯",
            operation = ActionCardOperation.CopyText("hello"),
        )
        val viewModel = DeckOverlayViewModel(
            useCases = FakeDeckRepository(
                actionCardsFlow = flowOf(listOf(card)),
            ).toUseCases(),
            onFinished = {},
            onTransientMessage = {},
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.selectCategory(1L)

        val state = viewModel.uiState.value
        assertEquals(1L, state.selectedCategoryId)
        assertEquals(false, state.isActionCardsLoading)
        assertEquals(listOf(card), state.actionCards)

        viewModel.clear()
    }

    @Test
    fun `opening web card finishes overlay without message`() {
        var finishCount = 0
        val transientMessages = mutableListOf<String>()
        val viewModel = DeckOverlayViewModel(
            useCases = FakeDeckRepository(
                actionCardsFlow = flowOf(emptyList()),
            ).toUseCases(),
            onFinished = { finishCount += 1 },
            onTransientMessage = transientMessages::add,
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.executeActionCard(webCard())

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
                actionCardsFlow = flowOf(emptyList()),
            ).toUseCases(),
            onFinished = { finishCount += 1 },
            onTransientMessage = transientMessages::add,
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.executeActionCard(textCard())

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
                actionCardsFlow = flowOf(emptyList()),
            ).toUseCases(),
            onFinished = { finishCount += 1 },
            onTransientMessage = transientMessages::add,
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.executeActionCard(bluetoothCard())

        assertEquals(1, finishCount)
        assertEquals(listOf("Bluetooth 설정에서 Buds을 찾는 중입니다."), transientMessages)
        assertEquals(null, viewModel.uiState.value.message)

        viewModel.clear()
    }

    @Test
    fun `connected bluetooth devices are exposed in overlay state`() {
        val firstDevice = ConnectedBluetoothDevice(
            name = "Buds",
            address = "AC:80:0A:20:CB:AF",
        )
        val secondDevice = ConnectedBluetoothDevice(
            name = "Buds Right",
            address = "11:22:33:44:55:66",
        )
        val connectedDevices = MutableStateFlow(listOf(firstDevice))
        val viewModel = DeckOverlayViewModel(
            useCases = FakeDeckRepository(
                actionCardsFlow = flowOf(emptyList()),
            ).toUseCases(connectedDevices),
            onFinished = {},
            onTransientMessage = {},
            dispatcher = Dispatchers.Unconfined,
        )

        assertEquals(
            listOf(firstDevice),
            viewModel.uiState.value.connectedBluetoothDevices,
        )

        connectedDevices.value = listOf(secondDevice)

        assertEquals(
            listOf(secondDevice),
            viewModel.uiState.value.connectedBluetoothDevices,
        )

        viewModel.clear()
    }

    @Test
    fun `bluetooth card is connected when device address matches`() {
        assertTrue(
            bluetoothCard().hasConnectedBluetoothDevice(
                listOf(
                    ConnectedBluetoothDevice(
                        name = "Other name",
                        address = "AC:80:0A:20:CB:AF",
                    ),
                ),
            ),
        )
    }

    @Test
    fun `bluetooth card is connected when buds unit address differs but name matches`() {
        val card = ActionCard(
            id = 23L,
            categoryId = 1L,
            title = "Buds3 Pro",
            operation = ActionCardOperation.BluetoothDevice(
                deviceName = "주상의 Buds3 Pro",
                deviceAddress = "AC:80:0A:20:CB:AF",
            ),
        )

        assertTrue(
            card.hasConnectedBluetoothDevice(
                listOf(
                    ConnectedBluetoothDevice(
                        name = "LE-주상의 Buds3 Pro (R)",
                        address = "11:22:33:44:55:66",
                    ),
                ),
            ),
        )
    }
}

private fun webCard() = ActionCard(
    id = 20L,
    categoryId = 1L,
    title = "Web",
    operation = ActionCardOperation.OpenUrl("https://example.com"),
)

private fun textCard() = ActionCard(
    id = 21L,
    categoryId = 1L,
    title = "Copy",
    operation = ActionCardOperation.CopyText("hello"),
)

private fun bluetoothCard() = ActionCard(
    id = 22L,
    categoryId = 1L,
    title = "Buds",
    operation = ActionCardOperation.BluetoothDevice(
        deviceName = "Buds",
        deviceAddress = "AC:80:0A:20:CB:AF",
    ),
)

private class FakeDeckRepository(
    private val actionCardsFlow: Flow<List<ActionCard>>,
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

    override fun observeActionCards(categoryId: Long): Flow<List<ActionCard>> {
        return actionCardsFlow
    }

    override suspend fun createCategory(
        name: String,
        description: String,
        isEnabled: Boolean,
    ): DeckCategory = error("Not used")

    override suspend fun createActionCard(
        categoryId: Long,
        title: String,
        description: String,
        operation: ActionCardOperation,
        isEnabled: Boolean,
    ): ActionCard = error("Not used")

    override suspend fun updateCategory(category: DeckCategory): DeckCategory = error("Not used")

    override suspend fun deleteCategory(categoryId: Long) = error("Not used")

    override suspend fun updateActionCard(card: ActionCard): ActionCard = error("Not used")

    override suspend fun deleteActionCard(actionCardId: Long) = error("Not used")
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

private fun DeckRepository.toUseCases(
    connectedDevicesFlow: Flow<List<ConnectedBluetoothDevice>> = flowOf(emptyList()),
): DeckUseCases {
    val appPreferenceRepository = FakeAppPreferenceRepository()

    return DeckUseCases(
        createCategory = CreateCategoryUseCase(this),
        observeCategories = ObserveCategoriesUseCase(this),
        updateCategory = UpdateCategoryUseCase(this),
        deleteCategory = DeleteCategoryUseCase(this),
        createTextActionCard = CreateTextActionCardUseCase(this),
        createWebActionCard = CreateWebActionCardUseCase(this),
        createBluetoothDeviceActionCard = CreateBluetoothDeviceActionCardUseCase(this),
        listPairedBluetoothDevices = ListPairedBluetoothDevicesUseCase(EmptyPairedBluetoothDevicesPort),
        observeConnectedBluetoothDevices = ObserveConnectedBluetoothDevicesUseCase(
            FakeConnectedBluetoothDevicesPort(connectedDevicesFlow),
        ),
        observeActionCards = ObserveActionCardsUseCase(this),
        updateTextActionCard = UpdateTextActionCardUseCase(this),
        updateWebActionCard = UpdateWebActionCardUseCase(this),
        updateBluetoothDeviceActionCard = UpdateBluetoothDeviceActionCardUseCase(this),
        deleteActionCard = DeleteActionCardUseCase(this),
        setActionCardEnabled = SetActionCardEnabledUseCase(this),
        executeActionCard = ExecuteActionCardUseCase(
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

private class FakeConnectedBluetoothDevicesPort(
    private val connectedDevicesFlow: Flow<List<ConnectedBluetoothDevice>>,
) : ConnectedBluetoothDevicesPort {
    override fun observeConnectedBluetoothDevices(): Flow<List<ConnectedBluetoothDevice>> {
        return connectedDevicesFlow
    }
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
