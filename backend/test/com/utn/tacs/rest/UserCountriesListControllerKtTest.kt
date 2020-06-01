package com.utn.tacs.rest

import com.utn.tacs.UserCountriesList
import com.utn.tacs.contentNegotiator
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.user.UsersService
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

    private lateinit var ucl1: UserCountriesList
    private lateinit var ucl2: UserCountriesList
    private lateinit var ucl3: UserCountriesList
    private lateinit var ucl4: UserCountriesList
    private lateinit var ucl5: UserCountriesList

    private val usersService = mockk<UsersService>()

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            contentNegotiator()
            userCountriesListRoutes(usersService)
        }, callback)
    }

    @Before
    fun before() {
        ucl1 = UserCountriesList("userId1".toId(), "TEST", mutableSetOf("TEST_COUNTRY"))
        ucl2 = UserCountriesList("userId2".toId(), "TEST_2", mutableSetOf("COUNTRY_1", "country_2", "CoUnTrY_3"))
        ucl3 = UserCountriesList("userId3".toId(), "TEST_3", mutableSetOf("COUNTRY_1"))
        ucl4 = UserCountriesList("userId3".toId(), "TEST_4", mutableSetOf("country_2"))
        ucl5 = UserCountriesList("userId3".toId(), "TEST_5", mutableSetOf("CoUnTrY_3"))
    }

    @Test
    fun testGetLists() = testApp {
        every { usersService.getUserLists("userId1") } returns listOf(ucl1)

        with(handleRequest(HttpMethod.Get, "/api/user/userId1/countries/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("[ {\n" +
                    "  \"_id\" : \"${ucl1._id}\",\n" +
                    "  \"userId\" : \"${ucl1.userId}\",\n" +
                    "  \"name\" : \"${ucl1.name}\",\n" +
                    "  \"countries\" : [ ${ucl1.countries.joinToString { "\"${it}\"" }} ],\n" +
                    "  \"creationDate\" : \"${ucl1.creationDate}\"\n" +
                    "} ]", response.content)
        }

        every { usersService.getUserLists("nonExistentId") } returns listOf()

        with(handleRequest(HttpMethod.Get, "/api/user/nonExistentId/countries/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("[ ]", response.content)
        }

        every { usersService.getUserLists("userId3") } returns listOf(ucl3, ucl4, ucl5)

        with(handleRequest(HttpMethod.Get, "/api/user/userId3/countries/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("[ {\n" +
                    "  \"_id\" : \"${ucl3._id}\",\n" +
                    "  \"userId\" : \"${ucl3.userId}\",\n" +
                    "  \"name\" : \"${ucl3.name}\",\n" +
                    "  \"countries\" : [ ${ucl3.countries.joinToString { "\"${it}\"" }} ],\n" +
                    "  \"creationDate\" : \"${ucl3.creationDate}\"\n" +
                    "}," +
                    " {\n" +
                    "  \"_id\" : \"${ucl4._id}\",\n" +
                    "  \"userId\" : \"${ucl4.userId}\",\n" +
                    "  \"name\" : \"${ucl4.name}\",\n" +
                    "  \"countries\" : [ ${ucl4.countries.joinToString { "\"${it}\"" }} ],\n" +
                    "  \"creationDate\" : \"${ucl4.creationDate}\"\n" +
                    "}, {\n" +
                    "  \"_id\" : \"${ucl5._id}\",\n" +
                    "  \"userId\" : \"${ucl5.userId}\",\n" +
                    "  \"name\" : \"${ucl5.name}\",\n" +
                    "  \"countries\" : [ ${ucl5.countries.joinToString { "\"${it}\"" }} ],\n" +
                    "  \"creationDate\" : \"${ucl5.creationDate}\"\n" +
                    "} ]", response.content)
        }
    }

    @Test
    fun testPostLists() = testApp {

        every { usersService.createUserList(ucl2.userId, ucl2.name, ucl2.countries) } returns ucl2._id

        with(handleRequest(HttpMethod.Post, "/api/user/userId2/countries") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"${ucl2.name}\",\"countries\":[ ${ucl2.countries.joinToString { "\"${it}\"" }} ]}")
        }) {
            assertEquals(HttpStatusCode.Created, response.status())
            assertEquals(ucl2._id.toString(), response.content)
        }

        every { usersService.createUserList(ucl2.userId, ucl2.name, ucl2.countries) } returns null

        with(handleRequest(HttpMethod.Post, "/api/user/userId2/countries") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"${ucl2.name}\",\"countries\":[ ${ucl2.countries.joinToString { "\"${it}\"" }} ]}")
        }) {
            assertEquals(HttpStatusCode.InternalServerError, response.status())
        }

        with(handleRequest(HttpMethod.Post, "/api/user/userId2/countries") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"${ucl2.name}\",\"countries\": not_a_list }")
        }) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }

    }

    @Test
    fun testGetListWithNameAndUserId() = testApp {
        every { usersService.getUserList(ucl3.userId.toString(), ucl3.name) } returns ucl3

        with(handleRequest(HttpMethod.Get, "/api/user/userId3/countries/list/TEST_3")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("{\n" +
                    "  \"_id\" : \"${ucl3._id}\",\n" +
                    "  \"userId\" : \"${ucl3.userId}\",\n" +
                    "  \"name\" : \"${ucl3.name}\",\n" +
                    "  \"countries\" : [ ${ucl3.countries.joinToString { "\"${it}\"" }} ],\n" +
                    "  \"creationDate\" : \"${ucl3.creationDate}\"\n" +
                    "}", response.content)
        }
        every { usersService.getUserList(ucl3.userId.toString(), "TEST_NO_EXISTS") } returns null

        with(handleRequest(HttpMethod.Get, "/api/user/userId3/countries/list/TEST_NO_EXISTS")) {
            assertEquals(HttpStatusCode.NoContent, response.status())
        }
    }

    @Test
    fun testDeleteLists() = testApp {
        every { usersService.delete(ucl3.userId, ucl3.name) } returns true

        with(handleRequest(HttpMethod.Delete, "/api/user/userId3/countries/list/TEST_3")) {
            assertEquals(HttpStatusCode.Accepted, response.status())
        }
        every { usersService.delete(ucl3.userId, ucl3.name) } returns false

        with(handleRequest(HttpMethod.Delete, "/api/user/userId3/countries/list/TEST_3")) {
            assertEquals(HttpStatusCode.NotModified, response.status())
        }
        every { usersService.delete(ucl3.userId, ucl3.name) } returns null

        with(handleRequest(HttpMethod.Delete, "/api/user/userId3/countries/list/TEST_3")) {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }
    }

    @Test
    fun testPatchLists() = testApp {
        every { usersService.update(ucl4.userId, ucl4.name, "new_name", mutableSetOf("new_country")) } returns ucl4._id

        with(handleRequest(HttpMethod.Patch, "/api/user/userId3/countries/list/TEST_4") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"new_name\",\"countries\":[ \"new_country\" ]}")
        }) {
            assertEquals(HttpStatusCode.Accepted, response.status())
            assertEquals(ucl4._id.toString(), response.content)
        }

        every { usersService.update(ucl4.userId, ucl4.name, "new_name", mutableSetOf("new_country")) } returns null

        with(handleRequest(HttpMethod.Patch, "/api/user/userId3/countries/list/TEST_4") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"new_name\",\"countries\":[ \"new_country\" ]}")
        }) {
            assertEquals(HttpStatusCode.NotModified, response.status())
        }

        with(handleRequest(HttpMethod.Patch, "/api/user/userId3/countries/list/TEST_4") {
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