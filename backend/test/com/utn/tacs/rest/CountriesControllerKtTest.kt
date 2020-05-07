package com.utn.tacs.rest

import com.google.gson.reflect.TypeToken
import com.utn.tacs.CountryData
import com.utn.tacs.HttpBinError
import com.utn.tacs.gson
import com.utn.tacs.module
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class CountriesControllerKtTest {

    @Test
    fun testApiCountriesLocation() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries?lat=-34&lon=-64")) {
            val expectedResponse = listOf(
                CountryResponse("Argentina"),
                CountryResponse("Bolivia"),
                CountryResponse("Brazil"),
                CountryResponse("Chile"),
                CountryResponse("Paraguay"),
                CountryResponse("Peru"),
                CountryResponse("Uruguay")
            ).toString()

            assertEquals(HttpStatusCode.OK, response.status())
            val data = gson.fromJson(response.content, Array<CountryResponse>::class.java).asList().toString()

            assertEquals(expectedResponse, data)
        }

        with(handleRequest(HttpMethod.Get, "/api/countries/?lat=-3333&lon=4000")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data = gson.fromJson(response.content, Array<String>::class.java).asList()
            assertEquals(0, data.size)
        }

        with(handleRequest(HttpMethod.Get, "/api/countries/?lat=asd&lon=dsa")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data = gson.fromJson(response.content, HttpBinError::class.java)
            assertEquals(HttpStatusCode.InternalServerError, data.code)
            assertEquals("java.lang.NumberFormatException: For input string: \"asd\"", data.message)
        }
    }

    @Test
    fun testApiCountriesRequests() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/US")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data = gson.fromJson(response.content, CountryData::class.java)

            assertEquals("US", data.countrycode.iso2)
            assertEquals("USA", data.countrycode.iso3)
            assertEquals("US", data.countryregion)
            assertEquals(37.0902, data.location.lat)
            assertEquals(-95.7129, data.location.lng)
        }

        with(handleRequest(HttpMethod.Get, "/api/countries/NONEXISTENT")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data = gson.fromJson(response.content, HttpBinError::class.java)
            assertEquals(HttpStatusCode.InternalServerError, data.code)
            assertEquals("java.lang.IllegalArgumentException: There was no country with iso2 code NONEXISTENT", data.message)
        }
    }

    @Test
    fun testApiCountriesTree(){
        //TODO
    }
}
