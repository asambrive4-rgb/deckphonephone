package com.example.deckphonephone.deck.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deckphonephone.deck.application.DeckError
import com.example.deckphonephone.deck.application.DeckResult
import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.application.OverlayHandPreference
import com.example.deckphonephone.deck.application.PairedBluetoothDevice
import com.example.deckphonephone.deck.application.PairedBluetoothDevicesResult
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.DeckCategory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckSettingViewModel(
    private val useCases: DeckUseCases,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DeckSettingUiState())
    val uiState = _uiState.asStateFlow()

    private var cardsJob: Job? = null

    init {
        viewModelScope.launch(dispatcher) {
            useCases.observeCategories().collect { categories ->
                _uiState.update { state ->
                    state.copy(
                        categories = categories,
                        isCategoriesLoading = false,
                    )
                }
            }
        }
        viewModelScope.launch(dispatcher) {
            useCases.observeDarkTheme().collect { isDarkTheme ->
                _uiState.update { state ->
                    state.copy(isDarkTheme = isDarkTheme)
                }
            }
        }
        viewModelScope.launch(dispatcher) {
            useCases.observeOverlayHandPreference().collect { overlayHandPreference ->
                _uiState.update { state ->
                    state.copy(overlayHandPreference = overlayHandPreference)
                }
            }
        }
        viewModelScope.launch(dispatcher) {
            useCases.observeConnectedBluetoothDevices().collect { devices ->
                _uiState.update { state ->
                    state.copy(connectedBluetoothDevices = devices)
                }
            }
        }
    }

    fun onCategoryNameChanged(value: String) {
        _uiState.update { it.copy(categoryNameInput = value) }
    }

    fun requestCreateCategory() {
        _uiState.update { it.copy(isCreatingCategory = true, message = null) }
    }

    fun dismissCreateCategory() {
        _uiState.update { it.copy(isCreatingCategory = false) }
    }

    fun requestCreateActionCard() {
        _uiState.update { it.copy(isCreatingActionCard = true, message = null) }
    }

    fun dismissCreateActionCard() {
        _uiState.update { it.copy(isCreatingActionCard = false) }
    }

    fun requestAppSettings() {
        _uiState.update { it.copy(isAppSettingsOpen = true, message = null) }
    }

    fun dismissAppSettings() {
        _uiState.update { it.copy(isAppSettingsOpen = false) }
    }

    fun setDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch(dispatcher) {
            useCases.setDarkTheme(isDarkTheme)
        }
    }

    fun setOverlayRightHanded(isRightHanded: Boolean) {
        viewModelScope.launch(dispatcher) {
            val overlayHandPreference = if (isRightHanded) {
                OverlayHandPreference.Right
            } else {
                OverlayHandPreference.Left
            }
            useCases.setOverlayHandPreference(overlayHandPreference)
        }
    }

    fun createCategory() {
        val name = _uiState.value.categoryNameInput
        viewModelScope.launch(dispatcher) {
            when (val result = useCases.createCategory(name)) {
                is DeckResult.Success -> {
                    _uiState.update {
                        it.copy(
                            categoryNameInput = "",
                            isCreatingCategory = false,
                            message = "카테고리를 저장했습니다.",
                        )
                    }
                }

                is DeckResult.Failure -> showError(result.error)
            }
        }
    }

    fun requestEditCategory(category: DeckCategory) {
        _uiState.update {
            it.copy(
                editingCategory = CategoryEditState(
                    categoryId = category.id,
                    name = category.name,
                ),
                message = null,
            )
        }
    }

    fun onEditingCategoryNameChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                editingCategory = state.editingCategory?.copy(name = value),
            )
        }
    }

    fun dismissCategoryEdit() {
        _uiState.update { it.copy(editingCategory = null) }
    }

    fun saveCategoryEdit() {
        val state = _uiState.value
        val editState = state.editingCategory ?: return
        val category = state.categories.firstOrNull { it.id == editState.categoryId }
        if (category == null) {
            showMissingTarget()
            return
        }

        viewModelScope.launch(dispatcher) {
            when (val result = useCases.updateCategory(category, editState.name)) {
                is DeckResult.Success -> {
                    _uiState.update {
                        it.copy(
                            editingCategory = null,
                            message = "카테고리를 수정했습니다.",
                        )
                    }
                }

                is DeckResult.Failure -> showError(result.error)
            }
        }
    }

    fun requestDeleteCategory(category: DeckCategory) {
        _uiState.update {
            it.copy(
                deleteTarget = DeleteTarget.Category(category),
                message = null,
            )
        }
    }

    fun selectCategory(categoryId: Long) {
        _uiState.update {
            it.copy(
                actionCards = emptyList(),
                isActionCardsLoading = true,
                actionCardTitleInput = "",
                actionCardPayloadInput = "",
                selectedBluetoothDevice = null,
                isCreatingActionCard = false,
            )
        }

        cardsJob?.cancel()
        cardsJob = viewModelScope.launch(dispatcher) {
            useCases.observeActionCards(categoryId).collect { actionCards ->
                _uiState.update {
                    it.copy(
                        selectedCategoryId = categoryId,
                        actionCards = actionCards,
                        isActionCardsLoading = false,
                    )
                }
            }
        }
    }

    fun leaveCategory() {
        cardsJob?.cancel()
        cardsJob = null
        _uiState.update {
            it.copy(
                selectedCategoryId = null,
                actionCards = emptyList(),
                isActionCardsLoading = false,
                actionCardTitleInput = "",
                actionCardPayloadInput = "",
                selectedBluetoothDevice = null,
                isCreatingActionCard = false,
            )
        }
    }

    fun onActionCardTitleChanged(value: String) {
        _uiState.update { it.copy(actionCardTitleInput = value) }
    }

    fun onActionCardPayloadChanged(value: String) {
        _uiState.update { it.copy(actionCardPayloadInput = value) }
    }

    fun onActionCardTypeChanged(type: ActionCardType) {
        _uiState.update { state ->
            state.copy(
                selectedActionCardType = type,
                actionCardPayloadInput = "",
                selectedBluetoothDevice = if (type == ActionCardType.Bluetooth) {
                    state.selectedBluetoothDevice
                } else {
                    null
                },
            )
        }
    }

    fun loadPairedBluetoothDevices() {
        _uiState.update { it.copy(isBluetoothDevicesLoading = true, message = null) }
        viewModelScope.launch(dispatcher) {
            when (val result = useCases.listPairedBluetoothDevices()) {
                is PairedBluetoothDevicesResult.Success -> {
                    _uiState.update {
                        it.copy(
                            pairedBluetoothDevices = result.devices,
                            isBluetoothDevicesLoading = false,
                        )
                    }
                }

                PairedBluetoothDevicesResult.PermissionDenied -> showBluetoothDeviceListError(
                    "근처 기기 권한을 허용해 주세요.",
                )

                PairedBluetoothDevicesResult.BluetoothUnavailable -> showBluetoothDeviceListError(
                    "이 기기에서 블루투스를 사용할 수 없습니다.",
                )

                PairedBluetoothDevicesResult.Failure -> showBluetoothDeviceListError(
                    "블루투스 기기 목록을 불러오지 못했습니다.",
                )
            }
        }
    }

    fun bluetoothPermissionDenied() {
        showBluetoothDeviceListError("근처 기기 권한을 허용해 주세요.")
    }

    fun onBluetoothDeviceSelected(device: PairedBluetoothDevice) {
        _uiState.update { state ->
            state.copy(
                selectedBluetoothDevice = device,
                actionCardTitleInput = if (state.actionCardTitleInput.isBlank()) device.name else state.actionCardTitleInput,
            )
        }
    }

    fun onEditingBluetoothDeviceSelected(device: PairedBluetoothDevice) {
        _uiState.update { state ->
            val editingActionCard = state.editingActionCard ?: return@update state
            state.copy(
                editingActionCard = editingActionCard.copy(
                    selectedBluetoothDevice = device,
                    title = if (editingActionCard.title.isBlank()) device.name else editingActionCard.title,
                ),
            )
        }
    }

    fun createActionCard() {
        val state = _uiState.value
        val categoryId = state.selectedCategoryId
        if (categoryId == null) {
            showError(DeckError.CategoryNotSelected)
            return
        }

        viewModelScope.launch(dispatcher) {
            val result = when (state.selectedActionCardType) {
                ActionCardType.Text -> useCases.createTextActionCard(
                    categoryId = categoryId,
                    title = state.actionCardTitleInput,
                    text = state.actionCardPayloadInput,
                )

                ActionCardType.Web -> useCases.createWebActionCard(
                    categoryId = categoryId,
                    title = state.actionCardTitleInput,
                    rawUrl = state.actionCardPayloadInput,
                )

                ActionCardType.Bluetooth -> useCases.createBluetoothDeviceActionCard(
                    categoryId = categoryId,
                    title = state.actionCardTitleInput,
                    device = state.selectedBluetoothDevice,
                )
            }

            when (result) {
                is DeckResult.Success -> {
                    _uiState.update {
                        it.copy(
                            actionCardTitleInput = "",
                            actionCardPayloadInput = "",
                            selectedBluetoothDevice = null,
                            isCreatingActionCard = false,
                            message = "액션 카드를 저장했습니다.",
                        )
                    }
                }

                is DeckResult.Failure -> showError(result.error)
            }
        }
    }

    fun requestEditActionCard(card: ActionCard) {
        _uiState.update {
            it.copy(
                editingActionCard = card.toActionCardEditState(),
                message = null,
            )
        }
    }

    fun onEditingActionCardTitleChanged(value: String) {
        _uiState.update { state ->
            state.copy(editingActionCard = state.editingActionCard?.copy(title = value))
        }
    }

    fun onEditingActionCardPayloadChanged(value: String) {
        _uiState.update { state ->
            state.copy(editingActionCard = state.editingActionCard?.copy(payload = value))
        }
    }

    fun onEditingActionCardTypeChanged(type: ActionCardType) {
        _uiState.update { state ->
            val editingActionCard = state.editingActionCard ?: return@update state
            state.copy(
                editingActionCard = editingActionCard.copy(
                    selectedActionCardType = type,
                    payload = if (editingActionCard.selectedActionCardType == type) editingActionCard.payload else "",
                    selectedBluetoothDevice = if (type == ActionCardType.Bluetooth) {
                        editingActionCard.selectedBluetoothDevice
                    } else {
                        null
                    },
                ),
            )
        }
    }

    fun onEditingActionCardEnabledChanged(isEnabled: Boolean) {
        _uiState.update { state ->
            state.copy(editingActionCard = state.editingActionCard?.copy(isEnabled = isEnabled))
        }
    }

    fun dismissActionCardEdit() {
        _uiState.update { it.copy(editingActionCard = null) }
    }

    fun saveActionCardEdit() {
        val state = _uiState.value
        val editState = state.editingActionCard ?: return
        val card = state.actionCards.firstOrNull { it.id == editState.actionCardId }
        if (card == null) {
            showMissingTarget()
            return
        }

        viewModelScope.launch(dispatcher) {
            val enabledCard = card.copy(isEnabled = editState.isEnabled)
            val result = when (editState.selectedActionCardType) {
                ActionCardType.Text -> useCases.updateTextActionCard(
                    card = enabledCard,
                    title = editState.title,
                    text = editState.payload,
                )

                ActionCardType.Web -> useCases.updateWebActionCard(
                    card = enabledCard,
                    title = editState.title,
                    rawUrl = editState.payload,
                )

                ActionCardType.Bluetooth -> useCases.updateBluetoothDeviceActionCard(
                    card = enabledCard,
                    title = editState.title,
                    device = editState.selectedBluetoothDevice,
                )
            }

            when (result) {
                is DeckResult.Success -> {
                    _uiState.update {
                        it.copy(
                            editingActionCard = null,
                            message = "액션 카드를 수정했습니다.",
                        )
                    }
                }

                is DeckResult.Failure -> showError(result.error)
            }
        }
    }

    fun requestDeleteActionCard(card: ActionCard) {
        _uiState.update {
            it.copy(
                deleteTarget = DeleteTarget.ActionCard(card),
                message = null,
            )
        }
    }

    fun toggleActionCardEnabled(card: ActionCard) {
        viewModelScope.launch(dispatcher) {
            val updatedCard = useCases.setActionCardEnabled(
                card = card,
                isEnabled = !card.isEnabled,
            )
            showMessage(
                if (updatedCard.isEnabled) {
                    "액션 카드를 활성화했습니다."
                } else {
                    "액션 카드를 비활성화했습니다."
                },
            )
        }
    }

    fun dismissDeleteConfirmation() {
        _uiState.update { it.copy(deleteTarget = null) }
    }

    fun confirmDelete() {
        val target = _uiState.value.deleteTarget ?: return
        viewModelScope.launch(dispatcher) {
            when (target) {
                is DeleteTarget.Category -> {
                    if (_uiState.value.selectedCategoryId == target.category.id) {
                        cardsJob?.cancel()
                        cardsJob = null
                    }
                    useCases.deleteCategory(target.category.id)
                    _uiState.update {
                        it.copy(
                            selectedCategoryId = if (it.selectedCategoryId == target.category.id) null else it.selectedCategoryId,
                            actionCards = if (it.selectedCategoryId == target.category.id) emptyList() else it.actionCards,
                            deleteTarget = null,
                            message = "카테고리를 삭제했습니다.",
                        )
                    }
                }

                is DeleteTarget.ActionCard -> {
                    useCases.deleteActionCard(target.actionCard.id)
                    _uiState.update {
                        it.copy(
                            deleteTarget = null,
                            message = "액션 카드를 삭제했습니다.",
                        )
                    }
                }
            }
        }
    }

    fun executeActionCard(card: ActionCard) {
        viewModelScope.launch(dispatcher) {
            when (val feedback = useCases.executeActionCard(card).toSettingExecutionFeedback()) {
                DeckSettingExecutionFeedback.None -> Unit
                is DeckSettingExecutionFeedback.Message -> showMessage(feedback.message)
            }
        }
    }

    fun messageShown() {
        _uiState.update { it.copy(message = null) }
    }

    private fun showError(error: DeckError) {
        showMessage(error.toDeckSettingMessage())
    }

    private fun showMissingTarget() {
        _uiState.update {
            it.copy(
                editingCategory = null,
                editingActionCard = null,
                deleteTarget = null,
                message = "대상을 찾지 못했습니다.",
            )
        }
    }

    private fun showBluetoothDeviceListError(message: String) {
        _uiState.update {
            it.copy(
                isBluetoothDevicesLoading = false,
                message = message,
            )
        }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    class Factory(
        private val useCases: DeckUseCases,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DeckSettingViewModel::class.java)) {
                return DeckSettingViewModel(useCases) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
