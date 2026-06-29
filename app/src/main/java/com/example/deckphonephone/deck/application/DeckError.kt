package com.example.deckphonephone.deck.application

sealed interface DeckError {
    data object CategoryNameBlank : DeckError
    data object ActionCardTitleBlank : DeckError
    data object TextBlank : DeckError
    data object UrlBlank : DeckError
    data object InvalidUrl : DeckError
    data object CategoryNotSelected : DeckError
    data object BluetoothDeviceNotSelected : DeckError
    data object BluetoothDeviceAddressBlank : DeckError
}