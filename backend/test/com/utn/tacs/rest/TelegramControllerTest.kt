package com.utn.tacs.rest

import com.utn.tacs.*
import com.utn.tacs.TokenTestGenerator.addJwtHeader
import com.utn.tacs.countries.CountriesService
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.telegram.TelegramRepository
import com.utn.tacs.user.UsersRepository
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
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.litote.kmongo.toId

class TelegramControllerTest {
    private val telegramId = "123"
    private val id = ObjectId().toString()
    private val authUser: User = User(id.toId(), "admin", "admin", "admin", true)
    private val user = User(id.toId(), "name1", "mail1", "password1")

    private val telegramSession = TelegramSession(user._id, telegramId)
    private val telegramUser = TelegramUser(telegramId, user.email, user.password)
    private val telegramUserJson = "{\"telegramId\":\"${telegramUser.telegramId}\",\n" +
                                    "\"username\":\"${telegramUser.username}\",\n" +
                                    "\"password\":\"${telegramUser.password}\"}"

    private val countriesList: UserCountriesList = UserCountriesList(user._id, "TEST", mutableSetOf("COUNTRY_1", "country_2", "CoUnTrY_3"))

    private val usersRepository = mockk<UsersRepository>()
    private val userListRepository = mockk<UserListsRepository>()
    private val telegramRepository = mockk<TelegramRepository>()
    private val usersService = mockk<UsersService>()
    private val countriesService = mockk<CountriesService>()

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            authentication(usersRepository)
            contentNegotiator()
            telegram(usersRepository, userListRepository, telegramRepository, usersService, countriesService)
        }, callback)
    }

    @Before
    fun before() {
        every { usersRepository.getUserOrFail(id) } returns authUser
    }

    @Test
    fun getTelegramSessionTest() = testApp {
        every { telegramRepository.getTelegramSession(telegramId) } returns telegramSession

        with(handleRequest(HttpMethod.Get, "/api/telegram?telegramId=$telegramId") {
            addJwtHeader(authUser)
        }) {
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
            Assertions.assertEquals(("{  \"_id\" : \"${telegramSession._id}\",  " +
                                    "\"userId\" : \"$id\",  " +
                                    "\"telegramId\" : \"123\"}").replace("\n", "").replace("\r", ""),
                    response.content!!.replace("\n", "").replace("\r", ""))
        }

        every { telegramRepository.getTelegramSession(telegramId) } returns null

        with(handleRequest(HttpMethod.Get, "/api/telegram?telegramId=$telegramId") {
            addJwtHeader(authUser)
        }) {
            Assertions.assertEquals(HttpStatusCode.NotFound, response.status())
        }
    }

    @Test
    fun loginTest() = testApp {
        every { telegramRepository.getTelegramSession(telegramUser.telegramId) } returns telegramSession

        with(handleRequest(HttpMethod.Post, "/api/telegram/login") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody(telegramUserJson)
        }) {
            Assertions.assertEquals(HttpStatusCode(402, "Telegram User already logged on"), response.status())
        }

        every { usersRepository.getUserByEmailAndPass(user.email, user.password!!) } returns user
        every { telegramRepository.getTelegramSession(telegramId) } returns null
        every { telegramRepository.createNewTelegramSession(user, telegramUser) } returns null

        with(handleRequest(HttpMethod.Post, "/api/telegram/login") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody(telegramUserJson)
        }) {
            Assertions.assertEquals(HttpStatusCode.Conflict, response.status())
        }

        every { telegramRepository.createNewTelegramSession(user, telegramUser) } returns telegramSession

        with(handleRequest(HttpMethod.Post, "/api/telegram/login") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody(telegramUserJson)
        }) {
            Assertions.assertEquals(("{\n" +
                                    "  \"_id\" : \"${telegramSession._id}\",\n" +
                                    "  \"userId\" : \"${id}\",\n" +
                                    "  \"telegramId\" : \"123\"\n" +
                                    "}").replace("\n","").replace("\r",""),
                    response.content!!.replace("\n","").replace("\r",""))
        }

        with(handleRequest(HttpMethod.Post, "/api/telegram/login") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody("{\"telegramId\":\"${telegramUser.telegramId}\",\"username\":\"\",\"password\":\"\"}")
        }) {
            Assertions.assertEquals(HttpStatusCode(400, "username and password can't be null"), response.status())
        }
    }

    @Test
    fun logoutTest() = testApp {
        every { telegramRepository.getTelegramSession(telegramUser.telegramId) } returns null

        with(handleRequest(HttpMethod.Post, "/api/telegram/logout") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody(telegramUserJson)
        }) {
            Assertions.assertEquals(HttpStatusCode(404, "Session not found"), response.status())
        }

        every { telegramRepository.getTelegramSession(telegramUser.telegramId) } returns telegramSession
        every { telegramRepository.deleteTelegramSession(telegramUser.telegramId) } returns Unit

        with(handleRequest(HttpMethod.Post, "/api/telegram/logout") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody(telegramUserJson)
        }) {
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    /*@Test
    fun getcountryListTest() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/telegram/countryList") {
            addJwtHeader(authUser)
        }) {
            Assertions.assertEquals(HttpStatusCode.BadRequest, response.status())
        }

        every { telegramRepository.getTelegramSession(telegramId) } returns null

        with(handleRequest(HttpMethod.Get, "/api/telegram/countryList?telegramId=$telegramId") {
            addJwtHeader(authUser)
        }) {
            Assertions.assertEquals(HttpStatusCode(400, "Id not found"), response.status())
        }

        every { telegramRepository.getTelegramSession(telegramId) } returns telegramSession
        every { userListsRepository.getUserLists(user._id.toString()) } returns listOf(countriesList)

        with(handleRequest(HttpMethod.Get, "/api/telegram/countryList?telegramId=$telegramId") {
            addJwtHeader(authUser)
        }) {
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
            Assertions.assertEquals("", response.content)
        }
    }*/

    /*@Test
    fun getUserCountriesListTest() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/telegram/countryList/${countriesList._id}") {
            addJwtHeader(authUser)
        }) {
            Assertions.assertEquals(HttpStatusCode.BadRequest, response.status())
        }

        every { telegramRepository.authenticated(telegramId, countriesList._id.toString()) } returns false

        with(handleRequest(HttpMethod.Get, "/api/telegram/countryList/${countriesList._id}?telegramId=$telegramId") {
            addJwtHeader(authUser)
        }) {
            Assertions.assertEquals(HttpStatusCode(400, "Id not found"), response.status())
        }

        every { telegramRepository.authenticated(telegramId, countriesList._id.toString()) } returns true
        every { userListsRepository.getUserList(user._id.toString()) } returns UserCountriesList(user._id, "name", mutableSetOf())

        with(handleRequest(HttpMethod.Get, "/api/telegram/countryList/${countriesList._id}?telegramId=$telegramId") {
            addJwtHeader(authUser)
        }) {
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
            Assertions.assertEquals(emptyList<String>(), response.content)
        }
    }*/

    /*@Test
    fun newListTest() = testApp {
        val listJson = "{\"name\":\"name\",\"countries\":[\"Argentina\",\"Chile\"]}"

        every { telegramRepository.getTelegramSession(telegramId) } returns null

        with(handleRequest(HttpMethod.Post, "/api/telegram/countryList?telegramId=$telegramId") {
            addJwtHeader(authUser)
        }) {
            Assertions.assertEquals(HttpStatusCode(400, "Id not found"), response.status())
        }


        every { telegramRepository.getTelegramSession(telegramId) } returns telegramSession
        every { countriesService.getAllCountries() } returns telegramSession

        with(handleRequest(HttpMethod.Post, "/api/telegram/countryList?telegramId=$telegramId") {
            addJwtHeader(authUser)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString());
            setBody(listJson)
        }) {
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
            Assertions.assertEquals(("{  \"_id\" : \"${telegramSession._id}\",  " +
                    "\"userId\" : \"userId1\",  " +
                    "\"telegramId\" : \"123\"}").replace("\n", "").replace("\r", ""),
                    response.content!!.replace("\n", "").replace("\r", ""))
        }
    }*/
}