package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.application.ExecuteCardResult
import com.example.deckphonephone.deck.domain.ActionCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckOverlayController(
    private val useCases: DeckUseCases,
    private val onFinished: () -> Unit,
    private val onTransientMessage: (String) -> Unit,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _uiState = MutableStateFlow(DeckOverlayUiState())
    val uiState = _uiState.asStateFlow()

    private var cardsJob: Job? = null

    init {
        scope.launch {
            useCases.observeCategories().collect { categories ->
                _uiState.update { state ->
                    state.copy(categories = categories)
                }
            }
        }
    }

    fun selectCategory(categoryId: Long) {
        _uiState.update {
            it.copy(
                selectedCategoryId = categoryId,
                cards = emptyList(),
                message = null,
            )
        }

        cardsJob?.cancel()
        cardsJob = scope.launch {
            useCases.observeCards(categoryId).collect { cards ->
                _uiState.update { it.copy(cards = cards) }
            }
        }
    }

    fun goBack() {
        if (_uiState.value.selectedCategoryId == null) {
            onFinished()
        } else {
            leaveCategory()
        }
    }

    fun executeCard(card: ActionCard) {
        scope.launch {
            when (useCases.executeCard(card)) {
                ExecuteCardResult.OpenedUrl -> onFinished()
                ExecuteCardResult.CopiedText -> {
                    onTransientMessage("복사했습니다")
                    onFinished()
                }

                ExecuteCardResult.DisabledCard -> showMessage("비활성화된 카드입니다.")
                ExecuteCardResult.OpenUrlFailed -> showMessage("웹페이지를 열지 못했습니다.")
                ExecuteCardResult.CopyTextFailed -> showMessage("문구를 복사하지 못했습니다.")
            }
        }
    }

    fun messageShown() {
        _uiState.update { it.copy(message = null) }
    }

    fun clear() {
        cardsJob?.cancel()
        scope.cancel()
    }

    private fun leaveCategory() {
        cardsJob?.cancel()
        cardsJob = null
        _uiState.update {
            it.copy(
                selectedCategoryId = null,
                cards = emptyList(),
                message = null,
            )
        }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }
}