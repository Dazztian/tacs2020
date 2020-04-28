import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.utn.tacs.CountryData
import com.utn.tacs.HttpBinError
import com.utn.tacs.module
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import junit.framework.Assert.assertEquals
import org.junit.Test


class ApplicationTest {

    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    @Test
    fun testRequests() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Hello World!", response.content)
        }
    }

    @Test
    fun testApiCountriesLocation() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries?lat=-34&lon=-64")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data = gson.fromJson(response.content, Array<String>::class.java).asList().toString()
            assertEquals("[Argentina, Bolivia, Brazil, Chile, Paraguay, Peru, Uruguay]", data)
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
    fun testApiCountriesList() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/list")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Retorna las listas del usuario", response.content)
        }
        with(handleRequest(HttpMethod.Post, "/api/countries/list")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Guarda una nueva listas del usuario", response.content)
        }
    }

    @Test
    fun testApiCountriesListWithId() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Delete, "/api/countries/list/22")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Borra una lista del usuario", response.content)
        }
        with(handleRequest(HttpMethod.Delete, "/api/countries/list/NON_EXISTENT_ID")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Borra una lista del usuario", response.content)
        }
        with(handleRequest(HttpMethod.Patch, "/api/countries/list/22")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Modifica una lista del usuario", response.content)
        }
        with(handleRequest(HttpMethod.Patch, "/api/countries/list/NON_EXISTENT_ID")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Modifica una lista del usuario", response.content)
        }
    }

    @Test
    fun testApiCountriesIdListTable() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/countries/list/22/table")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Envia los datos e/m/r para una lista de paises", response.content)
        }
        with(handleRequest(HttpMethod.Get, "/api/countries/list/NON_EXISTENT_ID/table")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Envia los datos e/m/r para una lista de paises", response.content)
        }
    }

    @Test
    fun testRegister() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Post, "/register")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("register", response.content)
        }
    }

    @Test
    fun testLogin() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Post, "/login")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("login", response.content)
        }
    }

    @Test
    fun testAuthGoogle() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Post, "/auth/google")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Oauth", response.content)
        }
    }

    @Test
    fun testLogout() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/logout")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("logout", response.content)
        }
    }

}

