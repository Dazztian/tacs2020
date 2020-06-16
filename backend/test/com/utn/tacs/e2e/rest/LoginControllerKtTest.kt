package com.utn.tacs.e2e.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.client.MongoDatabase
import com.utn.tacs.LoginResponse
import com.utn.tacs.auth.AuthorizationService
import com.utn.tacs.authentication
import com.utn.tacs.contentNegotiator
import com.utn.tacs.exception.exceptionHandler
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.rest.login
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.user.UsersService
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
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
import kotlin.test.assertTrue

@Testcontainers
class LoginControllerKtTest {
    @Test
    fun testSignUpAndLoginFlow() = withTestApplication({
        exceptionHandler()
        contentNegotiator()
        authentication(usersRepository)
        login(authorizationService, usersService)
    }) {
        with(handleRequest(HttpMethod.Post, "/api/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"email\":\"$userEmail\",\"password\":\"1234\"}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.NotFound, status)
        }

        with(handleRequest(HttpMethod.Post, "/api/signup") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"name\":\"testUser\",\"email\":\"$userEmail\",\"password\":\"1234\",\"country\":\"AR\",\"isAdmin\":true}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            val loginResponse: LoginResponse = jacksonObjectMapper().readValue(response.content!!)

            assertEquals(HttpStatusCode.OK, status)
            assertTrue(loginResponse.token.isNotEmpty())
            assertTrue(loginResponse.user.email.isNotEmpty())
            assertTrue(loginResponse.user.id.isNotEmpty())
            assertFalse(loginResponse.user.isAdmin)
            assertTrue(loginResponse.user.name.isNotEmpty())
        }

        with(handleRequest(HttpMethod.Post, "/api/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"email\":\"$userEmail\",\"password\":\"12345\"}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.Unauthorized, status)
        }

        with(handleRequest(HttpMethod.Post, "/api/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"email\":\"$userEmail\",\"password\":\"1234\"}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.OK)
        }

        with(handleRequest(HttpMethod.Post, "/api/signup") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"name\":\"testUser\",\"email\":\"$userEmail\",\"password\":\"1234\",\"country\":\"AR\",\"isAdmin\":true}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    companion object {
        private lateinit var usersRepository: UsersRepository
        private lateinit var authorizationService: AuthorizationService
        private lateinit var usersService: UsersService
        private lateinit var userListsRepository: UserListsRepository

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

            userListsRepository = UserListsRepository(mongoDatabase, usersRepository)
            usersService = UsersService(usersRepository, userListsRepository)
            authorizationService = AuthorizationService(usersRepository, usersService)

        }
    }
}
