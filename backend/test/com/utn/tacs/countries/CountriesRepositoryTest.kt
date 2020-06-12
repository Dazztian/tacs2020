package com.utn.tacs.countries

import com.mongodb.client.MongoDatabase
import com.utn.tacs.Country
import com.utn.tacs.CountryCode
import com.utn.tacs.CovidExternalClient
import com.utn.tacs.Location
import io.ktor.features.NotFoundException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.litote.kmongo.newId
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class CountriesRepositoryTest {

    private val mockExternalClient = mockk<CovidExternalClient>()

    @Test
    fun testGetCountriesFromDb() {
        //We are not testing that the cache works, so it will mocked and return empty.
        //First time calling the getCountries will call the external api, because of the timing of the cache.
        coEvery { mockExternalClient.getCountriesLatestFromApi() } returns listOf(country1, country2)
        val repository = CountriesRepository(mongoDatabase, mockExternalClient)
        assertEquals(listOf(country1, country2), runBlocking { repository.getCountries() })
        mongoDatabase.getCollection<Country>("countries").drop()
    }

    @Test
    fun testGetCountryByIso() {
        mongoDatabase.getCollection<Country>("countries").insertMany(listOf(country1))
        val repository = CountriesRepository(mongoDatabase, mockExternalClient)
        assertEquals(country1, runBlocking { repository.getCountry("ISO21") })

        coEvery { mockExternalClient.getCountryLatestByIsoCodeFromApi("ISO22") } returns country2
        assertEquals(country2, runBlocking { repository.getCountry("ISO22") })
        mongoDatabase.getCollection<Country>("countries").drop()

    }

    @Test
    fun testGetCountryByName() {
        mongoDatabase.getCollection<Country>("countries").insertMany(listOf(country1))
        val repository = CountriesRepository(mongoDatabase, mockExternalClient)
        assertEquals(country1, runBlocking { repository.getCountryByName("region1") })

        coEvery { mockExternalClient.getCountryLatestByIsoCodeFromApi("ISO22") } throws IllegalArgumentException()

        assertThrows<IllegalArgumentException> { runBlocking { repository.getCountry("ISO22") } }
        mongoDatabase.getCollection<Country>("countries").drop()
    }

    @Test
    fun testGetCountriesByName() {
        mongoDatabase.getCollection<Country>("countries").insertMany(listOf(country1, country2))
        val repository = CountriesRepository(mongoDatabase, mockExternalClient)
        assertEquals(listOf(country1, country2), runBlocking { repository.getCountriesByName(listOf("region1", "region2")) })

        assertThrows<NotFoundException> { runBlocking { repository.getCountriesByName(listOf("nonExistent", "nonExistent")) } }
        mongoDatabase.getCollection<Country>("countries").drop()
    }

    companion object {

        private lateinit var country3: Country
        private lateinit var country2: Country
        private lateinit var country1: Country

        @Container
        var mongoContainer = GenericContainer<Nothing>("mongo:3.6.18").apply { withExposedPorts(27017) }


        private lateinit var mongoDatabase: MongoDatabase

        @BeforeClass
        @JvmStatic
        fun before() {

            mongoContainer.start()

            country1 = Country(newId(), "region1", "2020-05-05", Location(25.0, 25.0), CountryCode("ISO21", "ISO31"), 1000, 960, 40)
            country2 = Country(newId(), "region2", "2020-05-06", Location(35.0, 2445.0), CountryCode("ISO22", "ISO43"), 424000, 9610, 340)
            country3 = Country(newId(), "region3", "2020-05-07", Location(35.0, 2445.0), CountryCode("ISO25", "ISO323"), 424000, 9610, 340)

            mongoDatabase = KMongo.createClient("mongodb://${mongoContainer.containerIpAddress}:${mongoContainer.getMappedPort(27017)}").getDatabase("test")

        }

        @AfterClass
        @JvmStatic
        fun after() {

        }
    }
}