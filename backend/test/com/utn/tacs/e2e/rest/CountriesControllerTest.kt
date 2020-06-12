package com.utn.tacs.e2e.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.CountriesNamesResponse
import com.utn.tacs.CountryResponse
import com.utn.tacs.module
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CountriesControllerTest {
    @Test
    fun testGetCountries_ok() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)

            assertEquals(HttpStatusCode.OK, status)
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
            val expectedCountries = listOf("AR", "BO", "BR", "CL", "PY", "PE", "UY")

            assertEquals(HttpStatusCode.OK, status)
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
            assertEquals(HttpStatusCode.BadRequest, status)
        }

        with(handleRequest(HttpMethod.Get, "/api/countries?lat=pepe&lon=-64")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun testGetCountryByIsoCode2_ok() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/US")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val country = jacksonObjectMapper().readValue<CountryResponse>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("US", country.countrycode?.iso2)
            assertEquals("US", country.countryregion)
        }
    }

    @Test
    fun testGetCountryByIsoCode2_notFound() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/PEPE")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun testGetCountryByName_ok() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries?name=indonesia")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val country = jacksonObjectMapper().readValue<CountryResponse>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("ID", country.countrycode?.iso2)
            assertEquals("Indonesia", country.countryregion)
        }
    }

    @Test
    fun testGetCountryByName_notFound() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries?name=aaa")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun testGetCountriesNames_ok() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/names")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countriesNames = jacksonObjectMapper().readValue<List<CountriesNamesResponse>>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(174, countriesNames.size)
        }
    }

    @Test
    fun testGetCountryTimeseries_ok() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=KE&")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("KE", country.countrycode?.iso2)
            assertEquals("Kenya", country.countryregion)
            assertNotNull(country.timeseries)
            assertTrue(country.timeseries!!.isNotEmpty())

            var previousTimeSerie = country.timeseries!!.get(0)
            country.timeseries!!.forEach {
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
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=PEPE")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun testGetCountryTimeseries_between2Days_ok() = withTestApplication(Application::module) {
        val fromDay = 8
        val toDay = 10
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDay=" + fromDay + "&toDay=" + toDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals(1, countries.size)
            assertEquals("AR", country.countrycode?.iso2)
            assertEquals("Argentina", country.countryregion)
            assertEquals(3, country.timeseries!!.size)

            var first = true
            var previousTimeSerie = country.timeseries!!.get(0)
            country.timeseries!!.forEach {
                assertNotNull(it.number)
                assertNotNull(it.confirmed)
                assertNotNull(it.deaths)
                assertNotNull(it.recovered)
                assertNotNull(it.date)
                if (first) {
                    first = false
                } else {
                    assertTrue(previousTimeSerie.number < it.number)
                }
                previousTimeSerie = it
            }

            assertEquals(8, country.timeseries!!.get(0).number)
            assertEquals(9, country.timeseries!!.get(1).number)
            assertEquals(10, country.timeseries!!.get(2).number)
        }
    }

    @Test
    fun testGetCountryTimeseries_toDay_ok() = withTestApplication(Application::module) {
        val toDay = 10
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&toDay=" + toDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.countrycode?.iso2)
            assertEquals("Argentina", country.countryregion)
            assertEquals(toDay, country.timeseries!!.size)

            var first = true
            var previousTimeSerie = country.timeseries!!.get(0)
            assertEquals(1, previousTimeSerie.number)
            assertEquals(toDay, country.timeseries!!.get(9).number)
            country.timeseries!!.forEach {
                assertNotNull(it.number)
                assertNotNull(it.confirmed)
                assertNotNull(it.deaths)
                assertNotNull(it.recovered)
                assertNotNull(it.date)
                if (first) {
                    first = false
                } else {
                    assertTrue(previousTimeSerie.number < it.number)
                }
                previousTimeSerie = it
            }
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDay_ok() = withTestApplication(Application::module) {
        val fromDay = 10
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDay=" + fromDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.countrycode?.iso2)
            assertEquals("Argentina", country.countryregion)
            assertTrue(country.timeseries!!.isNotEmpty())

            var first = true
            var previousTimeSerie = country.timeseries!!.get(0)
            assertEquals(fromDay, previousTimeSerie.number)
            country.timeseries!!.forEach {
                assertNotNull(it.number)
                assertNotNull(it.confirmed)
                assertNotNull(it.deaths)
                assertNotNull(it.recovered)
                assertNotNull(it.date)
                if (first) {
                    first = false
                } else {
                    assertTrue(previousTimeSerie.number < it.number)
                }
                previousTimeSerie = it
            }
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDayToDayWrongRanges_BadRequest() = withTestApplication(Application::module) {
        val fromDay = 10
        val toDay = 3
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDay=" + fromDay + "&toDay=" + toDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDayWrongTypeToDay_BadRequest() = withTestApplication(Application::module) {
        val fromDay = "pepe"
        val toDay = 3
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDay=" + fromDay + "&toDay=" + toDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDayToDayWrongType_BadRequest() = withTestApplication(Application::module) {
        val fromDay = 3
        val toDay = "pepe"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDay=" + fromDay + "&toDay=" + toDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun testGetCountryTimeseries_betweenDates_ok() = withTestApplication(Application::module) {
        val fromDate = "5/25/20"
        val toDate = "5/28/20"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDate=" + fromDate + "&toDate=" + toDate)) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.countrycode?.iso2)
            assertEquals("Argentina", country.countryregion)
            assertEquals(4, country.timeseries!!.size)

            var first = true
            var previousTimeSerie = country.timeseries!!.get(0)
            country.timeseries!!.forEach {
                assertNotNull(it.number)
                assertNotNull(it.confirmed)
                assertNotNull(it.deaths)
                assertNotNull(it.recovered)
                assertNotNull(it.date)
                if (first) {
                    first = false
                } else {
                    assertTrue(previousTimeSerie.number < it.number)
                }
                previousTimeSerie = it
            }

            assertEquals("5/25/20", country.timeseries!!.get(0).date)
            assertEquals("5/26/20", country.timeseries!!.get(1).date)
            assertEquals("5/27/20", country.timeseries!!.get(2).date)
            assertEquals("5/28/20", country.timeseries!!.get(3).date)
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDate_ok() = withTestApplication(Application::module) {
        val fromDate = "4/17/20"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDate=" + fromDate)) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.countrycode?.iso2)
            assertEquals("Argentina", country.countryregion)
            assertTrue(country.timeseries!!.isNotEmpty())

            var first = true
            var previousTimeSerie = country.timeseries!!.get(0)
            assertEquals(fromDate, previousTimeSerie.date)
            country.timeseries!!.forEach {
                assertNotNull(it.number)
                assertNotNull(it.confirmed)
                assertNotNull(it.deaths)
                assertNotNull(it.recovered)
                assertNotNull(it.date)
                if (first) {
                    first = false
                } else {
                    assertTrue(previousTimeSerie.number < it.number)
                }
                previousTimeSerie = it
            }
        }
    }

    @Test
    fun testGetCountryTimeseries_toDate_ok() = withTestApplication(Application::module) {
        val toDate = "5/25/20"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&toDate=${toDate}")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.countrycode?.iso2)
            assertEquals("Argentina", country.countryregion)
            assertTrue(country.timeseries!!.isNotEmpty())

            var first = true
            var previousTimeSerie = country.timeseries!!.get(0)
            assertEquals(toDate, country.timeseries!!.get(country.timeseries!!.size - 1).date)
            country.timeseries!!.forEach {
                assertNotNull(it.number)
                assertNotNull(it.confirmed)
                assertNotNull(it.deaths)
                assertNotNull(it.recovered)
                assertNotNull(it.date)
                if (first) {
                    first = false
                } else {
                    assertTrue(previousTimeSerie.number < it.number)
                }
                previousTimeSerie = it
            }
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDateToDateWrongType_BadRequest() = withTestApplication(Application::module) {
        val fromDate = "1/25/20"
        val toDate = "pepe"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDate=" + fromDate + "&toDate=" + toDate)) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun testGetCountryTimeseries_datesRangeWithoutData_ok() = withTestApplication(Application::module) {
        val fromDate = "1/1/20"
        val toDate = "1/25/20"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&toDate=${toDate}&fromDate=${fromDate}")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.countrycode?.iso2)
            assertEquals("Argentina", country.countryregion)
            assertTrue(country.timeseries!!.isEmpty())
        }
    }

    @Test
    fun testGetCountryTimeseries_datesRangeWithPartialData_ok() = withTestApplication(Application::module) {
        val fromDate = "1/1/20"
        val toDate = "3/5/20"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&toDate=${toDate}&fromDate=${fromDate}")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<CountryResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.countrycode?.iso2)
            assertEquals("Argentina", country.countryregion)
            assertEquals(3, country.timeseries!!.size)

            var first = true
            var previousTimeSerie = country.timeseries!!.get(0)
            country.timeseries!!.forEach {
                assertNotNull(it.number)
                assertNotNull(it.confirmed)
                assertNotNull(it.deaths)
                assertNotNull(it.recovered)
                assertNotNull(it.date)
                if (first) {
                    first = false
                } else {
                    assertTrue(previousTimeSerie.number < it.number)
                }
                previousTimeSerie = it
            }

            assertEquals("3/3/20", country.timeseries!!.get(0).date)
            assertEquals("3/4/20", country.timeseries!!.get(1).date)
            assertEquals(toDate, country.timeseries!!.get(2).date)

        }
    }
}


