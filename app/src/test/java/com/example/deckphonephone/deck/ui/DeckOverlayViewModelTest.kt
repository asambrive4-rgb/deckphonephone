package com.example.deckphonephone.deck.ui

import com.example.deckphonephone.deck.application.CopyTextPort
import com.example.deckphonephone.deck.application.CopyTextResult
import com.example.deckphonephone.deck.application.CreateCategoryUseCase
import com.example.deckphonephone.deck.application.CreateTextCardUseCase
import com.example.deckphonephone.deck.application.CreateWebCardUseCase
import com.example.deckphonephone.deck.application.DeckRepository
import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.application.DeleteCardUseCase
import com.example.deckphonephone.deck.application.DeleteCategoryUseCase
import com.example.deckphonephone.deck.application.ExecuteCardUseCase
import com.example.deckphonephone.deck.application.ObserveCardsUseCase
import com.example.deckphonephone.deck.application.ObserveCategoriesUseCase
import com.example.deckphonephone.deck.application.OpenUrlPort
import com.example.deckphonephone.deck.application.OpenUrlResult
import com.example.deckphonephone.deck.application.SetCardEnabledUseCase
import com.example.deckphonephone.deck.application.UpdateCategoryUseCase
import com.example.deckphonephone.deck.application.UpdateTextCardUseCase
import com.example.deckphonephone.deck.application.UpdateWebCardUseCase
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import com.example.deckphonephone.deck.domain.DeckCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckOverlayViewModelTest {
    @Test
    fun `selecting category stays loading until cards are emitted`() {
        val viewModel = DeckOverlayViewModel(
            useCases = SuspendedCardsRepository().toUseCases(),
            onFinished = {},
            onTransientMessage = {},
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.selectCategory(1L)

        val state = viewModel.uiState.value
        assertEquals(1L, state.selectedCategoryId)
        assertTrue(state.isCardsLoading)
        assertEquals(emptyList<ActionCard>(), state.cards)

        viewModel.clear()
    }
}

private class SuspendedCardsRepository : DeckRepository {
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
        return flow { awaitCancellation() }
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

private fun DeckRepository.toUseCases(): DeckUseCases {
    return DeckUseCases(
        createCategory = CreateCategoryUseCase(this),
        observeCategories = ObserveCategoriesUseCase(this),
        updateCategory = UpdateCategoryUseCase(this),
        deleteCategory = DeleteCategoryUseCase(this),
        createTextCard = CreateTextCardUseCase(this),
        createWebCard = CreateWebCardUseCase(this),
        observeCards = ObserveCardsUseCase(this),
        updateTextCard = UpdateTextCardUseCase(this),
        updateWebCard = UpdateWebCardUseCase(this),
        deleteCard = DeleteCardUseCase(this),
        setCardEnabled = SetCardEnabledUseCase(this),
        executeCard = ExecuteCardUseCase(
            openUrlPort = AlwaysSuccessfulOpenUrlPort,
            copyTextPort = AlwaysSuccessfulCopyTextPort,
        ),
    )
}

private object AlwaysSuccessfulOpenUrlPort : OpenUrlPort {
    override suspend fun openUrl(url: String): OpenUrlResult = OpenUrlResult.Success
}

private object AlwaysSuccessfulCopyTextPort : CopyTextPort {
    override suspend fun copyText(text: String): CopyTextResult = CopyTextResult.Success
}