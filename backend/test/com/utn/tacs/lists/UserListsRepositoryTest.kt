package com.utn.tacs.lists


import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import org.bson.Document
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.litote.kmongo.*
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


@Testcontainers
class UserListsRepositoryTest {

    @Test
    fun testGetUserListByUserListId() {
        val repo = UserListsRepository(mongoDatabase)

        assertEquals(userCountryList1, repo.getUserList(userCountryList1._id))
        assertNull(repo.getUserList("NON_EXISTENT_ID".toId()))
    }

    @Test
    fun testGetUserListsByCreationDate() {
        val repo = UserListsRepository(mongoDatabase)

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
        val repo = UserListsRepository(mongoDatabase)
        assertEquals(4, repo.getCount())
    }

    @Test
    fun testGetAllThatContains(){
        val repo = UserListsRepository(mongoDatabase)

        val result = repo.getAllThatContains("Country1")
        assertEquals(1, result.size)
        assertTrue(result.contains(userCountryList1))

        val result2 = repo.getAllThatContains("NON_EXISTENT")
        assertEquals(0, result2.size)
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
        assertNull(repo.getUserList(userId1, "non_existent"))
    }

    @Test
    fun testCreateUserListCorrectValues() {

        val repo = UserListsRepository(mongoDatabase)

        //Before this user 1 has only 2 lists.
        val newListInserted = repo.createUserList(UserCountriesList(userId1, "list_name", mutableSetOf("Country13", "Country14")))
        assertEquals(3, repo.getUserLists(userId1).size)
        assertEquals(newListInserted, repo.getUserList(userId1, "list_name")?._id)

        //Should add even when countries are empty.
        val newListInserted2 = repo.createUserList(UserCountriesList(userId1, "list_name_empty_set", mutableSetOf()))
        assertEquals(4, repo.getUserLists(userId1).size)
        assertEquals(newListInserted2, repo.getUserList(userId1, "list_name_empty_set")?._id)


        //Before and after this user 2 must have only 2 lists
        val newListNotInserted = repo.createUserList(UserCountriesList(userId2, "list4", mutableSetOf("Country13", "Country14")))
        assertEquals(2, repo.getUserLists(userId2).size)
        assertNull(newListNotInserted)

        /*TODO
        //Should not add new list when the user does not exist
        val id2: Id<User> = newId()
        repo.createUserList(UserCountriesList(id2, "list44", mutableSetOf("Country13", "Country14")))
        assertEquals(0, repo.getUserLists(id2).size) */
    }

    @Test
    fun testCreateUserListWithRepeatedName() {

        val repo = UserListsRepository(mongoDatabase)

        //Before and after this user 2 must have only 2 lists
        val newListNotInserted = repo.createUserList(UserCountriesList(userId2, "list4", mutableSetOf("Country13", "Country14")))
        assertEquals(2, repo.getUserLists(userId2).size)
        assertNull(newListNotInserted)

    }

    @Test
    fun testCreateUserListWithNonExistentUser() {
        /*TODO
        //Should not add new list when the user does not exist
        val id2: Id<User> = newId()
        repo.createUserList(UserCountriesList(id2, "list44", mutableSetOf("Country13", "Country14")))
        assertEquals(0, repo.getUserLists(id2).size) */
    }

    @Test
    fun testDeleteWithCorrectValues() {
        val repo = UserListsRepository(mongoDatabase)

        repo.createUserList(UserCountriesList(userId1, "list_to_delete", mutableSetOf()))

        val sizeBeforeDelete = repo.getUserLists(userId1).size

        val result = repo.delete(userId1, "list_to_delete")

        assertNotNull(result)
        assertTrue(result)
        assertEquals(sizeBeforeDelete - 1, repo.getUserLists(userId1).size)
        assertNull(repo.getUserList(userId1, "list_to_delete"))
    }

    @Test
    fun testDeleteNonExistentList() {
        val repo = UserListsRepository(mongoDatabase)

        val result = repo.delete(userId2, "list_that_does_not_exist")
        assertNull(result)
    }

    @Test
    fun testDoUpdateNameAndCountries() {
        val repo = UserListsRepository(mongoDatabase)

        val idCreated = repo.createUserList(UserCountriesList(userId1, "list_before_update", mutableSetOf("Country22", "Country23")))

        val sizeBeforeUpdate = repo.getUserLists(userId1).size

        val response = repo.doUpdate(userId1, "list_before_update", "list_after_update", mutableSetOf("Country33"))

        assertEquals(idCreated, response)
        assertEquals(sizeBeforeUpdate, repo.getUserLists(userId1).size)

        val updated = repo.getUserList(userId1, "list_after_update")

        assertNotNull(updated)
        assertEquals(mutableSetOf("Country22", "Country23", "Country33"), updated.countries)
        assertEquals("list_after_update", updated.name)
        assertEquals(idCreated, updated._id)

    }

    @Test
    fun testDoUpdateNameButNotCountriesWhenTheyAreTheSame() {
        val repo = UserListsRepository(mongoDatabase)

        val idCreated = repo.createUserList(UserCountriesList(userId1, "list_before_update_2", mutableSetOf("Country22", "Country23")))

        val sizeBeforeUpdate = repo.getUserLists(userId1).size

        val response = repo.doUpdate(userId1, "list_before_update_2", "list_after_update_2", mutableSetOf("Country22", "Country23"))

        assertEquals(idCreated, response)
        assertEquals(sizeBeforeUpdate, repo.getUserLists(userId1).size)

        val updated = repo.getUserList(userId1, "list_after_update_2")

        assertNotNull(updated)
        assertEquals(mutableSetOf("Country22", "Country23"), updated.countries)
        assertEquals("list_after_update_2", updated.name)
        assertEquals(idCreated, updated._id)
    }

    @Test
    fun testDoUpdateOnlyName() {
        val repo = UserListsRepository(mongoDatabase)

        val idCreated = repo.createUserList(UserCountriesList(userId2, "list_to_update_name", mutableSetOf("Country22", "Country23")))

        val sizeBeforeUpdate = repo.getUserLists(userId2).size

        val response = repo.doUpdate(userId2, "list_to_update_name", "list_name_updated")

        assertEquals(idCreated, response)
        assertEquals(sizeBeforeUpdate, repo.getUserLists(userId2).size)

        val updated = repo.getUserList(userId2, "list_name_updated")

        assertNotNull(updated)
        assertEquals(mutableSetOf("Country22", "Country23"), updated.countries)
        assertEquals("list_name_updated", updated.name)
        assertEquals(idCreated, updated._id)
    }

    @Test
    fun testDoUpdateOnlyCountries() {
        val repo = UserListsRepository(mongoDatabase)

        val idCreated = repo.createUserList(UserCountriesList(userId2, "list_to_update_countries", mutableSetOf("Country22", "Country23")))

        val sizeBeforeUpdate = repo.getUserLists(userId2).size

        val response = repo.doUpdate(userId2, "list_to_update_countries", mutableSetOf("Country22", "Country23", "Country24", "Country25"))

        assertEquals(idCreated, response)
        assertEquals(sizeBeforeUpdate, repo.getUserLists(userId2).size)

        val updated = repo.getUserList(userId2, "list_to_update_countries")

        assertNotNull(updated)
        assertEquals(mutableSetOf("Country22", "Country23", "Country24", "Country25"), updated.countries)
        assertEquals("list_to_update_countries", updated.name)
        assertEquals(idCreated, updated._id)
    }

    @Test
    fun testUpdate() {
        val repo = UserListsRepository(mongoDatabase)

        val response1 = repo.update(userId1, "list1", "list_1_name_changed", mutableSetOf("Country22"))
        assertNotNull(response1)
        val updated1 = repo.getUserList(userId1, "list_1_name_changed")
        assertEquals(mutableSetOf("Country1", "Country2", "Country3", "Country22"), updated1?.countries)
        assertEquals("list_1_name_changed", updated1?.name)

        val response2 = repo.update(userId1, "list2", "list_2_name_changed", null)

        assertNotNull(response2)
        val updated2 = repo.getUserList(userId1, "list_2_name_changed")
        assertEquals(mutableSetOf("Country4", "Country5", "Country6"), updated2?.countries)
        assertEquals("list_2_name_changed", updated2?.name)

        val response3 = repo.update(userId2, "list3", null, mutableSetOf("Country22"))

        assertNotNull(response3)
        val updated3 = repo.getUserList(userId2, "list3")
        assertEquals(mutableSetOf("Country7", "Country8", "Country9", "Country22"), updated3?.countries)
        assertEquals("list3", updated3?.name)

        assertNull(repo.update(userId2, "list3", null, null))
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

        private lateinit var mongoDatabase: MongoDatabase

        @BeforeClass
        @JvmStatic
        fun before() {

            mongoContainer.start()

            userId1 = newId()
            userId2 = newId()

            userCountryList1 = UserCountriesList(userId1, "list1", mutableSetOf("Country1", "Country2", "Country3"), LocalDate.parse("2020-05-23"))

            userCountryList2 = UserCountriesList(userId1, "list2", mutableSetOf("Country4", "Country5", "Country6"), LocalDate.parse("2020-05-22"))

            userCountryList3 = UserCountriesList(userId2, "list3", mutableSetOf("Country7", "Country8", "Country9"), LocalDate.parse("2020-05-21"))

            userCountryList4 = UserCountriesList(userId2, "list4", mutableSetOf("Country10", "Country11", "Country12"), LocalDate.parse("2020-05-20"))


            mongoDatabase = KMongo.createClient("mongodb://${mongoContainer.containerIpAddress}:${mongoContainer.getMappedPort(27017)}").getDatabase("test")

            //TODO this is temporal. This must be replaced with correct creation on db when executing the code.
            val index = Document("name", 1).append("userId", 1)
            mongoDatabase.getCollection("userCountriesList").createIndex(index, IndexOptions().unique(true))

            mongoDatabase.getCollection<UserCountriesList>().insertMany(mutableListOf(userCountryList1, userCountryList2, userCountryList3, userCountryList4))
        }

        @AfterClass
        @JvmStatic
        fun after() {
            mongoDatabase.getCollection("userCountriesList").drop()
        }
    }
}