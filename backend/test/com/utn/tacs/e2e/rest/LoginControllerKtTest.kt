package com.utn.tacs.e2e.rest

import org.junit.Test
import org.junit.Before
import io.ktor.server.testing.withTestApplication
import io.ktor.application.Application
import com.utn.tacs.module
import io.ktor.server.testing.handleRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.Country
import com.utn.tacs.LoginResponse
import com.utn.tacs.SignUpRequest
import io.ktor.http.*
import io.ktor.server.testing.setBody
import org.json.JSONObject
import org.junit.jupiter.api.Assertions
import kotlin.test.assertNotNull
import org.litote.kmongo.id.jackson.IdJacksonModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
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
            setBody("{\"email\":\""+ userEmail +"\",\"password\":\"1234\"}")
        }){
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
    fun testSignUp_nonExistingUserSendIsAdmingTrue_ok() = withTestApplication(Application::module) {
        var userId = ""
        var token = ""

        with(handleRequest(HttpMethod.Post, "/api/signup") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"name\":\"testUser\",\"email\":\"" + userEmail + "\",\"password\":\"1234\",\"country\":\"AR\",\"isAdmin\":true}")
        }){
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.OK, status)

            val jsonData = JSONObject(response.content)
            userId = (jsonData.get("user") as JSONObject).get("_id").toString()
            token = jsonData.get("token").toString()
        }

        with(handleRequest(HttpMethod.Post, "/api/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{\"email\":\""+ userEmail +"\",\"password\":\"1234\"}")
        }){
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.OK)
        }

        with(handleRequest(HttpMethod.Get, "/api/user/${userId}") {
            addHeader(HttpHeaders.Authorization, "Bearer ${token}")
        }){
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.OK)
        }

        with(handleRequest(HttpMethod.Delete, "/api/user/${userId}") {
            addHeader(HttpHeaders.Authorization, "Bearer ${token}")
        }){
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(status, HttpStatusCode.Accepted)
        }
    }
}
