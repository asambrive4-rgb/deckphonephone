package com.example.deckphonephone.deck.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deckphonephone.deck.application.DeckError
import com.example.deckphonephone.deck.application.DeckResult
import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.application.ExecuteCardResult
import com.example.deckphonephone.deck.domain.ActionCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckViewModel(
    private val useCases: DeckUseCases,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DeckUiState())
    val uiState = _uiState.asStateFlow()

    private var cardsJob: Job? = null

    init {
        viewModelScope.launch {
            useCases.observeCategories().collect { categories ->
                _uiState.update { state ->
                    state.copy(categories = categories)
                }
            }
        }
    }

    fun onCategoryNameChanged(value: String) {
        _uiState.update { it.copy(categoryNameInput = value) }
    }

    fun createCategory() {
        val name = _uiState.value.categoryNameInput
        viewModelScope.launch {
            when (val result = useCases.createCategory(name)) {
                is DeckResult.Success -> {
                    _uiState.update {
                        it.copy(
                            categoryNameInput = "",
                            message = "카테고리를 저장했습니다.",
                        )
                    }
                }

                is DeckResult.Failure -> showError(result.error)
            }
        }
    }

    fun selectCategory(categoryId: Long) {
        _uiState.update {
            it.copy(
                selectedCategoryId = categoryId,
                cards = emptyList(),
                cardTitleInput = "",
                cardPayloadInput = "",
            )
        }

        cardsJob?.cancel()
        cardsJob = viewModelScope.launch {
            useCases.observeCards(categoryId).collect { cards ->
                _uiState.update { it.copy(cards = cards) }
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
                cardTitleInput = "",
                cardPayloadInput = "",
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
        _uiState.update {
            it.copy(
                selectedCardType = type,
                cardPayloadInput = "",
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

        viewModelScope.launch {
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
            }

            when (result) {
                is DeckResult.Success -> {
                    _uiState.update {
                        it.copy(
                            cardTitleInput = "",
                            cardPayloadInput = "",
                            message = "카드를 저장했습니다.",
                        )
                    }
                }

                is DeckResult.Failure -> showError(result.error)
            }
        }
    }

    fun executeCard(card: ActionCard) {
        viewModelScope.launch {
            when (useCases.executeCard(card)) {
                ExecuteCardResult.OpenedUrl -> Unit
                ExecuteCardResult.PastedText -> showMessage("붙여넣음")
                ExecuteCardResult.PasteTextDeferred -> showMessage("문구 붙여넣기 방식은 다음 구획에서 검토합니다.")
                ExecuteCardResult.DisabledCard -> showMessage("비활성화된 카드입니다.")
                ExecuteCardResult.OpenUrlFailed -> showMessage("웹페이지를 열지 못했습니다.")
                ExecuteCardResult.PasteTextFailed -> showMessage("문구를 붙여넣지 못했습니다.")
            }
        }
    }

    fun messageShown() {
        _uiState.update { it.copy(message = null) }
    }

    private fun showError(error: DeckError) {
        showMessage(error.toMessage())
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    private fun DeckError.toMessage(): String {
        return when (this) {
            DeckError.CategoryNameBlank -> "카테고리 이름을 입력해 주세요."
            DeckError.CardTitleBlank -> "슬롯 이름을 입력해 주세요."
            DeckError.TextBlank -> "붙여넣을 문구를 입력해 주세요."
            DeckError.UrlBlank -> "열 웹페이지 주소를 입력해 주세요."
            DeckError.InvalidUrl -> "웹 주소 형식이 올바르지 않습니다."
            DeckError.CategoryNotSelected -> "카테고리를 먼저 선택해 주세요."
        }
    }

    class Factory(
        private val useCases: DeckUseCases,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DeckViewModel::class.java)) {
                return DeckViewModel(useCases) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}