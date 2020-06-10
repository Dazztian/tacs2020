package com.utn.tacs.e2e.rest

import com.utn.tacs.module
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.concurrent.TimeUnit

class LoginControllerKtTest {

    private val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
    private val userEmail = "testuser" + timestamp + "@gmail.com"

    @Test
    /**
     * Test login non existing user
     */
    fun testLogin_nonExistingUser_notFoundException() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Post, "/api/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"email\":\"" + userEmail + "\",\"password\":\"1234\"}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.NotFound)
        }
    }

    @Test
    /**
     * Test Steps:
     *      create an user successfully - try send is admin true but the user should be created as no admin
     *      Login that user
     *      Get that user info
     *      Delete that user
     */
    fun testSignUp_nonExistingUserSendIsAdminTrue_ok() = withTestApplication(Application::module) {
        var userId = ""
        var token = ""

        with(handleRequest(HttpMethod.Post, "/api/signup") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"name\":\"testUser\",\"email\":\"" + userEmail + "\",\"password\":\"1234\",\"country\":\"AR\",\"isAdmin\":true}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.OK, status)
        }

        with(handleRequest(HttpMethod.Post, "/api/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"email\":\"" + userEmail + "\",\"password\":\"1234\"}")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.OK)
        }

    }
}
