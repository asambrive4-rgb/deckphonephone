package com.example.deckphonephone.deck.application

sealed interface DeckResult<out T> {
    data class Success<T>(val value: T) : DeckResult<T>
    data class Failure(val error: DeckError) : DeckResult<Nothing>
}
