package com.utn.tacs.countries

import com.utn.tacs.*
import io.ktor.features.BadRequestException
import io.ktor.features.NotFoundException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.bytebuddy.pool.TypePool
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.litote.kmongo.newId
import kotlin.test.assertTrue

class CountriesServiceTest {

    private val countriesRepository = mockk<CountriesRepository>()

    @Test
    fun testGetAllCountries() {
        val country1 = Country(newId(), "region1", "2020-05-05", Location(25.0, 25.0), CountryCode("ISO21", "ISO31"), 1000, 960, 40)
        val country2 = Country(newId(), "region2", "2020-05-06", Location(35.0, 2445.0), CountryCode("ISO22", "ISO33"), 424000, 9610, 340)
        val country3 = Country(newId(), "region3", "2020-05-07", Location(505.0, 235.0), CountryCode("ISO23", "ISO33"), 10030, 9604, 430)

        coEvery { countriesRepository.getCountries() } returns listOf(country1, country2, country3)

        val service = CountriesService(countriesRepository)

        assertEquals(listOf(CountryResponse(country1), CountryResponse(country2), CountryResponse(country3)), runBlocking { service.getAllCountries() })
    }

    @Test
    fun testGetNearestCountries() {
        val country1 = Country(newId(), "region1", "2020-05-05", Location(25.0, 25.0), CountryCode("AR", "ISO31"), 1000, 960, 40)
        val country2 = Country(newId(), "region2", "2020-05-06", Location(35.0, 2445.0), CountryCode("UY", "ISO33"), 424000, 9610, 340)
        val country3 = Country(newId(), "region3", "2020-05-07", Location(505.0, 235.0), CountryCode("CN", "ISO33"), 10030, 9604, 430)

        coEvery { countriesRepository.getCountries() } returns listOf(country1, country2, country3)

        val service = CountriesService(countriesRepository)
        var response: List<CountryResponse> = listOf()
        runBlocking { response = service.getNearestCountries(25.0, 25.0) }

        assertEquals(2, response.size)
        assertEquals("AR", response.first().countrycode?.iso2)
        assertEquals("CN", response.last().countrycode?.iso2)
    }

    @Test
    fun testGetLatestCountryByIsoCode() {
        val country1 = Country(newId(), "region1", "2020-05-05", Location(25.0, 25.0), CountryCode("ISO21", "ISO31"), 1000, 960, 40)

        coEvery { countriesRepository.getCountry("ISO21") } returns country1

        val service = CountriesService(countriesRepository)

        assertEquals(CountryResponse(country1), runBlocking { service.getCountryLatestByIsoCode("ISO21") })
    }

    @Test
    fun testGetLatestCountryByIsoCodeWithIllegalArgument() {
        coEvery { countriesRepository.getCountry("ISO21") } throws IllegalArgumentException()
        val service = CountriesService(countriesRepository)
        assertThrows<NotFoundException> { runBlocking { service.getCountryLatestByIsoCode("ISO21") } }
    }

    @Test
    fun testGetCountryLatestByName() {
        val country1 = Country(newId(), "region1", "2020-05-05", Location(25.0, 25.0), CountryCode("ISO21", "ISO31"), 1000, 960, 40)

        coEvery { countriesRepository.getCountryByName("Name") } returns country1
        val service = CountriesService(countriesRepository)
        assertEquals(CountryResponse(country1), runBlocking { service.getCountryLatestByName("NAME") })
    }

    @Test
    fun testGetCountryLatestByMailFailsWithNotFound() {
        coEvery { countriesRepository.getCountryByName("Name") } throws IllegalArgumentException()
        val service = CountriesService(countriesRepository)
        assertThrows<NotFoundException> { runBlocking { service.getCountryLatestByName("NAME") } }
    }

     @Test
     fun testGetCountriesByName(){
         val country1 = Country(newId(), "region1", "2020-05-05", Location(25.0, 25.0), CountryCode("ISO21", "ISO31"), 1000, 960, 40)
         val country2 = Country(newId(), "region2", "2020-05-06", Location(35.0, 2445.0), CountryCode("ISO22", "ISO33"), 424000, 9610, 340)
         val country3 = Country(newId(), "region3", "2020-05-07", Location(505.0, 235.0), CountryCode("ISO23", "ISO33"), 10030, 9604, 430)

         val names = listOf("name1", "name2")
         coEvery { countriesRepository.getCountriesByName(names) } returns listOf(country1, country2, country3)

         val service = CountriesService(countriesRepository)

         assertEquals(listOf(CountryResponse(country1), CountryResponse(country2), CountryResponse(country3)), runBlocking { service.getCountriesByName(names) })
     }

    @Test
    fun testGetCountriesByNameFails(){
        val names = listOf("name1", "name2")
        coEvery { countriesRepository.getCountriesByName(names) } throws NotFoundException()

        val service = CountriesService(countriesRepository)
        assertThrows<NotFoundException> { runBlocking { service.getCountriesByName(names)} }
    }

    @Test
    fun testGetCountriesTimeSeries_ByDays(){
        val country1Code = "AR"
        val country2Code = "BR"
        val fromDay = 2
        val toDay = 3
        val country1 = Country(newId(), "Argentina", "2020-05-05", Location(25.0, 25.0), CountryCode("AR", "ISO31"), 1, 1, 1)
        val country2 = Country(newId(), "Brasil", "2020-05-06", Location(35.0, 2445.0), CountryCode("BR", "ISO33"), 1, 1, 1)

        coEvery { countriesRepository.getCountry(country1Code) } returns country1
        coEvery { countriesRepository.getCountry(country2Code) } returns country2

        val service = CountriesService(countriesRepository)

        var response: List<CountryResponse> = listOf()
        runBlocking { response = service.getCountryTimesSeries(listOf(country1Code,country2Code), fromDay, toDay, null, null) }

        assertEquals(country1Code, response.first().countrycode!!.iso2)
        assertEquals("Argentina", response.first().countryregion)
        assertEquals(listOf(TimeSeries(2,1,0,0,"3/4/20"),TimeSeries(3,1,0,0,"3/5/20")) ,response.first().timeseries!!)
        assertEquals(6, response.first().offset)

        assertEquals(country2Code, response.last().countrycode!!.iso2)
        assertEquals("Brasil", response.last().countryregion)
        assertEquals(listOf(TimeSeries(2,1,0,0,"2/27/20"),TimeSeries(3,1,0,0,"2/28/20")) ,response.last().timeseries)
        assertEquals(0, response.last().offset)
    }

    @Test
    fun testGetCountriesTimeSeries_ByDatesWrongFormar_exceptionThrows(){
        val country1Code = "AR"
        val country2Code = "BR"
        val fromDate = "3/4/20"
        val toDate = "pepe"
        val country1 = Country(newId(), "Argentina", "2020-05-05", Location(25.0, 25.0), CountryCode("AR", "ISO31"), 1, 1, 1)
        val country2 = Country(newId(), "Brasil", "2020-05-06", Location(35.0, 2445.0), CountryCode("BR", "ISO33"), 1, 1, 1)

        coEvery { countriesRepository.getCountry(country1Code) } returns country1
        coEvery { countriesRepository.getCountry(country2Code) } returns country2

        val service = CountriesService(countriesRepository)
        assertThrows<BadRequestException> { runBlocking { service.getCountryTimesSeries(listOf(country1Code,country2Code), null, null, fromDate, toDate) }}
    }

    @Test
    fun testGetCountriesTimeSeries_ByDates(){
        val country1Code = "AR"
        val country2Code = "BR"
        val fromDate = "3/4/20"
        val toDate = "3/5/20"
        val country1 = Country(newId(), "Argentina", "2020-05-05", Location(25.0, 25.0), CountryCode("AR", "ISO31"), 1, 1, 1)
        val country2 = Country(newId(), "Brasil", "2020-05-06", Location(35.0, 2445.0), CountryCode("BR", "ISO33"), 1, 1, 1)

        coEvery { countriesRepository.getCountry(country1Code) } returns country1
        coEvery { countriesRepository.getCountry(country2Code) } returns country2

        val service = CountriesService(countriesRepository)

        var response: List<CountryResponse> = listOf()
        runBlocking { response = service.getCountryTimesSeries(listOf(country1Code,country2Code), null, null, fromDate, toDate) }

        assertEquals(country1Code, response.first().countrycode!!.iso2)
        assertEquals("Argentina", response.first().countryregion)
        assertEquals(listOf(TimeSeries(2,1,0,0,"3/4/20"),TimeSeries(3,1,0,0,"3/5/20")) ,response.first().timeseries!!)
        assertEquals(0, response.first().offset)

        assertEquals(country2Code, response.last().countrycode!!.iso2)
        assertEquals("Brasil", response.last().countryregion)
        assertEquals(listOf(TimeSeries(8,4,0,0,"3/4/20"),TimeSeries(9,4,0,0,"3/5/20")) ,response.last().timeseries)
        assertEquals(0, response.last().offset)
    }

    @Test
    fun testGetCountriesTimeSeries(){
        val country1Code = "AR"
        val country2Code = "BR"
        val country1 = Country(newId(), "Argentina", "2020-05-05", Location(25.0, 25.0), CountryCode("AR", "ISO31"), 1, 1, 1)
        val country2 = Country(newId(), "Brasil", "2020-05-06", Location(35.0, 2445.0), CountryCode("BR", "ISO33"), 1, 1, 1)

        coEvery { countriesRepository.getCountry(country1Code) } returns country1
        coEvery { countriesRepository.getCountry(country2Code) } returns country2

        val service = CountriesService(countriesRepository)

        var response: List<CountryResponse> = listOf()
        runBlocking { response = service.getCountryTimesSeries(listOf(country1Code,country2Code)) }

        assertEquals(country1Code, response.first().countrycode!!.iso2)
        assertEquals("Argentina", response.first().countryregion)
        assertEquals(0 ,response.first().timeseries!!.size)
        assertEquals(0, response.first().offset)

        assertEquals(country2Code, response.last().countrycode!!.iso2)
        assertEquals("Brasil", response.last().countryregion)
        assertEquals(0 ,response.last().timeseries!!.size)
        assertEquals(0, response.last().offset)
    }

    @Test
    fun testGetIsoByName_ok(){
        val country1 = Country(newId(), "Argentina", "2020-05-05", Location(25.0, 25.0), CountryCode("AR", "ISO31"), 1, 1, 1)
        val country2 = Country(newId(), "Brasil", "2020-05-06", Location(35.0, 2445.0), CountryCode("BR", "ISO33"), 1, 1, 1)

        val names = listOf("Argentina", "Brasil")
        coEvery { countriesRepository.getCountriesByName(names) } returns listOf(country1, country2)

        val service = CountriesService(countriesRepository)

        assertEquals(listOf("AR" , "BR"), runBlocking { service.getIsoByName(names) })
    }

    @Test
    fun testGetIsoByName_emptyNamesList(){
        val service = CountriesService(countriesRepository)
        val names: List<String> = listOf()
        assertEquals(names, runBlocking { service.getIsoByName(names) })
    }
}