package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.domain.ActionCard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckOverlayViewModel(
    private val useCases: DeckUseCases,
    private val onFinished: () -> Unit,
    private val onTransientMessage: (String) -> Unit,
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _uiState = MutableStateFlow(DeckOverlayUiState())
    val uiState = _uiState.asStateFlow()

    private var cardsJob: Job? = null

    init {
        scope.launch {
            useCases.observeCategories().collect { categories ->
                _uiState.update { state ->
                    state.copy(
                        categories = categories,
                        isCategoriesLoading = false,
                    )
                }
            }
        }
        scope.launch {
            useCases.observeConnectedBluetoothDevices().collect { devices ->
                _uiState.update { state ->
                    state.copy(connectedBluetoothDevices = devices)
                }
            }
        }
    }

    fun selectCategory(categoryId: Long) {
        _uiState.update {
            it.copy(
                cards = emptyList(),
                isCardsLoading = true,
                message = null,
            )
        }

        cardsJob?.cancel()
        cardsJob = scope.launch {
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

    fun goBack() {
        if (_uiState.value.selectedCategoryId == null) {
            onFinished()
        } else {
            leaveCategory()
        }
    }

    fun close() {
        onFinished()
    }

    fun executeCard(card: ActionCard) {
        scope.launch {
            val feedback = useCases.executeCard(card).toOverlayExecutionFeedback()
            feedback.message?.let { message ->
                if (feedback.isTransientMessage) {
                    onTransientMessage(message)
                } else {
                    showMessage(message)
                }
            }
            if (feedback.shouldFinish) {
                onFinished()
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
                isCardsLoading = false,
                message = null,
            )
        }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }
}
