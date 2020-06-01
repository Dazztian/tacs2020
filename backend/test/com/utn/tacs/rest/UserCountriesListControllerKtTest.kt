package com.utn.tacs.rest

import com.utn.tacs.UserCountriesList
import com.utn.tacs.UserCountriesListModificationRequest
import com.utn.tacs.UserCountriesListResponse
import com.utn.tacs.contentNegotiator
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.user.UsersService
import io.ktor.features.NotFoundException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.litote.kmongo.toId

class UserCountriesListControllerKtTest {

    private lateinit var ucl1: UserCountriesListResponse
    private lateinit var ucl2: UserCountriesListResponse
    private lateinit var ucl3: UserCountriesListResponse
    private lateinit var ucl4: UserCountriesListResponse
    private lateinit var ucl5: UserCountriesListResponse

    private val usersService = mockk<UsersService>()

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            contentNegotiator()
            userCountriesListRoutes(usersService)
        }, callback)
    }

    @Before
    fun before() {
        ucl1 = UserCountriesListResponse("id1", "TEST_1", mutableSetOf("TEST_COUNTRY"))
        ucl2 = UserCountriesListResponse("id2", "TEST_2", mutableSetOf("COUNTRY_1", "country_2", "CoUnTrY_3"))
        ucl3 = UserCountriesListResponse("id3", "TEST_3", mutableSetOf("COUNTRY_1"))
        ucl4 = UserCountriesListResponse("id4", "TEST_4", mutableSetOf("country_2"))
        ucl5 = UserCountriesListResponse("id5", "TEST_5", mutableSetOf("CoUnTrY_3"))
    }

    @Test
    fun testGetLists() = testApp {
        every { usersService.getUserLists("userId1") } returns listOf(ucl1)

        with(handleRequest(HttpMethod.Get, "/api/user/userId1/lists/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("[ {\n" +
                    "  \"id\" : \"${ucl1.id}\",\n" +
                    "  \"name\" : \"${ucl1.name}\",\n" +
                    "  \"countries\" : [ ${ucl1.countries.joinToString { "\"${it}\"" }} ],\n" +
                    "} ]", response.content)
        }

        every { usersService.getUserLists("nonExistentId") } returns listOf()

        with(handleRequest(HttpMethod.Get, "/api/user/nonExistentId/lists/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("[ ]", response.content)
        }

        every { usersService.getUserLists("userId3") } returns listOf(ucl3, ucl4, ucl5)

        with(handleRequest(HttpMethod.Get, "/api/user/userId3/lists/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("[ {\n" +
                    "  \"id\" : \"${ucl3.id}\",\n" +
                    "  \"name\" : \"${ucl3.name}\",\n" +
                    "  \"countries\" : [ ${ucl3.countries.joinToString { "\"${it}\"" }} ],\n" +
                    "}," +
                    " {\n" +
                    "  \"id\" : \"${ucl4.id}\",\n" +
                    "  \"name\" : \"${ucl4.name}\",\n" +
                    "  \"countries\" : [ ${ucl4.countries.joinToString { "\"${it}\"" }} ],\n" +
                    "}, {\n" +
                    "  \"_id\" : \"${ucl5.id}\",\n" +
                    "  \"name\" : \"${ucl5.name}\",\n" +
                    "  \"countries\" : [ ${ucl5.countries.joinToString { "\"${it}\"" }} ],\n" +
                    "} ]", response.content)
        }
    }

    @Test
    fun testPostLists() = testApp {
        val userId = "userId2"
        every { usersService.createUserList(userId, ucl2.name, ucl2.countries) } returns ucl2

        with(handleRequest(HttpMethod.Post, "/api/user/" + userId + "/lists") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"${ucl2.name}\",\"countries\":[ ${ucl2.countries.joinToString { "\"${it}\"" }} ]}")
        }) {
            assertEquals(HttpStatusCode.Created, response.status())
            assertEquals(ucl2.toString(), response.content)
        }

        every { usersService.createUserList(userId, ucl2.name, ucl2.countries) } throws NotFoundException()

        with(handleRequest(HttpMethod.Post, "/api/user/" + userId + "/lists") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"${ucl2.name}\",\"countries\":[ ${ucl2.countries.joinToString { "\"${it}\"" }} ]}")
        }) {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }

        with(handleRequest(HttpMethod.Post, "/api/user/" + userId + "/lists") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"${ucl2.name}\",\"countries\": not_a_list }")
        }) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }

    }

    @Test
    fun testGetListWithNameAndUserId() = testApp {
        val userId = "userId3"
        every { usersService.getUserList(userId, ucl3.name) } returns ucl3

        with(handleRequest(HttpMethod.Get, "/api/user/" +  userId + "/lists/TEST_3")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("{\n" +
                    "  \"id\" : \"${ucl3.id}\",\n" +
                    "  \"name\" : \"${ucl3.name}\",\n" +
                    "  \"countries\" : [ ${ucl3.countries.joinToString { "\"${it}\"" }} ],\n" +
                    "}", response.content)
        }
        every { usersService.getUserList(userId, "TEST_NO_EXISTS") } throws NotFoundException()

        with(handleRequest(HttpMethod.Get, "/api/user/" + userId + "/lists/TEST_NO_EXISTS")) {
            assertEquals(HttpStatusCode.NoContent, response.status())
        }
    }

    @Test
    fun testDeleteLists() = testApp {
        val userId = "userId"

        with(handleRequest(HttpMethod.Delete, "/api/user/" + userId + "/lists/TEST_3")) {
            assertEquals(HttpStatusCode.Accepted, response.status())
        }

        every { usersService.deleteUserList(userId, ucl3.name) } throws NotFoundException()
        with(handleRequest(HttpMethod.Delete, "/api/user/userId3/lists/TEST_3")) {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }

        every { usersService.deleteUserList(userId, ucl3.name) } throws  Exception()
        with(handleRequest(HttpMethod.Delete, "/api/user/userId3/lists/TEST_3")) {
            assertEquals(HttpStatusCode.InternalServerError, response.status())
        }
    }

    @Test
    fun testPatchLists() = testApp {
        val userId = "userId"
        val userListId = "userListId"
        val request = UserCountriesListModificationRequest("name" , mutableSetOf("new_country"))

        every { usersService.updateUserList(userId, userListId, request) } returns ucl4
        with(handleRequest(HttpMethod.Patch, "/api/user/" + userId + "/lists/TEST_4") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"new_name\",\"countries\":[ \"new_country\" ]}")
        }) {
            assertEquals(HttpStatusCode.Accepted, response.status())
            assertEquals(ucl4.toString(), response.content)
        }

        every { usersService.updateUserList(userId, userListId, request) } throws NotFoundException()
        with(handleRequest(HttpMethod.Patch, "/api/user/" + userId + "/countries/list/TEST_4") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"new_name\",\"countries\":[ \"new_country\" ]}")
        }) {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }

        with(handleRequest(HttpMethod.Patch, "/api/user/" + userId + "/countries/list/TEST_4") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"new_name\", bad_json ]}")
        }) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }


    }

    @Test
    fun testApiCountriesIdListTable() = withTestApplication({ userCountriesListRoutes(usersService) }) {
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