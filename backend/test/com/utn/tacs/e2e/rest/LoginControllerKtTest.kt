package com.utn.tacs.e2e.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.LoginResponse
import com.utn.tacs.module
import com.utn.tacs.usersRepository
import com.utn.tacs.usersService
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.AfterClass
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoginControllerKtTest {
    @Test
    fun testSignUpAndLoginFlow() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Post, "/api/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"email\":\"" + userEmail + "\",\"password\":\"1234\"}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.Unauthorized, status)
        }

        with(handleRequest(HttpMethod.Post, "/api/signup") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"name\":\"testUser\",\"email\":\"" + userEmail + "\",\"password\":\"1234\",\"country\":\"AR\",\"isAdmin\":true}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            val loginResponse: LoginResponse = jacksonObjectMapper().readValue<LoginResponse>(response.content!!)

            assertEquals(HttpStatusCode.OK, status)
            assertTrue(loginResponse.token.isNotEmpty())
            assertTrue(loginResponse.user.email.isNotEmpty())
            assertTrue(loginResponse.user.id.isNotEmpty())
            assertFalse(loginResponse.user.isAdmin)
            assertTrue(loginResponse.user.name.isNotEmpty())
        }

        with(handleRequest(HttpMethod.Post, "/api/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"email\":\"" + userEmail + "\",\"password\":\"12345\"}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.Unauthorized, status)
        }

        with(handleRequest(HttpMethod.Post, "/api/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"email\":\"" + userEmail + "\",\"password\":\"1234\"}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.OK)
        }

        with(handleRequest(HttpMethod.Post, "/api/signup") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"name\":\"testUser\",\"email\":\"" + userEmail + "\",\"password\":\"1234\",\"country\":\"AR\",\"isAdmin\":true}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    companion object {
        private val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        private val userEmail = "testuser" + timestamp + "@gmail.com"

        @AfterClass
        @JvmStatic
        fun cleanData() {
            usersService.deleteUserByEmail(userEmail)
        }
    }
}
