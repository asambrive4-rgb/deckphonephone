package com.example.deckphonephone.deck.application

import com.example.deckphonephone.deck.domain.CardAction
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateUseCaseTest {
    private val repository = FakeDeckRepository()
    private val createCategory = CreateCategoryUseCase(repository)
    private val createTextCard = CreateTextCardUseCase(repository)
    private val createWebCard = CreateWebCardUseCase(repository)
    private val createBluetoothDeviceCard = CreateBluetoothDeviceCardUseCase(repository)

    @Test
    fun `category name is required`() = runBlocking {
        val result = createCategory(" ")

        assertEquals(DeckResult.Failure(DeckError.CategoryNameBlank), result)
    }

    @Test
    fun `card title is required for text card`() = runBlocking {
        val result = createTextCard(categoryId = 1, title = "", text = "hello")

        assertEquals(DeckResult.Failure(DeckError.CardTitleBlank), result)
    }

    @Test
    fun `text payload is required`() = runBlocking {
        val result = createTextCard(categoryId = 1, title = "Reply", text = " ")

        assertEquals(DeckResult.Failure(DeckError.TextBlank), result)
    }

    @Test
    fun `web url is required`() = runBlocking {
        val result = createWebCard(categoryId = 1, title = "Search", rawUrl = "")

        assertEquals(DeckResult.Failure(DeckError.UrlBlank), result)
    }

    @Test
    fun `web url must be valid`() = runBlocking {
        val result = createWebCard(categoryId = 1, title = "Bad", rawUrl = "ftp://example.com")

        assertEquals(DeckResult.Failure(DeckError.InvalidUrl), result)
    }

    @Test
    fun `web card stores normalized url`() = runBlocking {
        val result = createWebCard(categoryId = 7, title = "GitHub", rawUrl = "github.com")

        assertTrue(result is DeckResult.Success)
        val card = (result as DeckResult.Success).value
        assertEquals(CardAction.OpenUrl("https://github.com"), card.action)
    }

    @Test
    fun `bluetooth card stores selected device`() = runBlocking {
        val device = PairedBluetoothDevice(
            name = "Buds",
            address = "AC:80:0A:20:CB:AF",
        )

        val result = createBluetoothDeviceCard(
            categoryId = 7,
            title = "이어폰",
            device = device,
        )

        assertTrue(result is DeckResult.Success)
        val card = (result as DeckResult.Success).value
        assertEquals(
            CardAction.BluetoothDevice(
                deviceName = "Buds",
                deviceAddress = "AC:80:0A:20:CB:AF",
            ),
            card.action,
        )
    }

    @Test
    fun `bluetooth card requires title`() = runBlocking {
        val result = createBluetoothDeviceCard(
            categoryId = 1,
            title = " ",
            device = PairedBluetoothDevice("Buds", "AC:80:0A:20:CB:AF"),
        )

        assertEquals(DeckResult.Failure(DeckError.CardTitleBlank), result)
    }

    @Test
    fun `bluetooth card requires selected device`() = runBlocking {
        val result = createBluetoothDeviceCard(
            categoryId = 1,
            title = "이어폰",
            device = null,
        )

        assertEquals(DeckResult.Failure(DeckError.BluetoothDeviceNotSelected), result)
    }

    @Test
    fun `bluetooth card requires device address`() = runBlocking {
        val result = createBluetoothDeviceCard(
            categoryId = 1,
            title = "이어폰",
            device = PairedBluetoothDevice("Buds", " "),
        )

        assertEquals(DeckResult.Failure(DeckError.BluetoothDeviceAddressBlank), result)
    }
}