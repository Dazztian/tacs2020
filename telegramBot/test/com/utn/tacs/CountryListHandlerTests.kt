package com.utn.tacs

import com.github.kotlintelegrambot.entities.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.utn.tacs.handlers.*
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CountryListHandlerTests {
    private val chatId :Long = 123
    private val listId :String = "ListId"

    @Before
    fun beforeAll() {
        mockkStatic(RequestManager::class)
        mockkObject(RequestManager)

        every { RequestManager.healthCheck() } returns true
        every { RequestManager.isLoggedIn(chatId.toString()) } returns true

        lastImportantMessages.clear()
    }

    @Test
    fun countriesCommandTest(){
        every { RequestManager.allCountriesNames() } returns arrayOf("Argentina","Chile")

        assertEquals(listOf(TelegramMessageWrapper(chatId, acceptedCountriesText + "Argentina\nChile")),
            countriesCommand(chatId)
        )
    }

    @Test
    fun myListsCommandTest(){
        every { RequestManager.getCountryLists(chatId.toString()) } returns emptyList()

        assertEquals(listOf(TelegramMessageWrapper(chatId, textNoLists, replyMarkup = returnButton())),
            myListsCommand(chatId)
        )

        every { RequestManager.getCountryLists(chatId.toString()) } returns listOf(CountriesList("_id", "userId", "name", emptySet(), null))

        assertEquals(listOf(TelegramMessageWrapper(
            chatId, myListsText,
            replyMarkup = InlineKeyboardMarkup(listOf(listOf(InlineKeyboardButton("name", callbackData = "Check_list _id")))+ newListButtonNoMarkup())
        )),
            myListsCommand(chatId)
        )
    }

    @Test
    fun showListTest(){
        every { RequestManager.getListCountries(listId, chatId.toString()) } returns emptyList()

        assertEquals(listOf(TelegramMessageWrapper(chatId, textNoCountries, replyMarkup = InlineKeyboardMarkup(
            listButtonsNoMarkup(listId)
        )
        )),
            showList(listId, chatId)
        )

        every { RequestManager.getListCountries(listId, chatId.toString()) } returns listOf(
            Country("_id", "Argentina", null, null, null, 100, 200, 300, null),
            Country("_id", "Brazil", null, null, null, 500, 600, 700, null))

        assertEquals(listOf(TelegramMessageWrapper(chatId,
            "<pre>\n" +
                    "|     Name     | Confirmed |  Deaths  | Recovered |\n" +
                    "|:------------:|:---------:|:--------:|:---------:|\n" +
                    "| Argentina    | 100       | 200      | 300       |\n" +
                    "| Brazil       | 500       | 600      | 700       |\n" +
                    "</pre>",
            parseMode= ParseMode.HTML,
            replyMarkup = InlineKeyboardMarkup(listButtonsNoMarkup(listId))
        )),
            showList(listId, chatId)
        )
    }

    @Test
    fun addCountryCommandTest(){
        assertEquals(listOf(TelegramMessageWrapper(chatId, addCountryText)),
            addCountryCommand(chatId, listId)
        )
        assertEquals(PreviousMessageWrapper(MessageType.ADD_COUNTRY, listId).countryListId, lastImportantMessages[chatId]!!.countryListId)
        assertEquals(PreviousMessageWrapper(MessageType.ADD_COUNTRY, listId).messageType, lastImportantMessages[chatId]!!.messageType)
    }

    @Test
    fun addListCommandTest(){
        assertEquals(listOf(TelegramMessageWrapper(chatId, createListText)),
            addListCommand(chatId)
        )
        assertEquals(PreviousMessageWrapper(MessageType.NEW_LIST, listId).messageType, lastImportantMessages[chatId]!!.messageType)
    }

    @Test
    fun messageCommandAddCountryTest(){
        assertEquals(emptyList(), messageCommand(chatId, chatId, ""))
        val countriesList = setOf("Argentina", "Brazil")

        every { RequestManager.addCountries(chatId.toString(), listId, countriesList) } returns "Saved"
        every { RequestManager.getListCountries(listId, chatId.toString()) } returns listOf(
            Country("_id", "Argentina", null, null, null, 100, 200, 300, null),
            Country("_id", "Brazil", null, null, null, 500, 600, 700, null))
        addCountryCommand(chatId, listId)

        assertEquals(listOf(TelegramMessageWrapper(chatId,
            "<pre>\n" +
                    "|     Name     | Confirmed |  Deaths  | Recovered |\n" +
                    "|:------------:|:---------:|:--------:|:---------:|\n" +
                    "| Argentina    | 100       | 200      | 300       |\n" +
                    "| Brazil       | 500       | 600      | 700       |\n" +
                    "</pre>",
            parseMode= ParseMode.HTML,
            replyMarkup = InlineKeyboardMarkup(listButtonsNoMarkup(listId))
        )),
            messageCommand(chatId, chatId, "Argentina\nBrazil")
        )
        assertEquals(0, lastImportantMessages.size)

        every { RequestManager.addCountries(chatId.toString(), listId, countriesList) } returns "ERROR"

        addCountryCommand(chatId, listId)
        assertEquals(listOf(TelegramMessageWrapper(chatId, "ERROR")), messageCommand(chatId, chatId, "Argentina\nBrazil"))
    }

    @Test
    fun messageCommandAddListTest(){
        assertEquals(emptyList(), messageCommand(chatId, chatId, ""))
        val countriesList = listOf("Argentina", "Brazil")

        every { RequestManager.newCountriesList(chatId.toString(), "name", countriesList) } returns "OK $listId"
        every { RequestManager.getListCountries(listId, chatId.toString()) } returns listOf(
            Country("_id", "Argentina", null, null, null, 100, 200, 300, null),
            Country("_id", "Brazil", null, null, null, 500, 600, 700, null))
        addListCommand(chatId)

        assertEquals(listOf(TelegramMessageWrapper(chatId,
            "<pre>\n" +
                    "|     Name     | Confirmed |  Deaths  | Recovered |\n" +
                    "|:------------:|:---------:|:--------:|:---------:|\n" +
                    "| Argentina    | 100       | 200      | 300       |\n" +
                    "| Brazil       | 500       | 600      | 700       |\n" +
                    "</pre>",
            parseMode= ParseMode.HTML,
            replyMarkup = InlineKeyboardMarkup(listButtonsNoMarkup(listId))
        )),
            messageCommand(chatId, chatId, "name\nArgentina\nBrazil")
        )
        assertEquals(0, lastImportantMessages.size)

        every { RequestManager.newCountriesList(chatId.toString(), "name", countriesList) } returns "ERROR"

        addListCommand(chatId)
        assertEquals(listOf(TelegramMessageWrapper(chatId, "ERROR")), messageCommand(chatId, chatId, "name\nArgentina\nBrazil"))
    }

    @Test
    fun checkCommandTest(){
        every { RequestManager.getCountryByName("Argentina") } returns Country(null, "Argentina", null, null, null, 50, 60, 70, null)

        assertEquals(listOf(TelegramMessageWrapper(
            chatId = chatId,
            parseMode = ParseMode.HTML,
            text = "<pre>\n" +
                    "|     Name     | Confirmed |  Deaths  | Recovered |\n" +
                    "|:------------:|:---------:|:--------:|:---------:|\n" +
                    "| Argentina    | 50        | 60       | 70        |\n" +
                    "</pre>")),
            checkCommand(chatId, listOf("Argentina"))
        )
    }
}