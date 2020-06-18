package com.utn.tacs.rest

import com.utn.tacs.*
import com.utn.tacs.TokenTestGenerator.addJwtHeader
import com.utn.tacs.user.UsersRepository
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
import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.litote.kmongo.id.toId

class UserCountriesListControllerKtTest {

    private lateinit var authUser: User
    private lateinit var ucl1: UserCountriesListResponse
    private lateinit var ucl2: UserCountriesListResponse
    private lateinit var ucl3: UserCountriesListResponse
    private lateinit var ucl4: UserCountriesListResponse
    private lateinit var ucl5: UserCountriesListResponse

    private val usersService = mockk<UsersService>()
    private val usersRepository = mockk<UsersRepository>()

    /*
    * When using Controllers that need authentication, authentication must be the first installed, and
    * userRepository must be a mock that returns the test user.
    * */
    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            authentication(usersRepository)
            contentNegotiator()
            userCountriesListRoutes(usersService)
        }, callback)
    }

    @Before
    fun before() {
        ucl1 = UserCountriesListResponse("id1", "TEST_1", mutableSetOf(CountriesNamesResponse("TEST_COUNTRY")),"")
        ucl2 = UserCountriesListResponse("id2", "TEST_2", mutableSetOf(CountriesNamesResponse("AR"),CountriesNamesResponse("UY"),CountriesNamesResponse("US")),"")
        ucl3 = UserCountriesListResponse("id3", "TEST_3", mutableSetOf(CountriesNamesResponse("AR")),"")
        ucl4 = UserCountriesListResponse("id4", "TEST_4", mutableSetOf(CountriesNamesResponse("UY")),"")
        ucl5 = UserCountriesListResponse("id5", "TEST_5", mutableSetOf(CountriesNamesResponse("US")),"")

        authUser = User(ObjectId().toId(), "test-user")

        every { usersRepository.getUserOrFail(any()) } returns authUser

    }

    @Test
    fun testGetLists() = testApp {
        every { usersService.getUserLists("userId1") } returns listOf(ucl1)

        with(handleRequest(HttpMethod.Get, "/api/user/userId1/lists/") {
            addJwtHeader(authUser)
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(formatJson("[ {\n" +
                    "  \"id\" : \"${ucl1.id}\",\n" +
                    "  \"name\" : \"${ucl1.name}\",\n" +
                    "  \"countries\" : [ ${ucl1.countries.joinToString { "{\"name\":\"${it.name}\" , \"iso2\":\"${it.iso2}\" }" }} ]\n," +
                    "  \"creationDate\": \"\" " +
                    "} ]"), formatJson(response.content!!))
        }

        every { usersService.getUserLists("nonExistentId") } returns listOf()

        with(handleRequest(HttpMethod.Get, "/api/user/nonExistentId/lists/") {
            addJwtHeader(authUser)
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("[ ]", response.content)
        }

        every { usersService.getUserLists("userId3") } returns listOf(ucl3, ucl4, ucl5)

        with(handleRequest(HttpMethod.Get, "/api/user/userId3/lists/") {
            addJwtHeader(authUser)
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(formatJson("[ {\n" +
                    "  \"id\" : \"${ucl3.id}\",\n" +
                    "  \"name\" : \"${ucl3.name}\",\n" +
                    "  \"countries\" : [ ${ucl3.countries.joinToString { "{\"name\":\"${it.name}\" , \"iso2\":\"${it.iso2}\" }" }} ] , \n" +
                    "  \"creationDate\": \"\" " +
                    "}," +
                    " {\n" +
                    "  \"id\" : \"${ucl4.id}\",\n" +
                    "  \"name\" : \"${ucl4.name}\",\n" +
                    "  \"countries\" : [ ${ucl4.countries.joinToString { "{\"name\":\"${it.name}\" , \"iso2\":\"${it.iso2}\" }" }} ] , \n" +
                    "  \"creationDate\": \"\" " +
                    "}, {\n" +
                    "  \"id\" : \"${ucl5.id}\",\n" +
                    "  \"name\" : \"${ucl5.name}\",\n" +
                    "  \"countries\" : [ ${ucl5.countries.joinToString { "{\"name\":\"${it.name}\" , \"iso2\":\"${it.iso2}\" }" }} ] , \n" +
                    "  \"creationDate\": \"\" " +
                    "} ]"), formatJson(response.content!!))
        }
    }

    @Test
    fun testPostLists() = testApp {
        val userId = "userId2"
        every { usersService.createUserList(userId, ucl2.name, ucl2.countries.map { it.iso2 }.toMutableSet()) } returns ucl2

        with(handleRequest(HttpMethod.Post, "/api/user/" + userId + "/lists") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"${ucl2.name}\",\"countries\":[ ${ucl2.countries.joinToString { "\"${it.iso2}\"" }} ]}")
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(formatJson("{\n" +
                    "  \"id\" : \"id2\",\n" +
                    "  \"name\" : \"TEST_2\",\n" +
                    "  \"countries\" : [ {\"name\":\"Argentina\",\"iso2\":\"AR\"},{\"name\":\"Uruguay\",\"iso2\":\"UY\"},{\"name\":\"US\",\"iso2\":\"US\"} ]\n ," +
                    " \"creationDate\" : \"\" " +
                    "}"), formatJson(response.content!!))
        }

        every { usersService.createUserList(userId, ucl2.name, ucl2.countries.map { it.iso2 }.toMutableSet() ) } throws NotFoundException()

        with(handleRequest(HttpMethod.Post, "/api/user/" + userId + "/lists") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"${ucl2.name}\",\"countries\":[ ${ucl2.countries.joinToString { "\"${it.iso2}\"" }} ]}")
        }) {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }

        with(handleRequest(HttpMethod.Post, "/api/user/" + userId + "/lists") {
            addJwtHeader(authUser)
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

        with(handleRequest(HttpMethod.Get, "/api/user/" + userId + "/lists/TEST_3") {
            addJwtHeader(authUser)
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(formatJson("{\n" +
                    "  \"id\" : \"${ucl3.id}\",\n" +
                    "  \"name\" : \"${ucl3.name}\",\n" +
                    "  \"countries\" : [ ${ucl3.countries.joinToString { "{\"name\":\"${it.name}\" , \"iso2\":\"${it.iso2}\" }" }} ]\n," +
                    " \"creationDate\" : \"\" " +
                    "}"), formatJson(response.content!!))
        }
        every { usersService.getUserList(userId, "TEST_NO_EXISTS") } throws NotFoundException()

        with(handleRequest(HttpMethod.Get, "/api/user/" + userId + "/lists/TEST_NO_EXISTS")
        {
            addJwtHeader(authUser)
        }) {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }
    }

    @Test
    fun testDeleteLists() = testApp {
        val userId = "userId"

        every { usersService.deleteUserList(userId, ucl3.name) } returns Unit
        with(handleRequest(HttpMethod.Delete, "/api/user/" + userId + "/lists/TEST_3") {
            addJwtHeader(authUser)
        }) {
            assertEquals(HttpStatusCode.Accepted, response.status())
        }

        every { usersService.deleteUserList(userId, ucl3.name) } throws NotFoundException()
        with(handleRequest(HttpMethod.Delete, "/api/user/userId/lists/TEST_3")
        {
            addJwtHeader(authUser)
        }) {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }

    }

    @Test
    fun testPutLists() = testApp {
        val userId = "userId"
        val userListId = "userListId"

        every { usersService.updateUserList(userId, userListId, any()) } returns ucl4

        with(handleRequest(HttpMethod.Put, "/api/user/${userId}/lists/${userListId}") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"new_name\",\"countries\":[ \"new_country\" ]}")
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            val responseExpected = "{\n" +
                    "  \"id\" : \"id4\",\n" +
                    "  \"name\" : \"TEST_4\",\n" +
                    "  \"countries\" : [ { \"name\" : \"Uruguay\", \"iso2\" : \"UY\" } ]\n," +
                    " \"creationDate\" : \"\" " +
                    "}"
            assertEquals(formatJson(responseExpected), formatJson(response.content!!))
        }

        every { usersService.updateUserList(userId, userListId, any()) } throws NotFoundException()
        with(handleRequest(HttpMethod.Put, "/api/user/${userId}/lists/${userListId}") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"new_name\",\"countries\":[ \"new_country\" ]}")
        }) {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }

        with(handleRequest(HttpMethod.Put, "/api/user/${userId}/lists/${userListId}") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"name\":\"new_name\", bad_json ]}")
        }) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    private fun formatJson(json: String): String {
        return json.replace("\n", "").replace("\r", "").replace(" ", "")
    }
}