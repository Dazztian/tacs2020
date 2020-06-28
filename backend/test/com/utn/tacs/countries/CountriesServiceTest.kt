package com.utn.tacs.countries

import com.utn.tacs.Country
import com.utn.tacs.CountryCode
import com.utn.tacs.CountryResponse
import com.utn.tacs.Location
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
}