package com.utn.tacs.e2e.rest

import com.utn.tacs.exception.exceptionHandler
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.reports.AdminReportsService
import com.utn.tacs.rest.adminReports
import com.utn.tacs.rest.countriesRoutes
import com.utn.tacs.rest.login
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.testcontainers.junit.jupiter.*
import com.utn.tacs.user.UsersRepository
import org.testcontainers.containers.GenericContainer
import com.mongodb.client.MongoDatabase
import com.utn.tacs.auth.AuthorizationService
import com.utn.tacs.auth.JwtConfig
import com.utn.tacs.user.UsersService
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import org.junit.BeforeClass
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.litote.kmongo.KMongo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Testcontainers
class AdminReportsControllerTest {

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            authentication(usersRepository)
            contentNegotiator()
            adminReports(adminReportsService)
            exceptionHandler()
        }, callback)
    }

    @Test
    fun testGetUsersListReport_invalidToken_unauthorized() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/admin/report"){
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer token")
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.Unauthorized, status)
        }
    }

    @Test
    fun testGetUsersListReport_ok() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/admin/report"){
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer " + token)
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            val users = jacksonObjectMapper().readValue<List<UserBasicData>>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            users.forEach{ user ->
                assertNotNull(user.id)
                assertNotNull(user.email)
                assertNotNull(user.name)
            }
        }
    }

    @Test
    fun testGetUserData_ok() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/admin/report/" + adminUser._id.toString()){
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer " + token)
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testCompareLists_ok() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/admin/report/lists/compare?list1=" + list1Id + "&list2=" + list2Id){
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer " + token)
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            val comparison = jacksonObjectMapper().readValue<UserListComparision>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(comparison.userCountryList1.id, list1Id)
            assertEquals(comparison.userCountryList1.name, list1.name)
            assertEquals(2, comparison.userCountryList1.countries.size)
            assertTrue(comparison.userCountryList1.countries.map{ it.iso2 }.contains("AR"))
            assertTrue(comparison.userCountryList1.countries.map{ it.iso2 }.contains("BR"))
            assertEquals(comparison.userCountryList2.id, list2Id)
            assertEquals(comparison.userCountryList2.name, list2.name)
            assertEquals(2, comparison.userCountryList2.countries.size)
            assertTrue(comparison.userCountryList2.countries.map{ it.iso2 }.contains("AR"))
            assertTrue(comparison.userCountryList2.countries.map{ it.iso2 }.contains("UY"))
            assertEquals(comparison.sharedCountries, setOf("AR"))
        }
    }

    @Test
    fun testGetListOfCountry_ok() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/admin/report/AR/list"){
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer " + token)
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            val dataResponse = jacksonObjectMapper().readValue<CountryListsDataResponse>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            assertNotNull(dataResponse.totalUsers)
            assertNotNull(dataResponse.users)
            assertTrue(dataResponse.users.contains(adminUser._id.toString()))
        }
    }

    @Test
    fun testGetListsTotal_ok() = testApp {
        with(handleRequest(HttpMethod.Get, "/api/admin/report/lists/total"){
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer " + token)
        }) {
            val status = response.status() ?: throw Exception("not response status code found")
            val dataResponse = jacksonObjectMapper().readValue<ListTotalResponse>(response.content!!)
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(dataResponse.totalLists >= 2)
        }
    }

    companion object {
        private lateinit var usersRepository: UsersRepository
        private lateinit var userListsRepository: UserListsRepository
        private lateinit var adminReportsService: AdminReportsService
        private lateinit var authorizationService: AuthorizationService
        private lateinit var token: String
        private lateinit var adminUser: User
        private lateinit var list1Id: String
        private lateinit var list2Id: String
        private lateinit var list1: UserCountriesList
        private lateinit var list2: UserCountriesList

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
            adminReportsService = AdminReportsService(usersRepository, userListsRepository)
            authorizationService = AuthorizationService(usersRepository, UsersService(usersRepository, userListsRepository))

            adminUser = authorizationService.auth(usersRepository.adminUserEmail, "adminutn")
            token = JwtConfig.makeToken(adminUser)

            list1 = UserCountriesList(adminUser._id,"list name 1", mutableSetOf("AR","BR"))
            list2 = UserCountriesList(adminUser._id,"list name 2", mutableSetOf("AR","UY"))
            list1Id = userListsRepository.createUserList(list1)!!
            list2Id = userListsRepository.createUserList(list2)!!
        }
    }
}