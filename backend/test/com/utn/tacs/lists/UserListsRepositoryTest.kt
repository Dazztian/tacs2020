package com.utn.tacs.lists

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.user.UsersRepository
import io.ktor.features.NotFoundException
import org.bson.Document
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.litote.kmongo.Id
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.litote.kmongo.newId
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate
import kotlin.test.*


@Testcontainers
class UserListsRepositoryTest {

    @Test
    fun testGetUserListByUserListId_resultsFound() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))
        assertEquals(userCountryList1, repo.getUserList(userCountryList1._id.toString()))
    }

    @Test
    fun testGetUserListByUserListId_emptyResult() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))
        assertNull(repo.getUserList(newId<UserListsRepository>().toString()))
    }

    @Test
    fun testGetUserListsByUserId_resultsFound() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))
        val expected = listOf(userCountryList5)
        assertEquals(expected, repo.getUserLists(userId3.toString()))
    }

    @Test
    fun testGetUserListsByUserId_emptyResponse() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))
        val newUserId: Id<User> = newId()

        assertTrue(repo.getUserLists(newUserId.toString()).isEmpty())
    }

    @Test
    fun testGetUserListsByCreationDate() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))

        val result = repo.getUserListsByCreationDate(LocalDate.parse("2020-05-20"), LocalDate.parse("2020-05-24"))
        assertEquals(4, result.size)
        assertTrue(result.containsAll(listOf(userCountryList1, userCountryList2, userCountryList3, userCountryList4)))

        val result2 = repo.getUserListsByCreationDate(LocalDate.parse("2020-05-23"), LocalDate.parse("2020-05-23"))
        assertEquals(1, result2.size)
        assertTrue(result.contains(userCountryList1))

        val result3 = repo.getUserListsByCreationDate(LocalDate.parse("2020-09-23"), LocalDate.parse("2020-05-23"))
        assertEquals(0, result3.size)
    }

    @Test
    fun testGetCount() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))
        assertEquals(5, repo.getCount())
    }

    @Test
    fun testGetAllThatContains_existingCountry() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))

        val result = repo.getAllThatContains("Country1")
        assertEquals(1, result.size)
        assertTrue(result.contains(userCountryList1))
    }

    @Test
    fun testGetAllThatContains_nonExistingCountry() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))

        assertEquals(0, repo.getAllThatContains("NON_EXISTENT").size)
    }

    @Test
    fun testCreateUserListCorrectValues() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))

        //Before this user 1 has only 2 lists.
        val newListInserted = repo.createUserList(UserCountriesList(userId1, "list_name", mutableSetOf("Country13", "Country14")))
                ?: ""
        assertEquals(3, repo.getUserLists(userId1.toString()).size)
        assertEquals(newListInserted, repo.getUserList(newListInserted)?._id.toString())

        //Should add even when countries are empty.
        val newListInserted2 = repo.createUserList(UserCountriesList(userId1, "list_name_empty_set", mutableSetOf()))
                ?: ""
        assertEquals(4, repo.getUserLists(userId1.toString()).size)
        assertEquals(newListInserted2, repo.getUserList(newListInserted2)?._id.toString())
    }

    @Test
    fun testCreateUserListWithRepeatedName() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))
        //Before and after this user 2 must have only 2 lists
        val newListNotInserted = repo.createUserList(UserCountriesList(userId2, "list4", mutableSetOf("Country13", "Country14")))
        assertEquals(2, repo.getUserLists(userId2.toString()).size)
        assertNull(newListNotInserted)

    }

    @Test
    fun testCreateUserListWithNonExistentUser() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))
        val invalidUserId: Id<User> = newId()
        assertFailsWith<NotFoundException> { repo.createUserList(UserCountriesList(invalidUserId, "list44", mutableSetOf("Country13", "Country14"))) }
    }

    @Test
    fun testDeleteWithCorrectValues() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))

        val createdListId = repo.createUserList(UserCountriesList(userId1, "list_to_delete", mutableSetOf()))
        assertNotNull(createdListId)

        val sizeBeforeDelete = repo.getUserLists(userId1.toString()).size
        val result = repo.delete(createdListId)

        assertNotNull(result)
        assertTrue(result)
        assertEquals(sizeBeforeDelete - 1, repo.getUserLists(userId1.toString()).size)
        assertNull(repo.getUserList(userId1.toString()))
    }

    @Test
    fun testDeleteNonExistentList() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))
        val listId: Id<UserCountriesList> = newId()
        assertFailsWith<NotFoundException> { repo.delete(listId.toString()) }
    }

    @Test
    fun testDoUpdateNameAndCountries() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))

        val idCreated = repo.createUserList(UserCountriesList(userId1, "list_before_update", mutableSetOf("Country22", "Country23")))
                ?: throw Exception("not exception expected")
        val sizeBeforeUpdate = repo.getUserLists(userId1.toString()).size

        val response = repo.doUpdate(idCreated, "list_after_update", mutableSetOf("Country22", "Country23", "Country33"))
        assertEquals(idCreated, response)
        assertEquals(sizeBeforeUpdate, repo.getUserLists(userId1.toString()).size)

        val updated = repo.getUserList(idCreated)

        assertNotNull(updated)
        assertEquals(mutableSetOf("Country22", "Country23", "Country33"), updated.countries)
        assertEquals("list_after_update", updated.name)
        assertEquals(idCreated, updated._id.toString())

    }

    @Test
    fun testDoUpdateOnlyCountries() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))
        val listName = "list Name"

        val idCreated = repo.createUserList(UserCountriesList(userId1, listName, mutableSetOf("Country22", "Country23")))
                ?: throw Exception("not exception expected")
        val sizeBeforeUpdate = repo.getUserLists(userId1.toString()).size

        val response = repo.doUpdate(idCreated, listName, mutableSetOf("Country33", "Country77"))
        assertEquals(idCreated, response)

        assertEquals(sizeBeforeUpdate, repo.getUserLists(userId1.toString()).size)
        val updated = repo.getUserList(idCreated)

        assertNotNull(updated)
        assertEquals(mutableSetOf("Country33", "Country77"), updated.countries)
        assertEquals(listName, updated.name)
        assertEquals(idCreated, updated._id.toString())
    }

    @Test
    fun testDoUpdateOnlyName() {
        val repo = UserListsRepository(mongoDatabase, UsersRepository(mongoDatabase))
        val listName = "list Name"
        val listNameUpdated = "list Name Updated"

        val idCreated = repo.createUserList(UserCountriesList(userId1, listName, mutableSetOf("Country22", "Country23")))
                ?: throw Exception("not exception expected")
        val sizeBeforeUpdate = repo.getUserLists(userId1.toString()).size

        val response = repo.doUpdate(idCreated, listNameUpdated, mutableSetOf("Country22", "Country23"))
        assertEquals(idCreated, response)

        assertEquals(sizeBeforeUpdate, repo.getUserLists(userId1.toString()).size)
        val updated = repo.getUserList(idCreated)

        assertNotNull(updated)
        assertEquals(mutableSetOf("Country22", "Country23"), updated.countries)
        assertEquals(listNameUpdated, updated.name)
        assertEquals(idCreated, updated._id.toString())
    }

    companion object {

        @Container
        var mongoContainer = GenericContainer<Nothing>("mongo:3.6.18").apply { withExposedPorts(27017) }

        private lateinit var userCountryList1: UserCountriesList
        private lateinit var userCountryList2: UserCountriesList
        private lateinit var userCountryList3: UserCountriesList
        private lateinit var userCountryList4: UserCountriesList
        private lateinit var userCountryList5: UserCountriesList

        private val userId1: Id<User> = newId()
        private val userId2: Id<User> = newId()
        private val userId3: Id<User> = newId()

        private val listId1: Id<UserCountriesList> = newId()
        private val listId2: Id<UserCountriesList> = newId()
        private val listId3: Id<UserCountriesList> = newId()
        private val listId4: Id<UserCountriesList> = newId()
        private val listId5: Id<UserCountriesList> = newId()

        private lateinit var user1: User
        private lateinit var user2: User
        private lateinit var user3: User


        private lateinit var mongoDatabase: MongoDatabase

        @BeforeClass
        @JvmStatic
        fun before() {

            mongoContainer.start()

            user1 = User(userId1, "user1")
            user2 = User(userId2, "user2")
            user3 = User(userId3, "user3");

            userCountryList1 = UserCountriesList(listId1, userId1, "list1", mutableSetOf("Country1", "Country2", "Country3"), LocalDate.parse("2020-05-23"))
            userCountryList2 = UserCountriesList(listId2, userId1, "list2", mutableSetOf("Country4", "Country5", "Country6"), LocalDate.parse("2020-05-22"))
            userCountryList3 = UserCountriesList(listId3, userId2, "list3", mutableSetOf("Country7", "Country8", "Country9"), LocalDate.parse("2020-05-21"))
            userCountryList4 = UserCountriesList(listId4, userId2, "list4", mutableSetOf("Country10", "Country11", "Country12"), LocalDate.parse("2020-05-20"))
            userCountryList5 = UserCountriesList(listId5, userId3, "list5", mutableSetOf("Country123"), LocalDate.parse("2020-02-20"))

            mongoDatabase = KMongo.createClient("mongodb://${mongoContainer.containerIpAddress}:${mongoContainer.getMappedPort(27017)}").getDatabase("test")

            //TODO this is temporal. This must be replaced with correct creation on db when executing the code.
            val index = Document("name", 1).append("userId", 1)
            mongoDatabase.getCollection("userCountriesList").createIndex(index, IndexOptions().unique(true))
            mongoDatabase.getCollection<User>("users").insertMany(mutableListOf(user1, user2, user3))
            mongoDatabase.getCollection<UserCountriesList>().insertMany(mutableListOf(userCountryList1, userCountryList2, userCountryList3, userCountryList4, userCountryList5))
        }

        @AfterClass
        @JvmStatic
        fun after() {
            mongoDatabase.getCollection("userCountriesList").drop()
            mongoDatabase.getCollection("users").drop()
        }
    }
}