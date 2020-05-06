package com.utn.tacs.lists


import com.mongodb.MongoClient
import com.utn.tacs.UserCountriesList
import com.utn.tacs.utils.MongoClientGenerator
import org.bson.Document
import org.junit.Before
import org.junit.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@Testcontainers
class UserListsRepositoryTest {

    @Container
    var mongoContainer = GenericContainer<Nothing>("mongo:3.6.18")
            .apply { withExposedPorts(27017) }


    // TODO tal vez esto convenga convertirlo en una clase aparte y que genere todo lo necesario para todos los tests e ir llamandolo.
    @Before
    fun before() {

        mongoContainer.start()

        val user: Document = Document()
        user["id"] = 1
        user["name"] = "user1"
        user["email"] = "email"
        user["password"] = "password"
        user["countriesList"] = listOf(Document().append("name", "list1").append("countries", listOf("Country1", "Country2", "Country3")),
                Document().append("name", "list2").append("countries", listOf("Country4", "Country5", "Country6")))

        val user2: Document = Document()
        user2["id"] = 2
        user2["name"] = "user2"
        user2["email"] = "email2"
        user2["password"] = "password2"
        user2["countriesList"] = listOf(Document().append("name", "list3").append("countries", listOf("Country7", "Country8", "Country9")),
                Document().append("name", "list4").append("countries", listOf("Country10", "Country11", "Country12")))

        val mongoClient = MongoClient(mongoContainer.containerIpAddress, mongoContainer.getMappedPort(27017))
        mongoClient.getDatabase("test").getCollection("users").insertMany(mutableListOf(user, user2))
    }

    @Test
    fun testGetUserListsByUserId() {
        val mongoClient = MongoClient(mongoContainer.containerIpAddress, mongoContainer.getMappedPort(27017))

        val repo = UserListsRepository(mongoClient.getDatabase("test"))

        val expected1 = listOf(UserCountriesList("list1", listOf("Country1", "Country2", "Country3")), UserCountriesList("list2", listOf("Country4", "Country5", "Country6")))
        val expected2 = listOf(UserCountriesList("list3", listOf("Country7", "Country8", "Country9")), UserCountriesList("list4", listOf("Country10", "Country11", "Country12")))

        assertEquals(expected1, repo.getUserLists(1))
        assertEquals(expected2, repo.getUserLists(2))
        assertTrue(repo.getUserLists(3).isEmpty())
    }


    @Test
    fun testGetUserListsByUserIdAndName() {
        val mongoClient = MongoClient(mongoContainer.containerIpAddress, mongoContainer.getMappedPort(27017))

        val repo = UserListsRepository(mongoClient.getDatabase("test"))

        val expected = listOf(UserCountriesList("list4", listOf("Country10", "Country11", "Country12")))

        assertEquals(expected, repo.getUserLists(2, "list4"))
        assertTrue(repo.getUserLists(45, "").isEmpty())
        assertTrue(repo.getUserLists(1, "NON_EXISTENT").isEmpty())
    }

    @Test
    fun testCreateUserList() {
        val mongoClient = MongoClient(mongoContainer.containerIpAddress, mongoContainer.getMappedPort(27017))

        val repo = UserListsRepository(mongoClient.getDatabase("test"))

        //Before this user 1 has only 2 lists.
        repo.createUserList(1, "new_list", listOf("Country13", "Country14"))
        assertEquals(3, repo.getUserLists(1).size)

        //Before and after this user 2 must have only 2 lists
        repo.createUserList(2, "list4", listOf("Country13", "Country14"))
        assertEquals(2, repo.getUserLists(2).size)

        //Should not add new list when the user does not exist
        repo.createUserList(4, "list4", listOf("Country13", "Country14"))
        assertEquals(0, repo.getUserLists(4).size)
    }
}