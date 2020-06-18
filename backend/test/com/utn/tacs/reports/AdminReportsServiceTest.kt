package com.utn.tacs.reports

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.UserCountriesListResponse
import com.utn.tacs.UserData
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.user.USERS_COLLECTION_NAME
import com.utn.tacs.user.UsersRepository
import io.ktor.features.NotFoundException
import org.bson.Document
import org.bson.types.ObjectId
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.litote.kmongo.Id
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.litote.kmongo.newId
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@Testcontainers
class AdminReportsServiceTest {

    @Test
    fun testGetUserData() {
        val service = AdminReportsService(usersRepository, userListRepository)

        val result: UserData? = service.getUserData(userId1.toString())

        Assert.assertNotNull(result)
        Assert.assertEquals(user1, result?.user)
        Assert.assertEquals(2, result?.listsQuantity)
        Assert.assertEquals(6, result?.countriesTotal)

        assertThrows<NotFoundException> { service.getUserData(ObjectId().toString()) }
    }

    @Test
    fun testGetListsByDate() {
        val service = AdminReportsService(usersRepository, userListRepository)

        val result = service.getRegisteredUserListsBetween(LocalDate.parse("2020-05-23"), LocalDate.parse("2020-05-23"))

        Assert.assertNotNull(result)
        Assert.assertEquals(1, result.size)
        Assert.assertTrue(result.contains(userCountryList1))

        val result2 = service.getRegisteredUserListsBetween(LocalDate.parse("2020-05-05"), LocalDate.parse("2020-05-25"))

        Assert.assertNotNull(result2)
        Assert.assertEquals(4, result2.size)
        Assert.assertTrue(result2.containsAll(listOf(userCountryList1, userCountryList2, userCountryList3, userCountryList4)))

        val result3 = service.getRegisteredUserListsBetween(LocalDate.parse("2021-05-05"), LocalDate.parse("2021-05-25"))

        Assert.assertNotNull(result3)
        Assert.assertEquals(0, result3.size)
    }

    @Test
    fun testGetListQuantity() {
        val service = AdminReportsService(usersRepository, userListRepository)
        Assert.assertEquals(4, service.getListsQuantity())
    }

    @Test
    fun testGetUsersByCountry() {
        val service = AdminReportsService(usersRepository, userListRepository)

        val result = service.getUsersByCountry("Country3")
        Assert.assertEquals(2, result.size)
        Assert.assertTrue(result.contains(user1._id.toString()))
        Assert.assertTrue(result.contains(user2._id.toString()))

        val result2 = service.getUsersByCountry("Country6")
        Assert.assertEquals(1, result2.size)
        Assert.assertTrue(result2.contains(user1._id.toString()))

        val result3 = service.getUsersByCountry("NON_EXISTENT")
        Assert.assertEquals(0, result3.size)
    }

    @Test
    fun testGetListComparison() {
        val service = AdminReportsService(usersRepository, userListRepository)

        val result = service.getListComparison(userCountryList1._id.toString(), userCountryList4._id.toString())
        Assert.assertNotNull(result)
        Assert.assertEquals(UserCountriesListResponse(userCountryList1), result.userCountryList1)
        Assert.assertEquals(UserCountriesListResponse(userCountryList4), result.userCountryList2)
        Assert.assertEquals(setOf("Country1", "Country2", "Country3"), result.sharedCountries)

        val result2 = service.getListComparison(userCountryList1._id.toString(), userCountryList2._id.toString())
        Assert.assertNotNull(result2)
        Assert.assertEquals(UserCountriesListResponse(userCountryList1), result2.userCountryList1)
        Assert.assertEquals(UserCountriesListResponse(userCountryList2), result2.userCountryList2)
        Assert.assertTrue(result2.sharedCountries.isEmpty())

        assertThrows<NotFoundException> { service.getListComparison(userCountryList1._id.toString(), ObjectId().toString()) }

        assertThrows<NotFoundException> { service.getListComparison(ObjectId().toString(), ObjectId().toString()) }
    }

    companion object {
        @Container
        var mongoContainer = GenericContainer<Nothing>("mongo:3.6.18").apply { withExposedPorts(27017) }

        private lateinit var userCountryList1: UserCountriesList
        private lateinit var userCountryList2: UserCountriesList
        private lateinit var userCountryList3: UserCountriesList
        private lateinit var userCountryList4: UserCountriesList

        private lateinit var userId1: Id<User>
        private lateinit var userId2: Id<User>

        private lateinit var user1: User
        private lateinit var user2: User

        private lateinit var mongoDatabase: MongoDatabase

        private lateinit var userListRepository: UserListsRepository
        private lateinit var usersRepository: UsersRepository

        @BeforeClass
        @JvmStatic
        fun before() {

            mongoContainer.start()

            userId1 = newId()
            userId2 = newId()

            user1 = User("user1", "usermail1", "password1", userId1)
            user2 = User("user2", "usermail2", "password2", userId2)


            userCountryList1 = UserCountriesList(userId1, "list1", mutableSetOf("Country1", "Country2", "Country3"), LocalDate.parse("2020-05-23"))

            userCountryList2 = UserCountriesList(userId1, "list2", mutableSetOf("Country4", "Country5", "Country6"), LocalDate.parse("2020-05-15"))

            userCountryList3 = UserCountriesList(userId2, "list3", mutableSetOf("Country7", "Country8", "Country3"), LocalDate.parse("2020-05-21"))

            userCountryList4 = UserCountriesList(userId2, "list4", mutableSetOf("Country1", "Country2", "Country3"), LocalDate.parse("2020-05-21"))


            mongoDatabase = KMongo.createClient("mongodb://${mongoContainer.containerIpAddress}:${mongoContainer.getMappedPort(27017)}").getDatabase("test")

            //TODO this is temporal. This must be replaced with correct creation on db when executing the code.
            val index = Document("name", 1).append("userId", 1)
            mongoDatabase.getCollection("userCountriesList").createIndex(index, IndexOptions().unique(true))

            mongoDatabase.getCollection<User>(USERS_COLLECTION_NAME).insertMany(mutableListOf(user1, user2))
            mongoDatabase.getCollection<UserCountriesList>().insertMany(mutableListOf(userCountryList1, userCountryList2, userCountryList3, userCountryList4))

            userListRepository = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))
            usersRepository = UsersRepository(mongoDatabase)
        }

        @AfterClass
        @JvmStatic
        fun after() {
            mongoDatabase.getCollection("userCountriesList").drop()
            mongoDatabase.getCollection("users").drop()

        }
    }

}