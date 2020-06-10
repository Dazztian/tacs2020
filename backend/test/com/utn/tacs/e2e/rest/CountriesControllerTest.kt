package com.utn.tacs.e2e.rest

import com.utn.tacs.module
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.concurrent.TimeUnit
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.Country
import com.utn.tacs.CountryResponse
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CountriesControllerTest {
    @Test
    fun testGetCountries_ok() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)

            assertEquals(status, HttpStatusCode.OK)
            countries.forEach { it ->
                assertFalse(it.countryregion.isEmpty())
                assertFalse(it.lastupdate.isEmpty())
                assertNotNull(it.confirmed)
                assertNotNull(it.deaths)
                assertNotNull(it.recovered)
                assertTrue(it.timeseries?.isEmpty() ?: false)
            }
        }
    }

    @Test
    fun testGetCountriesByNearestLocation_ok() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries?lat=-34&lon=-64")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)
            val expectedCountries = listOf("AR" , "BO" , "BR" , "CL" , "PY" , "PE" , "UY")

            assertEquals(status, HttpStatusCode.OK)
            assertEquals(7, countries.size)
            countries.forEach { it ->
                assertTrue(expectedCountries.contains(it.countrycode?.iso2))
                assertFalse(it.countryregion.isEmpty())
                assertFalse(it.lastupdate.isEmpty())
                assertNotNull(it.confirmed)
                assertNotNull(it.deaths)
                assertNotNull(it.recovered)
                assertTrue(it.timeseries?.isEmpty() ?: false)
            }
        }
    }

    @Test
    fun testGetCountriesByNearestLocation_sendStringsAsLatAndLon_BadRequest() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries?lat=-34&lon=pepe")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.BadRequest)
        }

        with(handleRequest(HttpMethod.Get, "/api/countries?lat=pepe&lon=-64")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun testGetCountryByIsoCode2_ok() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/US")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val country = jacksonObjectMapper().readValue<CountryResponse>(response.content!!)
            assertEquals(status, HttpStatusCode.OK)
            assertEquals("US" , country.countrycode?.iso2)
            assertEquals("US" , country.countryregion)
        }
    }

    @Test
    fun testGetCountryByIsoCode2_notFound() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/PEPE")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.NotFound)
        }
    }

    @Test
    fun testGetCountryByName_ok() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries?name=indonesia")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val country = jacksonObjectMapper().readValue<CountryResponse>(response.content!!)
            assertEquals(status, HttpStatusCode.OK)
            assertEquals("ID" , country.countrycode?.iso2)
            assertEquals("Indonesia" , country.countryregion)
        }
    }

    @Test
    fun testGetCountryByName_notFound() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries?name=aaa")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.NotFound)
        }
    }

    @Test
    fun testGetCountriesNames_ok() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/names")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countriesNames = jacksonObjectMapper().readValue<List<String>>(response.content!!)
            assertEquals(status, HttpStatusCode.OK)
            assertEquals(187, countriesNames.size)
        }
    }

    @Test
    fun testGetCountryTimeseries_ok() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/KE/timeseries")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val country = jacksonObjectMapper().readValue<CountryResponse>(response.content!!)
            assertEquals(status, HttpStatusCode.OK)
            assertEquals("KE" , country.countrycode?.iso2)
            assertEquals("Kenya" , country.countryregion)
            assertNotNull(country.timeseries)
            assertTrue(country.timeseries!!.isNotEmpty())

            var previousTimeSerie = country.timeseries!!.get(0)
            country.timeseries!!.forEach { it ->
                assertNotNull(it.number)
                assertNotNull(it.confirmed)
                assertNotNull(it.deaths)
                assertNotNull(it.recovered)
                assertNotNull(it.date)
                if (it.number != 1) {
                    assertTrue(it.number > previousTimeSerie.number)
                } else {
                    assertEquals(1, it.number)
                }
                previousTimeSerie = it
            }
        }
    }

    @Test
    fun testGetCountryTimeseries_wrongIso2_NotFound() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/PEPE/timeseries")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.NotFound)
        }
    }

    @Test
    fun testGetCountryTimeseries_Between2Days_ok() = withTestApplication(Application::module) {
        val fromDay = 8
        val toDay = 10
        with(handleRequest(HttpMethod.Get, "/api/countries/AR/timeseries")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val country = jacksonObjectMapper().readValue<CountryResponse>(response.content!!)
            assertEquals(status, HttpStatusCode.OK)
            assertEquals("AR" , country.countrycode?.iso2)
            assertEquals("Argentina" , country.countryregion)
            assertEquals(3, country.timeseries!!.size)

            var previousTimeSerie = country.timeseries!!.get(0)
            country.timeseries!!.forEach { it ->
                assertNotNull(it.number)
                assertNotNull(it.confirmed)
                assertNotNull(it.deaths)
                assertNotNull(it.recovered)
                assertNotNull(it.date)
                if (it.number != 1) {
                    assertTrue(it.number > previousTimeSerie.number)
                } else {
                    assertEquals(1, it.number)
                }
                previousTimeSerie = it
            }

            assertEquals(8,country.timeseries!!.get(0).number)
            assertEquals(9,country.timeseries!!.get(1).number)
            assertEquals(10,country.timeseries!!.get(2).number)
        }
    }
}


