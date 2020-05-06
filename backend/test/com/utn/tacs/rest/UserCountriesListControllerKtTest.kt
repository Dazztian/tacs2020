package com.utn.tacs.rest

import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.module
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.mockk
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class UserCountriesListControllerKtTest {


    private val userListRepository = mockk<UserListsRepository>()

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
    fun testApiCountriesListWithId() = withTestApplication({userCountriesListRoutes(userListRepository) })  {
        with(handleRequest(HttpMethod.Delete, "/api/user/1/countries/list/1")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Borra una lista del usuario", response.content)
        }
        with(handleRequest(HttpMethod.Delete, "/api/user/1/countries/list/NO_REAL_LIST")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Borra una lista del usuario", response.content)
        }
        with(handleRequest(HttpMethod.Patch, "/api/user/1/countries/list/1")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Modifica una lista del usuario", response.content)
        }
        with(handleRequest(HttpMethod.Patch,  "/api/user/1/countries/list/NO_REAL_LIST")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Modifica una lista del usuario", response.content)
        }
    }

    @Test
    fun testApiCountriesIdListTable() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/api/user/1/countries/list/table/10")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Envia los datos e/m/r para una lista de paises", response.content)
        }
        with(handleRequest(HttpMethod.Get, "/api/user/1/countries/list/table/10")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Envia los datos e/m/r para una lista de paises", response.content)
        }
    }

}