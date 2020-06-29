package com.utn.tacs.e2e.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.client.MongoDatabase
import com.utn.tacs.*
import com.utn.tacs.countries.CountriesRepository
import com.utn.tacs.countries.CountriesService
import com.utn.tacs.exception.exceptionHandler
import com.utn.tacs.rest.countriesRoutes
import com.utn.tacs.user.UsersRepository
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.BeforeClass
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.litote.kmongo.KMongo
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Testcontainers
class CountriesControllerTest {

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            authentication(usersRepository)
            contentNegotiator()
            countriesRoutes(countriesService)
            exceptionHandler()
        }, callback)
    }

    @Test
    fun testGetCountries_ok() = testApp {
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
    fun testGetCountriesByNearestLocation_ok() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/countries?lat=-34&lon=-64")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<TimeserieResponse>>(response.content!!)
            val expectedCountries = listOf("AR", "BO", "BR", "CL", "PY", "PE", "UY")

            assertEquals(HttpStatusCode.OK, status)
            assertEquals(7, countries.size)
            countries.forEach { it ->
                assertTrue(expectedCountries.contains(it.iso2))
                assertFalse(it.countryRegion.isEmpty())
                assertNotNull(it.confirmed!!)
                assertNotNull(it.deaths!!)
                assertNotNull(it.recovered!!)
            }
        }
    }

    @Test
    fun testGetCountriesByNearestLocation_sendStringsAsLatAndLon_BadRequest() = testApp {
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
    fun testGetCountryByIsoCode2_ok() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/countries/US")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val country = jacksonObjectMapper().readValue<CountryResponse>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("US", country.countrycode?.iso2)
            assertEquals("US", country.countryregion)
        }
    }

    @Test
    fun testGetCountryByIsoCode2_notFound() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/countries/PEPE")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun testGetCountryByName_ok() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/countries?name=indonesia")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val country = jacksonObjectMapper().readValue<CountryResponse>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("ID", country.countrycode?.iso2)
            assertEquals("Indonesia", country.countryregion)
        }
    }

    @Test
    fun testGetCountryByName_notFound() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/countries?name=aaa")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun testGetCountriesNames_ok() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/countries/names")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countriesNames = jacksonObjectMapper().readValue<List<CountriesNamesResponse>>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(174, countriesNames.size)
        }
    }

    @Test
    fun testGetCountryTimeseries_ok() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=BR,UY,KE&")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<TimeserieResponse>>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(3, countries.size)

            assertEquals("BR", countries.get(0).iso2)
            assertEquals("Brazil", countries.get(0).countryRegion)
            assertEquals(0, countries.get(0).offset)
            assertEquals(0, countries.get(0).timeseriesDeath!!.size)
            assertEquals(0, countries.get(0).timeseriesInfected!!.size)
            assertEquals(0, countries.get(0).timeseriesReconvered!!.size)
            assertEquals(0, countries.get(0).timeserieDate!!.size)
            assertNotNull(countries.get(0).confirmed)
            assertNotNull(countries.get(0).deaths)
            assertNotNull(countries.get(0).recovered)
            assertNotNull(countries.get(0).newCases)
            assertNotNull(countries.get(0).newRecovered)
            assertNotNull(countries.get(0).newDeath)

            assertEquals("UY", countries.get(1).iso2)
            assertEquals("Uruguay", countries.get(1).countryRegion)
            assertEquals(0, countries.get(1).offset)
            assertEquals(0, countries.get(1).timeseriesDeath!!.size)
            assertEquals(0, countries.get(1).timeseriesInfected!!.size)
            assertEquals(0, countries.get(1).timeseriesReconvered!!.size)
            assertEquals(0, countries.get(1).timeserieDate!!.size)
            assertNotNull(countries.get(1).confirmed)
            assertNotNull(countries.get(1).deaths)
            assertNotNull(countries.get(1).recovered)
            assertNotNull(countries.get(1).newCases)
            assertNotNull(countries.get(1).newRecovered)
            assertNotNull(countries.get(1).newDeath)

            assertEquals("KE", countries.get(2).iso2)
            assertEquals("Kenya", countries.get(2).countryRegion)
            assertEquals(0, countries.get(2).offset)
            assertEquals(0, countries.get(2).timeseriesDeath!!.size)
            assertEquals(0, countries.get(2).timeseriesInfected!!.size)
            assertEquals(0, countries.get(2).timeseriesReconvered!!.size)
            assertEquals(0, countries.get(2).timeserieDate!!.size)
            assertNotNull(countries.get(2).confirmed)
            assertNotNull(countries.get(2).deaths)
            assertNotNull(countries.get(2).recovered)
            assertNotNull(countries.get(2).newCases)
            assertNotNull(countries.get(2).newRecovered)
            assertNotNull(countries.get(2).newDeath)
        }
    }

    @Test
    fun testGetCountryTimeseries_ok_byDays() = testApp {
        val fromDay = 4
        val toDay = 8
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=BR,UY,KE&fromDay=" + fromDay + "&toDay=" + toDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<TimeserieResponse>>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(3, countries.size)

            assertEquals("BR", countries.get(0).iso2)
            assertEquals("Brazil", countries.get(0).countryRegion)
            assertEquals(0, countries.get(0).offset)
            assertEquals(listOf("0","0","0","0","0"), countries.get(0).timeseriesDeath!!)
            assertEquals(listOf("2","2","2","2","4"), countries.get(0).timeseriesInfected!!)
            assertEquals(listOf("0","0","0","0","0"), countries.get(0).timeseriesReconvered!!)
            assertEquals(listOf("2/29/20","3/1/20","3/2/20","3/3/20","3/4/20"), countries.get(0).timeserieDate!!)
            assertEquals(4  , countries.get(0).confirmed)
            assertEquals(0, countries.get(0).deaths)
            assertEquals(0, countries.get(0).recovered)
            assertEquals(0, countries.get(0).newCases)
            assertEquals(0, countries.get(0).newRecovered)
            assertEquals(0, countries.get(0).newDeath)

            assertEquals("UY", countries.get(1).iso2)
            assertEquals("Uruguay", countries.get(1).countryRegion)
            assertEquals(16, countries.get(1).offset)
            assertEquals(listOf("0","0","0","0","0"), countries.get(1).timeseriesDeath!!)
            assertEquals(listOf("29","50","79","94","110"), countries.get(1).timeseriesInfected!!)
            assertEquals(listOf("0","0","0","0","0"), countries.get(1).timeseriesReconvered!!)
            assertEquals(listOf("3/16/20","3/17/20","3/18/20","3/19/20","3/20/20"), countries.get(1).timeserieDate!!)
            assertEquals(110, countries.get(1).confirmed)
            assertEquals(0, countries.get(1).deaths)
            assertEquals(0, countries.get(1).recovered)
            assertEquals(0, countries.get(1).newCases)
            assertEquals(0, countries.get(1).newRecovered)
            assertEquals(0, countries.get(1).newDeath)

            assertEquals("KE", countries.get(2).iso2)
            assertEquals("Kenya", countries.get(2).countryRegion)
            assertEquals(16, countries.get(2).offset)
            assertEquals(listOf("0","0","0","0","0"), countries.get(2).timeseriesDeath!!)
            assertEquals(listOf("3","3","3","7","7"), countries.get(2).timeseriesInfected!!)
            assertEquals(listOf("0","0","0","0","0"), countries.get(2).timeseriesReconvered!!)
            assertEquals(listOf("3/16/20","3/17/20","3/18/20","3/19/20","3/20/20"), countries.get(2).timeserieDate!!)
            assertEquals(7, countries.get(2).confirmed)
            assertEquals(0, countries.get(2).deaths)
            assertEquals(0, countries.get(2).recovered)
            assertEquals(0, countries.get(2).newCases)
            assertEquals(0, countries.get(2).newRecovered)
            assertEquals(0, countries.get(2).newDeath)
        }
    }

    @Test
    fun testGetCountryTimeseries_wrongIso2_NotFound() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=PEPE")) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun testGetCountryTimeseries_between2Days_ok() = testApp {
        val fromDay = 8
        val toDay = 10
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDay=" + fromDay + "&toDay=" + toDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<TimeserieResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals(1, countries.size)
            assertEquals("AR", country.iso2)
            assertEquals("Argentina", country.countryRegion)
            assertEquals(listOf("3/10/20", "3/11/20", "3/12/20"), country.timeserieDate)
            assertEquals(listOf("0","0","0"), country.timeseriesReconvered)
            assertEquals(listOf("17","19","19"), country.timeseriesInfected)
            assertEquals(listOf("1","1","1"), country.timeseriesDeath)
            assertEquals(0, country.offset)
        }
    }

    @Test
    fun testGetCountryTimeseries_toDay_ok() = testApp {
        val toDay = 4
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&toDay=" + toDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<TimeserieResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.iso2)
            assertEquals("Argentina", country.countryRegion)
            assertEquals(listOf("3/3/20", "3/4/20", "3/5/20", "3/6/20"), country.timeserieDate)
            assertEquals(listOf("0","0","0","0"), country.timeseriesReconvered)
            assertEquals(listOf("1","1","1","2"), country.timeseriesInfected)
            assertEquals(listOf("0","0","0","0"), country.timeseriesDeath)
            assertEquals(0, country.offset)
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDay_ok() = testApp {
        val fromDay = 113
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDay=" + fromDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<TimeserieResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.iso2)
            assertEquals("Argentina", country.countryRegion)
            assertNotEquals(0 , country.timeserieDate!!.size)
            assertNotEquals(0, country.timeseriesReconvered!!.size)
            assertNotEquals(0, country.timeseriesInfected!!.size)
            assertNotEquals(0, country.timeseriesDeath!!.size)
            assertEquals(0, country.offset)
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDayToDayWrongRanges_BadRequest() = testApp {
        val fromDay = 10
        val toDay = 3
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDay=" + fromDay + "&toDay=" + toDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDayWrongTypeToDay_BadRequest() = testApp {
        val fromDay = "pepe"
        val toDay = 3
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDay=" + fromDay + "&toDay=" + toDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDayToDayWrongType_BadRequest() = testApp {
        val fromDay = 3
        val toDay = "pepe"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDay=" + fromDay + "&toDay=" + toDay)) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun testGetCountryTimeseries_betweenDates_ok() = testApp {
        val fromDate = "5/25/20"
        val toDate = "5/28/20"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDate=" + fromDate + "&toDate=" + toDate)) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<TimeserieResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.iso2)
            assertEquals("Argentina", country.countryRegion)
            assertEquals(listOf("5/25/20", "5/26/20", "5/27/20", "5/28/20"), country.timeserieDate)
            assertEquals(listOf("3999","4167","4349","4617"), country.timeseriesReconvered)
            assertEquals(listOf("12628","13228","13933","14702"), country.timeseriesInfected)
            assertEquals(listOf("467","484","500","508"), country.timeseriesDeath)
            assertEquals(0, country.offset)
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDate_ok() = testApp {
        val fromDate = "4/17/20"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDate=" + fromDate)) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<TimeserieResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.iso2)
            assertEquals("Argentina", country.countryRegion)
            assertEquals("4/17/20" , country.timeserieDate!!.first())
            assertEquals("666", country.timeseriesReconvered!!.first())
            assertEquals("2669", country.timeseriesInfected!!.first())
            assertEquals("123", country.timeseriesDeath!!.first())
            assertEquals(0, country.offset)
        }
    }

    @Test
    fun testGetCountryTimeseries_toDate_ok() = testApp {
        val toDate = "3/25/20"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&toDate=${toDate}")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<TimeserieResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.iso2)
            assertEquals("Argentina", country.countryRegion)
            assertEquals("3/3/20" , country.timeserieDate!!.first())
            assertEquals("1", country.timeseriesInfected!!.first())
            assertEquals("0", country.timeseriesReconvered!!.first())
            assertEquals("0", country.timeseriesDeath!!.first())
            assertEquals("3/25/20" , country.timeserieDate!!.last())
            assertEquals("52", country.timeseriesReconvered!!.last())
            assertEquals("387", country.timeseriesInfected!!.last())
            assertEquals("8", country.timeseriesDeath!!.last())
            assertEquals(23, country.timeserieDate!!.size)
            assertEquals(0, country.offset)
        }
    }

    @Test
    fun testGetCountryTimeseries_fromDateToDateWrongType_BadRequest() = testApp {
        val fromDate = "1/25/20"
        val toDate = "pepe"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&fromDate=" + fromDate + "&toDate=" + toDate)) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun testGetCountryTimeseries_datesRangeWithoutData_ok() = testApp {
        val fromDate = "1/1/20"
        val toDate = "1/25/20"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&toDate=${toDate}&fromDate=${fromDate}")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<TimeserieResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.iso2!!)
            assertEquals("Argentina", country.countryRegion)
            assertEquals(0, country.timeserieDate!!.size)
        }
    }

    @Test
    fun testGetCountryTimeseries_datesRangeWithPartialData_ok() = testApp {
        val fromDate = "1/1/20"
        val toDate = "3/5/20"
        with(handleRequest(HttpMethod.Get, "/api/countries/timeseries?countries=AR&toDate=${toDate}&fromDate=${fromDate}")) {
            val status = response.status() ?: throw Exception("not response status code found")
            val countries = jacksonObjectMapper().readValue<List<TimeserieResponse>>(response.content!!)
            val country = countries.first()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, countries.size)
            assertEquals("AR", country.iso2)
            assertEquals("Argentina", country.countryRegion)
            assertEquals("3/3/20" , country.timeserieDate!!.first())
            assertEquals("1", country.timeseriesInfected!!.first())
            assertEquals("0", country.timeseriesReconvered!!.first())
            assertEquals("0", country.timeseriesDeath!!.first())
            assertEquals("3/5/20" , country.timeserieDate!!.last())
            assertEquals("0", country.timeseriesReconvered!!.last())
            assertEquals("1", country.timeseriesInfected!!.last())
            assertEquals("0", country.timeseriesDeath!!.last())
            assertEquals(3, country.timeserieDate!!.size)
            assertEquals(0, country.offset)
        }
    }

    companion object {
        private lateinit var countriesService: CountriesService
        private lateinit var usersRepository: UsersRepository


        private val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        private val userEmail = "testuser" + timestamp + "@gmail.com"

        @Container
        var mongoContainer = GenericContainer<Nothing>("mongo:3.6.18").apply { withExposedPorts(27017) }

        private lateinit var mongoDatabase: MongoDatabase

        @BeforeClass
        @JvmStatic
        fun before() {
            mongoContainer.start()
            mongoDatabase = KMongo.createClient("mongodb://${mongoContainer.containerIpAddress}:${mongoContainer.getMappedPort(27017)}").getDatabase("test")

            usersRepository = UsersRepository(mongoDatabase)
            countriesService = CountriesService(CountriesRepository(mongoDatabase, CovidExternalClient))
        }
    }
}


