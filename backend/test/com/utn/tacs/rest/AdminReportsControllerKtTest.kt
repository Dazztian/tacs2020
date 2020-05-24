package com.utn.tacs.rest

import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.contentNegotiator
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.reports.AdminReportsService
import com.utn.tacs.user.UsersRepository
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.litote.kmongo.toId

class AdminReportsControllerKtTest {

    private lateinit var user1: User
    private lateinit var user2: User

    private lateinit var ucl1: UserCountriesList
    private lateinit var ucl2: UserCountriesList
    private lateinit var ucl3: UserCountriesList
    private lateinit var ucl4: UserCountriesList
    private lateinit var ucl5: UserCountriesList

    private val usersRepository = mockk<UsersRepository>()

    private val userListRepository = mockk<UserListsRepository>()

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            contentNegotiator()
            adminReports(AdminReportsService(usersRepository, userListRepository))
        }, callback)
    }

    @Before
    fun before() {
        user1 = User("userId1".toId(), "name1", "mail1", "password1")
        user2 = User("userId2".toId(), "name2", "mail2", "password2")

        ucl1 = UserCountriesList(user1._id, "TEST", mutableSetOf("TEST_COUNTRY"))
        ucl2 = UserCountriesList(user1._id, "TEST_2", mutableSetOf("COUNTRY_1", "country_2", "CoUnTrY_3"))
        ucl3 = UserCountriesList(user1._id, "TEST_3", mutableSetOf("COUNTRY_1"))
        ucl4 = UserCountriesList(user2._id, "TEST_4", mutableSetOf("country_2"))
        ucl5 = UserCountriesList(user2._id, "TEST_5", mutableSetOf("CoUnTrY_3"))
    }

    @Test
    fun testGetUserInfo() = testApp {
        every { usersRepository.getUserById(user1._id) } returns user1
        every { userListRepository.getUserLists(user1._id) } returns listOf(ucl1, ucl2, ucl3)

        with(handleRequest(HttpMethod.Get, "/api/admin/report/info/userId1")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("{\n" +
                    "  \"user\" : {\n" +
                    "    \"name\" : \"${user1.name}\",\n" +
                    "    \"email\" : \"${user1.email}\",\n" +
                    "    \"password\" : \"${user1.password}\",\n" +
                    "    \"_id\" : \"${user1._id}\",\n" +
                    "    \"creationDate\" : null\n" +
                    "  },\n" +
                    "  \"listsQuantity\" : 3,\n" +
                    "  \"countriesTotal\" : 5\n" +
                    "}", response.content)
        }

        every { usersRepository.getUserById(user2._id) } returns user2
        every { userListRepository.getUserLists(user2._id) } returns listOf()

        with(handleRequest(HttpMethod.Get, "/api/admin/report/info/userId2")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("{\n" +
                    "  \"user\" : {\n" +
                    "    \"name\" : \"${user2.name}\",\n" +
                    "    \"email\" : \"${user2.email}\",\n" +
                    "    \"password\" : \"${user2.password}\",\n" +
                    "    \"_id\" : \"${user2._id}\",\n" +
                    "    \"creationDate\" : null\n" +
                    "  },\n" +
                    "  \"listsQuantity\" : 0,\n" +
                    "  \"countriesTotal\" : 0\n" +
                    "}", response.content)
        }

        every { usersRepository.getUserById("NON_EXISTENT".toId()) } returns null

        with(handleRequest(HttpMethod.Get, "/api/admin/report/info/NON_EXISTENT")) {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }

    }

}