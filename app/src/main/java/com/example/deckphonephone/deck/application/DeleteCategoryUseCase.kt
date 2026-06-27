package com.example.deckphonephone.deck.application

class DeleteCategoryUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(categoryId: Long) {
        repository.deleteCategory(categoryId)
    }
}