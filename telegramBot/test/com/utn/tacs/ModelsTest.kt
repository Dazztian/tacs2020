package com.utn.tacs

import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import com.github.kotlintelegrambot.entities.*

class ModelsTest {
    @Test
    fun countriesListTest(){
        val country = CountriesList("listId", "userId", "list name", setOf("Argentina", "Chile"), LocalDate.now())

        assertEquals(InlineKeyboardButton("list name", callbackData="Check_list listId"), country.toButton())
        country._id
        country.countries
        country.creationDate
        country.name
        country.userId
    }

    @Test
    fun countriesTest(){
        val country = Country("listId", "region", "lastUpdate", null,
            CountryCode("iso2", "iso3"), 0,0,0, emptyList())
        country._id
        country.confirmed
        country.countrycode
        country.countryregion
        country.deaths
        country.lastupdate
        country.location
        country.recovered
        country.timeseries
    }

    @Test
    fun timeSerieTest(){
        val timeSerie = TimeSerie(0,0,0,0, "date")
        timeSerie.tableHeader()
        timeSerie.confirmed
        timeSerie.date
        timeSerie.deaths
        timeSerie.number
        timeSerie.recovered
    }

    @Test
    fun userCountriesListResponseTest(){
        val userCountriesListResponse = UserCountriesListResponse("id", "name", mutableSetOf())
        userCountriesListResponse.countries
        userCountriesListResponse.id
        userCountriesListResponse.name
    }

    @Test
    fun telegramMessageWrapperTest(){
        val telegramMessageWrapper = TelegramMessageWrapper(0, "name")
        telegramMessageWrapper.chatId
        telegramMessageWrapper.text
    }

    @Test
    fun userNamesResponseTest(){
        val userNamesResponse = CountryNamesResponse("name", "iso2")
        userNamesResponse.iso2
        userNamesResponse.name
    }

    @Test
    fun countryResponseTimeseriesTest(){
        val countryResponseTimeseries = CountryResponseTimeseries("name", "lastUpdate", null,
            CountryCode("iso2", "iso3"), 0,0,0,null, null)
        countryResponseTimeseries.confirmed
        countryResponseTimeseries.countrycode
        countryResponseTimeseries.countryregion
        countryResponseTimeseries.deaths
        countryResponseTimeseries.lastupdate
        countryResponseTimeseries.location
        countryResponseTimeseries.timeSeriesTotal
        countryResponseTimeseries.timeseries
        countryResponseTimeseries.recovered
    }

    @Test
    fun timeSeriesTotalTest(){
        val timeSeriesTotal = TimeSeriesTotal(0,0,0)
        timeSeriesTotal.confirmed
        timeSeriesTotal.deaths
        timeSeriesTotal.recovered
    }
}