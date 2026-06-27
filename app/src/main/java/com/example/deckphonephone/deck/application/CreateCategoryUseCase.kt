package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.DeckCategory

class CreateCategoryUseCase(
    private val repository: DeckRepository,
) {
    suspend operator fun invoke(name: String): DeckResult<DeckCategory> {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return DeckResult.Failure(DeckError.CategoryNameBlank)
        }

        return DeckResult.Success(repository.createCategory(name = trimmedName))
    }
}
