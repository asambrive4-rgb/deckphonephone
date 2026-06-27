package com.example.deckphonephone

import android.content.Context
import com.example.deckphonephone.deck.application.CreateCategoryUseCase
import com.example.deckphonephone.deck.application.CreateTextCardUseCase
import com.example.deckphonephone.deck.application.CreateWebCardUseCase
import com.example.deckphonephone.deck.application.ExecuteCardUseCase
import com.example.deckphonephone.deck.application.DeckUseCases
import com.example.deckphonephone.deck.application.ObserveCardsUseCase
import com.example.deckphonephone.deck.application.ObserveCategoriesUseCase
import com.example.deckphonephone.deck.data.local.DeckDatabase
import com.example.deckphonephone.deck.data.local.RoomDeckRepository
import com.example.deckphonephone.deck.platform.AndroidOpenUrlAdapter
import com.example.deckphonephone.deck.platform.DeferredPasteTextAdapter

class DeckAppContainer(context: Context) {
    private val database = DeckDatabase.get(context)
    private val repository = RoomDeckRepository(database.deckDao())
    private val openUrlAdapter = AndroidOpenUrlAdapter(context.applicationContext)
    private val pasteTextAdapter = DeferredPasteTextAdapter()

    val useCases = DeckUseCases(
        createCategory = CreateCategoryUseCase(repository),
        observeCategories = ObserveCategoriesUseCase(repository),
        createTextCard = CreateTextCardUseCase(repository),
        createWebCard = CreateWebCardUseCase(repository),
        observeCards = ObserveCardsUseCase(repository),
        executeCard = ExecuteCardUseCase(
            openUrlPort = openUrlAdapter,
            pasteTextPort = pasteTextAdapter,
        ),
    )
}
