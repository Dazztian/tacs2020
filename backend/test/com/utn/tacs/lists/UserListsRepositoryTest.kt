package com.utn.tacs.lists


import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import org.junit.Before
import org.junit.Test
import org.litote.kmongo.*
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue


@Testcontainers
class UserListsRepositoryTest {

    private lateinit var userCountryList1: UserCountriesList
    private lateinit var userCountryList2: UserCountriesList
    private lateinit var userCountryList3: UserCountriesList
    private lateinit var userCountryList4: UserCountriesList

    private lateinit var userId1: Id<User>
    private lateinit var userId2: Id<User>

    private lateinit var mongoDatabase: MongoDatabase

    @Container
    var mongoContainer = GenericContainer<Nothing>("mongo:3.6.18").apply { withExposedPorts(27017) }


    // TODO tal vez esto convenga convertirlo en una clase aparte y que genere todo lo necesario para todos los tests e ir llamandolo.
    @Before
    fun before() {

        mongoContainer.start()

        userId1 = newId()
        userId2 = newId()

        userCountryList1 = UserCountriesList(userId1, "list1", mutableSetOf("Country1", "Country2", "Country3"))

        userCountryList2 = UserCountriesList(userId1, "list2", mutableSetOf("Country4", "Country5", "Country6"))

        userCountryList3 = UserCountriesList(userId2, "list3", mutableSetOf("Country7", "Country8", "Country9"))

        userCountryList4 = UserCountriesList(userId2, "list4", mutableSetOf("Country10", "Country11", "Country12"))


        mongoDatabase = KMongo.createClient("mongodb://${mongoContainer.containerIpAddress}:${mongoContainer.getMappedPort(27017)}").getDatabase("test")

        mongoDatabase.getCollection<UserCountriesList>().createIndex("{userId: 1, name: 1}")
        mongoDatabase.getCollection<UserCountriesList>().insertMany(mutableListOf(userCountryList1, userCountryList2, userCountryList3, userCountryList4))
    }

    @Test
    fun testGetUserListsByUserId() {
        val repo = UserListsRepository(mongoDatabase)

        val expected1 = listOf(userCountryList1, userCountryList2)
        val expected2 = listOf(userCountryList3, userCountryList4)

        assertEquals(expected1, repo.getUserLists(userId1))
        assertEquals(expected2, repo.getUserLists(userId2))
        assertTrue(repo.getUserLists(newId()).isEmpty())
    }


    @Test
    fun testGetUserListsByUserIdAndName() {
        val repo = UserListsRepository(mongoDatabase)

        assertEquals(userCountryList1, repo.getUserList(userId1, "list1"))
        assertNull(repo.getUserList(newId(), ""))
        assertNull(repo.getUserList(userId1, "NON_EXISTENT"))
    }

    @Test
    fun testCreateUserList() {

        val repo = UserListsRepository(mongoDatabase)

        //Before this user 1 has only 2 lists.

        var r = repo.createUserList(UserCountriesList(userId1, "new_list", mutableSetOf("Country13", "Country14")))
        assertEquals(3, repo.getUserLists(userId1).size)

        //Before and after this user 2 must have only 2 lists
        repo.createUserList(UserCountriesList(userId2, "new_list", mutableSetOf("Country13", "Country14")))
        assertEquals(2, repo.getUserLists(userId2).size)


        //TODO THINK ABOUT THIS
/*
        //Should not add new list when the user does not exist
        repo.createUserList(4, "list4", listOf("Country13", "Country14"))
        assertEquals(0, repo.getUserLists(4).size)*/
    }

    //TODO ADD MORE TESTS
}