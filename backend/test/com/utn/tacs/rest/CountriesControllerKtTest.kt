package com.utn.tacs.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.*
import com.utn.tacs.TokenTestGenerator.addJwtHeader
import com.utn.tacs.countries.CountriesService
import com.utn.tacs.user.UsersRepository
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.litote.kmongo.id.jackson.IdJacksonModule
import org.litote.kmongo.id.toId
import java.lang.IllegalArgumentException

class CountriesControllerKtTest {

    /*
   * When using Controllers that need authentication, authentication must be the first installed, and
   * userRepository must be a mock that returns the test user.
   * */

    private lateinit var authUser: User
    val countriesService = mockk<CountriesService>()
    private val usersRepository = mockk<UsersRepository>()

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            authentication(usersRepository)
            contentNegotiator()
            countriesRoutes(countriesService)
        }, callback)
    }

    @Before
    fun before() {
        authUser = User(ObjectId().toId(), "test-user")
        every { usersRepository.getUserOrFail(any()) } returns authUser
    }

    @Test
    fun testApiCountriesLocation() = testApp {

        var lat = -34.0
        var lon = -64.0

        coEvery { countriesService.getNearestCountries(lat, lon) } returns listOf()

        with(handleRequest(HttpMethod.Get, "/api/countries?lat=${lat}&lon=${lon}") {
            addJwtHeader(authUser)
        }) {
            val data: List<Country> = jacksonObjectMapper().registerModule(IdJacksonModule()).readValue(response.content!!)
            assertEquals(0, data.size)
        }

        lat = -3333333.0
        lon = -6999999.0

        coEvery { countriesService.getNearestCountries(lat, lon) } returns listOf()
        with(handleRequest(HttpMethod.Get, "/api/countries?lat=${lat}&lon=${lon}") {
            addJwtHeader(authUser)
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data: List<Country> = jacksonObjectMapper().registerModule(IdJacksonModule()).readValue(response.content!!)
            assertEquals(0, data.size)
        }

        with(handleRequest(HttpMethod.Get, "/api/countries/?lat=asd&lon=dsa") {
            addJwtHeader(authUser)
        }) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun testApiCountriesRequests() = testApp {

        coEvery { countriesService.getCountryLatestByIsoCode("US") } returns CountryResponse( "US",
                "", Location(37.0902, -95.7129), CountryCode("US", "USA"), 0, 0, 0, listOf())

        with(handleRequest(HttpMethod.Get, "/api/countries/US") {
            addJwtHeader(authUser)
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data: Country = jacksonObjectMapper().registerModule(IdJacksonModule()).readValue(response.content!!)

            assertEquals("US", data.countrycode?.iso2)
            assertEquals("USA", data.countrycode?.iso3)
            assertEquals("US", data.countryregion)
            assertEquals(37.0902, data.location.lat)
            assertEquals(-95.7129, data.location.lng)
        }

    }

    @Test
    fun testApiCountriesTree() {
        //TODO
    }
}
