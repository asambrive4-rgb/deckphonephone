package com.example.deckphonephone

import android.content.Context
import com.example.deckphonephone.deck.application.CreateCategoryUseCase
import com.example.deckphonephone.deck.application.CreateTextCardUseCase
import com.example.deckphonephone.deck.application.CreateWebCardUseCase
import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.application.DeleteCardUseCase
import com.example.deckphonephone.deck.application.DeleteCategoryUseCase
import com.example.deckphonephone.deck.application.ExecuteCardUseCase
import com.example.deckphonephone.deck.application.ObserveCardsUseCase
import com.example.deckphonephone.deck.application.ObserveCategoriesUseCase
import com.example.deckphonephone.deck.application.SetCardEnabledUseCase
import com.example.deckphonephone.deck.application.UpdateCategoryUseCase
import com.example.deckphonephone.deck.application.UpdateTextCardUseCase
import com.example.deckphonephone.deck.application.UpdateWebCardUseCase
import com.example.deckphonephone.deck.data.local.DeckDatabase
import com.example.deckphonephone.deck.data.local.RoomDeckRepository
import com.example.deckphonephone.deck.platform.AndroidClipboardCopyTextAdapter
import com.example.deckphonephone.deck.platform.AndroidOpenUrlAdapter

class DeckAppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = DeckDatabase.get(appContext)
    private val repository = RoomDeckRepository(database.deckDao())
    private val openUrlAdapter = AndroidOpenUrlAdapter(appContext)
    private val copyTextAdapter = AndroidClipboardCopyTextAdapter(appContext)

    val useCases = DeckUseCases(
        createCategory = CreateCategoryUseCase(repository),
        observeCategories = ObserveCategoriesUseCase(repository),
        updateCategory = UpdateCategoryUseCase(repository),
        deleteCategory = DeleteCategoryUseCase(repository),
        createTextCard = CreateTextCardUseCase(repository),
        createWebCard = CreateWebCardUseCase(repository),
        observeCards = ObserveCardsUseCase(repository),
        updateTextCard = UpdateTextCardUseCase(repository),
        updateWebCard = UpdateWebCardUseCase(repository),
        deleteCard = DeleteCardUseCase(repository),
        setCardEnabled = SetCardEnabledUseCase(repository),
        executeCard = ExecuteCardUseCase(
            openUrlPort = openUrlAdapter,
            copyTextPort = copyTextAdapter,
        ),
    )
}