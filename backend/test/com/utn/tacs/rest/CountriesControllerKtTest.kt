package com.utn.tacs.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.Country
import com.utn.tacs.HttpBinError
import com.utn.tacs.module
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.litote.kmongo.id.jackson.IdJacksonModule

class CountriesControllerKtTest {

    @Test
    fun testApiCountriesLocation() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries?lat=-34&lon=-64")) {
            val data: List<Country> = jacksonObjectMapper().registerModule(IdJacksonModule()).readValue(response.content!!)
            assertEquals(7, data.size)
        }

        with(handleRequest(HttpMethod.Get, "/api/countries/?lat=-3333&lon=4000")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data: List<Country> = jacksonObjectMapper().registerModule(IdJacksonModule()).readValue(response.content!!)
            assertEquals(0, data.size)
        }

        with(handleRequest(HttpMethod.Get, "/api/countries/?lat=asd&lon=dsa")) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun testApiCountriesRequests() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/US")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data: Country = jacksonObjectMapper().registerModule(IdJacksonModule()).readValue(response.content!!)

            assertEquals("US", data.countrycode?.iso2)
            assertEquals("USA", data.countrycode?.iso3)
            assertEquals("US", data.countryregion)
            assertEquals(37.0902, data.location.lat)
            assertEquals(-95.7129, data.location.lng)
        }

        with(handleRequest(HttpMethod.Get, "/api/countries/NONEXISTENT")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data: HttpBinError = jacksonObjectMapper().readValue(response.content!!)
            assertEquals(HttpStatusCode.InternalServerError, data.code)
            assertEquals("java.lang.IllegalArgumentException: There was no country with iso2 code NONEXISTENT", data.message)
        }
    }

    @Test
    fun testApiCountriesTree() {
        //TODO
    }
}
