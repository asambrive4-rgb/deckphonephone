package com.example.deckphonephone.deck.application

data class DeckUseCases(
    val createCategory: CreateCategoryUseCase,
    val observeCategories: ObserveCategoriesUseCase,
    val createTextCard: CreateTextCardUseCase,
    val createWebCard: CreateWebCardUseCase,
    val observeCards: ObserveCardsUseCase,
    val executeCard: ExecuteCardUseCase,
)
