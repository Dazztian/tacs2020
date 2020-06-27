package com.utn.tacs

import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

class RequestsTests {
    private val listId :String = "123"
    private val telegramUserId :String = "123"
    private val username = "username"
    private val password = "pass"
    private val firstName :String = "FirstName"

    @Before
    fun beforeAll() {
        mockkStatic(RequestManager::class)
        mockkObject(RequestManager)
    }

    @Test
    fun healthCheckTest(){
        every { RequestManager.getResponse(urlBase + "configuration") } returns Response(Status(200,""), "Application running")
        assertEquals(true, RequestManager.healthCheck())

        every { RequestManager.getResponse(urlBase + "configuration") } returns Response(Status(400,""), "")
        assertEquals(false, RequestManager.healthCheck())

        every { RequestManager.getResponse(urlBase + "configuration") } throws Exception()
        assertEquals(false, RequestManager.healthCheck())
    }

    @Test
    fun isLoggedInTest(){
        every { RequestManager.getResponse(urlBase+"api/telegram?telegramId=$telegramUserId") } returns Response(Status(200,""), "")
        assertEquals(true, RequestManager.isLoggedIn(telegramUserId))

        every { RequestManager.getResponse(urlBase+"api/telegram?telegramId=$telegramUserId") } returns Response(Status(400,""), "")
        assertEquals(false, RequestManager.isLoggedIn(telegramUserId))

        every { RequestManager.getResponse(urlBase+"api/telegram?telegramId=$telegramUserId") } throws Exception()
        assertEquals(false, RequestManager.isLoggedIn(telegramUserId))
    }

    @Test
    fun loginTest(){
        val telegramUser = TelegramUser(telegramUserId, username, password)
        every { RequestManager.getResponse(urlBase+"api/telegram/login", Gson().toJson(telegramUser).toString()) } returns Response(Status(200,""), "")
        assertEquals(true, RequestManager.login(username, password, telegramUserId))

        every { RequestManager.getResponse(urlBase+"api/telegram/login", Gson().toJson(telegramUser).toString()) } returns Response(Status(400,""), "")
        assertEquals(false, RequestManager.login(username, password, telegramUserId))

        every { RequestManager.getResponse(urlBase+"api/telegram/login", Gson().toJson(telegramUser).toString()) } throws Exception()
        assertEquals(false, RequestManager.login(username, password, telegramUserId))
    }

    @Test
    fun logoutTest(){
        val telegramUser = TelegramUser(telegramUserId, null, null)
        every { RequestManager.getResponse(urlBase+"api/telegram/logout", Gson().toJson(telegramUser).toString()) } returns Response(Status(200,""), "")
        assertEquals(true, RequestManager.logout(telegramUserId))

        every { RequestManager.getResponse(urlBase+"api/telegram/logout", Gson().toJson(telegramUser).toString()) } returns Response(Status(400,""), "")
        assertEquals(false, RequestManager.logout(telegramUserId))

        every { RequestManager.getResponse(urlBase+"api/telegram/logout", Gson().toJson(telegramUser).toString()) } throws Exception()
        assertEquals(false, RequestManager.logout(telegramUserId))
    }

    @Test
    fun getCountryListsTest(){
        val countriesList = arrayOf(CountriesList("id", "userId", "name", emptySet(), LocalDate.now()))

        every { RequestManager.getResponse(urlBase+"api/telegram/countryList?telegramId=$telegramUserId") } returns Response(Status(200,""), Gson().toJson(countriesList).toString())
        assertEquals(countriesList.toList(), RequestManager.getCountryLists(telegramUserId))

        every { RequestManager.getResponse(urlBase+"api/telegram/countryList?telegramId=$telegramUserId") } returns Response(Status(200,""), "[]")
        assertEquals(emptyList(), RequestManager.getCountryLists(telegramUserId))

        every { RequestManager.getResponse(urlBase+"api/telegram/countryList?telegramId=$telegramUserId") } throws Exception()
        assertEquals(emptyList(), RequestManager.getCountryLists(telegramUserId))
    }

    @Test
    fun getCountryByNameTest(){
        val country = Country("id", "Argentina", null,null,null,0,0,0, emptyList())
        val name = "Argentina"

        every { RequestManager.getResponse(urlBase+"api/countries?name=$name") } returns Response(Status(200,""), Gson().toJson(country).toString())
        assertEquals(country, RequestManager.getCountryByName(name))

        every { RequestManager.getResponse(urlBase+"api/countries?name=$name") } returns Response(Status(400,""), "")
        assertEquals(null, RequestManager.getCountryByName(name))

        every { RequestManager.getResponse(urlBase+"api/countries?name=$name") } throws Exception()
        assertEquals(null, RequestManager.getCountryByName(name))
    }

    @Test
    fun allCountriesNamesTest(){
        val countryNames = arrayOf(CountryNamesResponse("Argentina", "AR"))

        every { RequestManager.getResponse(urlBase+"api/countries/names") } returns Response(Status(200,""), Gson().toJson(countryNames).toString())
        assertEquals("Argentina", RequestManager.allCountriesNames()[0].name)

        every { RequestManager.getResponse(urlBase+"api/countries/names") } returns Response(Status(400,""), "")
        assertEquals(emptyList(), RequestManager.allCountriesNames().toList())

        every { RequestManager.getResponse(urlBase+"api/countries/names") } throws Exception()
        assertEquals(emptyList(), RequestManager.allCountriesNames().toList())
    }

    @Test
    fun getListCountriesTest(){
        val countries = arrayOf(Country("Argentina", "AR", null, null,null,null,null,null))

        every { RequestManager.getResponse(urlBase+"api/telegram/countryList/$listId?telegramId=$telegramUserId") } returns Response(Status(200,""), Gson().toJson(countries).toString())
        assertEquals(countries.toList(), RequestManager.getListCountries(listId, telegramUserId))

        every { RequestManager.getResponse(urlBase+"api/telegram/countryList/$listId?telegramId=$telegramUserId") } returns Response(Status(400,""), "")
        assertEquals(emptyList(), RequestManager.getListCountries(listId, telegramUserId))

        every { RequestManager.getResponse(urlBase+"api/telegram/countryList/$listId?telegramId=$telegramUserId") } throws Exception()
        assertEquals(emptyList(), RequestManager.getListCountries(listId, telegramUserId))
    }

    @Test
    fun addCountriesTest(){
        val countries = setOf("Argentina", "Chile")

        every { RequestManager.getResponse(urlBase+"api/telegram/countryList/$listId/add?telegramId=$telegramUserId",
            Gson().toJson(UserCountriesListModificationRequest("list", countries.toMutableSet())).toString()) } returns Response(Status(200,"OK"), "OK")
        assertEquals("OK", RequestManager.addCountries(listId, telegramUserId, countries))

        every { RequestManager.getResponse(urlBase+"api/telegram/countryList/$listId/add?telegramId=$telegramUserId",
            Gson().toJson(UserCountriesListModificationRequest("list", countries.toMutableSet())).toString()) } throws Exception()
        assertEquals("Error", RequestManager.addCountries(listId, telegramUserId, countries))
    }

    @Test
    fun newCountriesListTest(){
        val listName = "name"
        val countries = listOf("Argentina", "Brazil")
        val userCountriesListModificationRequest = UserCountriesListModificationRequest(listName, countries.toMutableSet())

        every { RequestManager.getResponse(urlBase+"api/telegram/countryList?telegramId=$telegramUserId",
            Gson().toJson(userCountriesListModificationRequest).toString()) } returns Response(Status(200,""), Gson().toJson(UserCountriesListResponse("123", "name", mutableSetOf(CountryNamesResponse("Argentina", "AR"), CountryNamesResponse("Brazil", "BR")))).toString())
        assertEquals("OK 123", RequestManager.newCountriesList(telegramUserId, listName, countries))

        every { RequestManager.getResponse(urlBase+"api/telegram/countryList?telegramId=$telegramUserId",
            Gson().toJson(userCountriesListModificationRequest).toString()) } returns Response(Status(400,"Error msg"), "")
        assertEquals("Error msg", RequestManager.newCountriesList(telegramUserId, listName, countries))

        every { RequestManager.getResponse(urlBase+"api/telegram/countryList?telegramId=$telegramUserId",
            Gson().toJson(userCountriesListModificationRequest).toString()) } throws Exception()
        assertEquals("Error", RequestManager.newCountriesList(telegramUserId, listName, countries))
    }

    @Test
    fun getTimesesiesListTest(){
        val minusDays :Long = 1
        val today = LocalDateTime.now()
        val toDateString = today.format(DateTimeFormatter.ofPattern("MM/dd/yy"))
        val fromDateString = today.minusDays(minusDays).format(DateTimeFormatter.ofPattern("MM/dd/yy"))
        val url = urlBase+"api/telegram/countryList/$listId/timeseries?" +
                "toDate=$toDateString" +
                "&fromDate=$fromDateString"
        val response = listOf(CountryResponseTimeseries("Argentina", null,null,null,0,0,0,null,null))

        every { RequestManager.getResponse(url) } returns Response(Status(200,""), Gson().toJson(response).toString())
        assertEquals(response, RequestManager.getTimesesiesList(telegramUserId, minusDays))

        every { RequestManager.getResponse(url) } returns Response(Status(400,"Error msg"), "")
        assertEquals(emptyList(), RequestManager.getTimesesiesList(telegramUserId, minusDays))

        every { RequestManager.getResponse(url) } throws Exception()
        assertEquals(emptyList(), RequestManager.getTimesesiesList(telegramUserId, minusDays))
    }
}