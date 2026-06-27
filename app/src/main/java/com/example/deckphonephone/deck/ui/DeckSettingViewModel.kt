package com.example.deckphonephone.deck.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deckphonephone.deck.application.DeckError
import com.example.deckphonephone.deck.application.DeckResult
import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.application.ExecuteCardResult
import com.example.deckphonephone.deck.application.PairedBluetoothDevice
import com.example.deckphonephone.deck.application.PairedBluetoothDevicesResult
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
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

    fun requestCreateCard() {
        _uiState.update { it.copy(isCreatingCard = true, message = null) }
    }

    fun dismissCreateCard() {
        _uiState.update { it.copy(isCreatingCard = false) }
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
                cards = emptyList(),
                isCardsLoading = true,
                cardTitleInput = "",
                cardPayloadInput = "",
                selectedBluetoothDevice = null,
                isCreatingCard = false,
            )
        }

        cardsJob?.cancel()
        cardsJob = viewModelScope.launch(dispatcher) {
            useCases.observeCards(categoryId).collect { cards ->
                _uiState.update {
                    it.copy(
                        selectedCategoryId = categoryId,
                        cards = cards,
                        isCardsLoading = false,
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
                cards = emptyList(),
                isCardsLoading = false,
                cardTitleInput = "",
                cardPayloadInput = "",
                selectedBluetoothDevice = null,
                isCreatingCard = false,
            )
        }
    }

    fun onCardTitleChanged(value: String) {
        _uiState.update { it.copy(cardTitleInput = value) }
    }

    fun onCardPayloadChanged(value: String) {
        _uiState.update { it.copy(cardPayloadInput = value) }
    }

    fun onCardTypeChanged(type: CardType) {
        _uiState.update { state ->
            state.copy(
                selectedCardType = type,
                cardPayloadInput = "",
                selectedBluetoothDevice = if (type == CardType.Bluetooth) {
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
                cardTitleInput = if (state.cardTitleInput.isBlank()) device.name else state.cardTitleInput,
            )
        }
    }

    fun onEditingBluetoothDeviceSelected(device: PairedBluetoothDevice) {
        _uiState.update { state ->
            val editingCard = state.editingCard ?: return@update state
            state.copy(
                editingCard = editingCard.copy(
                    selectedBluetoothDevice = device,
                    title = if (editingCard.title.isBlank()) device.name else editingCard.title,
                ),
            )
        }
    }

    fun createCard() {
        val state = _uiState.value
        val categoryId = state.selectedCategoryId
        if (categoryId == null) {
            showError(DeckError.CategoryNotSelected)
            return
        }

        viewModelScope.launch(dispatcher) {
            val result = when (state.selectedCardType) {
                CardType.Text -> useCases.createTextCard(
                    categoryId = categoryId,
                    title = state.cardTitleInput,
                    text = state.cardPayloadInput,
                )

                CardType.Web -> useCases.createWebCard(
                    categoryId = categoryId,
                    title = state.cardTitleInput,
                    rawUrl = state.cardPayloadInput,
                )

                CardType.Bluetooth -> useCases.createBluetoothDeviceCard(
                    categoryId = categoryId,
                    title = state.cardTitleInput,
                    device = state.selectedBluetoothDevice,
                )
            }

            when (result) {
                is DeckResult.Success -> {
                    _uiState.update {
                        it.copy(
                            cardTitleInput = "",
                            cardPayloadInput = "",
                            selectedBluetoothDevice = null,
                            isCreatingCard = false,
                            message = "카드를 저장했습니다.",
                        )
                    }
                }

                is DeckResult.Failure -> showError(result.error)
            }
        }
    }

    fun requestEditCard(card: ActionCard) {
        _uiState.update {
            it.copy(
                editingCard = CardEditState(
                    cardId = card.id,
                    title = card.title,
                    payload = card.action.payload(),
                    selectedCardType = card.action.cardType(),
                    selectedBluetoothDevice = card.action.bluetoothDevice(),
                    isEnabled = card.isEnabled,
                ),
                message = null,
            )
        }
    }

    fun onEditingCardTitleChanged(value: String) {
        _uiState.update { state ->
            state.copy(editingCard = state.editingCard?.copy(title = value))
        }
    }

    fun onEditingCardPayloadChanged(value: String) {
        _uiState.update { state ->
            state.copy(editingCard = state.editingCard?.copy(payload = value))
        }
    }

    fun onEditingCardTypeChanged(type: CardType) {
        _uiState.update { state ->
            val editingCard = state.editingCard ?: return@update state
            state.copy(
                editingCard = editingCard.copy(
                    selectedCardType = type,
                    payload = if (editingCard.selectedCardType == type) editingCard.payload else "",
                    selectedBluetoothDevice = if (type == CardType.Bluetooth) {
                        editingCard.selectedBluetoothDevice
                    } else {
                        null
                    },
                ),
            )
        }
    }

    fun onEditingCardEnabledChanged(isEnabled: Boolean) {
        _uiState.update { state ->
            state.copy(editingCard = state.editingCard?.copy(isEnabled = isEnabled))
        }
    }

    fun dismissCardEdit() {
        _uiState.update { it.copy(editingCard = null) }
    }

    fun saveCardEdit() {
        val state = _uiState.value
        val editState = state.editingCard ?: return
        val card = state.cards.firstOrNull { it.id == editState.cardId }
        if (card == null) {
            showMissingTarget()
            return
        }

        viewModelScope.launch(dispatcher) {
            val enabledCard = card.copy(isEnabled = editState.isEnabled)
            val result = when (editState.selectedCardType) {
                CardType.Text -> useCases.updateTextCard(
                    card = enabledCard,
                    title = editState.title,
                    text = editState.payload,
                )

                CardType.Web -> useCases.updateWebCard(
                    card = enabledCard,
                    title = editState.title,
                    rawUrl = editState.payload,
                )

                CardType.Bluetooth -> useCases.updateBluetoothDeviceCard(
                    card = enabledCard,
                    title = editState.title,
                    device = editState.selectedBluetoothDevice,
                )
            }

            when (result) {
                is DeckResult.Success -> {
                    _uiState.update {
                        it.copy(
                            editingCard = null,
                            message = "카드를 수정했습니다.",
                        )
                    }
                }

                is DeckResult.Failure -> showError(result.error)
            }
        }
    }

    fun requestDeleteCard(card: ActionCard) {
        _uiState.update {
            it.copy(
                deleteTarget = DeleteTarget.Card(card),
                message = null,
            )
        }
    }

    fun toggleCardEnabled(card: ActionCard) {
        viewModelScope.launch(dispatcher) {
            val updatedCard = useCases.setCardEnabled(
                card = card,
                isEnabled = !card.isEnabled,
            )
            showMessage(
                if (updatedCard.isEnabled) {
                    "카드를 활성화했습니다."
                } else {
                    "카드를 비활성화했습니다."
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
                            cards = if (it.selectedCategoryId == target.category.id) emptyList() else it.cards,
                            deleteTarget = null,
                            message = "카테고리를 삭제했습니다.",
                        )
                    }
                }

                is DeleteTarget.Card -> {
                    useCases.deleteCard(target.card.id)
                    _uiState.update {
                        it.copy(
                            deleteTarget = null,
                            message = "카드를 삭제했습니다.",
                        )
                    }
                }
            }
        }
    }

    fun executeCard(card: ActionCard) {
        viewModelScope.launch(dispatcher) {
            when (useCases.executeCard(card)) {
                ExecuteCardResult.OpenedUrl -> Unit
                ExecuteCardResult.CopiedText -> showMessage("복사했습니다")
                ExecuteCardResult.DisabledCard -> showMessage("비활성화된 카드입니다.")
                ExecuteCardResult.CopyTextBlank -> showMessage("복사할 문구가 없습니다")
                ExecuteCardResult.OpenUrlFailed -> showMessage("웹페이지를 열지 못했습니다.")
                ExecuteCardResult.CopyTextFailed -> showMessage("문구를 복사하지 못했습니다.")
                ExecuteCardResult.BluetoothActionUnsupported -> showMessage(
                    "블루투스 연결/해제는 아직 지원하지 않습니다.",
                )
            }
        }
    }

    fun messageShown() {
        _uiState.update { it.copy(message = null) }
    }

    private fun showError(error: DeckError) {
        showMessage(error.toMessage())
    }

    private fun showMissingTarget() {
        _uiState.update {
            it.copy(
                editingCategory = null,
                editingCard = null,
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

    private fun CardAction.payload(): String {
        return when (this) {
            is CardAction.CopyText -> text
            is CardAction.OpenUrl -> url
            is CardAction.BluetoothDevice -> deviceAddress
        }
    }

    private fun CardAction.cardType(): CardType {
        return when (this) {
            is CardAction.CopyText -> CardType.Text
            is CardAction.OpenUrl -> CardType.Web
            is CardAction.BluetoothDevice -> CardType.Bluetooth
        }
    }

    private fun CardAction.bluetoothDevice(): PairedBluetoothDevice? {
        return when (this) {
            is CardAction.BluetoothDevice -> PairedBluetoothDevice(
                name = deviceName,
                address = deviceAddress,
            )

            is CardAction.CopyText,
            is CardAction.OpenUrl -> null
        }
    }

    private fun DeckError.toMessage(): String {
        return when (this) {
            DeckError.CategoryNameBlank -> "카테고리 이름을 입력해 주세요."
            DeckError.CardTitleBlank -> "슬롯 이름을 입력해 주세요."
            DeckError.TextBlank -> "복사할 문구를 입력해 주세요."
            DeckError.UrlBlank -> "열 웹페이지 주소를 입력해 주세요."
            DeckError.InvalidUrl -> "웹 주소 형식이 올바르지 않습니다."
            DeckError.CategoryNotSelected -> "카테고리를 먼저 선택해 주세요."
            DeckError.BluetoothDeviceNotSelected -> "블루투스 기기를 선택해 주세요."
            DeckError.BluetoothDeviceAddressBlank -> "블루투스 기기 주소를 찾지 못했습니다."
        }
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