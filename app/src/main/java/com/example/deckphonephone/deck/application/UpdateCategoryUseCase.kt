package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.DeckCategory

class UpdateCategoryUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(
        category: DeckCategory,
        name: String,
    ): DeckResult<DeckCategory> {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return DeckResult.Failure(DeckError.CategoryNameBlank)
        }

        return DeckResult.Success(
            repository.updateCategory(
                category.copy(name = trimmedName),
            ),
        )
    }
}