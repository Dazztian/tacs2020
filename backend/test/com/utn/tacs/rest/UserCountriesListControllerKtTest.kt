package com.utn.tacs.rest

import com.utn.tacs.module
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class UserCountriesListControllerKtTest {

    //TODO AGREGAR TESTS CON INFORMACION REAL
   /* @Test
    fun testApiCountriesList() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/user/countries/1")) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
        with(handleRequest(HttpMethod.Post, "/api/countries/list")) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }*/

    @Test
    fun testApiCountriesListWithId() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Delete, "/api/user/countries/list/1/list1")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Borra una lista del usuario", response.content)
        }
        with(handleRequest(HttpMethod.Delete, "/api/user/countries/list/2222/NO_REAL_LIST")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Borra una lista del usuario", response.content)
        }
        with(handleRequest(HttpMethod.Patch, "/api/user/countries/list/1/list1")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Modifica una lista del usuario", response.content)
        }
        with(handleRequest(HttpMethod.Patch, "/api/user/countries/list/2222/NO_REAL_LIST")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Modifica una lista del usuario", response.content)
        }
    }

    @Test
    fun testApiCountriesIdListTable() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/user/countries/list/table/id")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Envia los datos e/m/r para una lista de paises", response.content)
        }
        with(handleRequest(HttpMethod.Get, "/api/user/countries/list/table/id")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Envia los datos e/m/r para una lista de paises", response.content)
        }
    }

}