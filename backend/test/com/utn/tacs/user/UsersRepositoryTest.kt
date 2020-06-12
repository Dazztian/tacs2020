package com.utn.tacs.user

import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import com.utn.tacs.utils.Encoder
import io.ktor.features.NotFoundException
import org.bson.types.ObjectId
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.assertThrows
import org.litote.kmongo.Id
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.litote.kmongo.id.toId
import org.litote.kmongo.newId
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class UsersRepositoryTest {

    @Test
    fun testGetUserById() {
        val repository = UsersRepository(mongoDatabase)
        assertEquals(user1, repository.getUserById(userId1))
        assertNull(repository.getUserById(ObjectId().toId()))

        assertEquals(user2, repository.getUserById(userId2.toString()))
        assertNull(repository.getUserById(ObjectId().toString()))
    }

    @Test
    fun testGetUserOrFail() {
        val repository = UsersRepository(mongoDatabase)
        assertEquals(user1, repository.getUserOrFail(userId1.toString()))

        assertThrows<NotFoundException> { repository.getUserOrFail(ObjectId().toString()) }
    }

    @Test
    fun testGetUserByEmailAndPass() {
        val repository = UsersRepository(mongoDatabase)
        assertEquals(user1, repository.getUserByEmailAndPass(user1.email, "password1"))
        assertThrows<NotFoundException> { repository.getUserByEmailAndPass(user1.email, "not_real_password") }
    }

    @Test
    fun testGetUserByEmail(){
        val repository = UsersRepository(mongoDatabase)
        assertEquals(user1, repository.getUserByEmail(user1.email))
        assertNull(repository.getUserByEmail("non_existent"))
    }

    @Test
    fun testCreateAndDeleteUser(){
        val repository = UsersRepository(mongoDatabase)
        assertEquals(user3, repository.createUser(user3))

        //This is because we generate a new admin user in the repository creation.
        assertEquals(4,  mongoDatabase.getCollection<User>("users").countDocuments())

        repository.delete(user3)
        assertEquals(3,  mongoDatabase.getCollection<User>("users").countDocuments())
    }


    companion object {

        @Container
        var mongoContainer = GenericContainer<Nothing>("mongo:3.6.18").apply { withExposedPorts(27017) }

        private val userId1: Id<User> = newId()
        private val userId2: Id<User> = newId()
        private val userId3: Id<User> = newId()

        private lateinit var user1: User
        private lateinit var user2: User
        private lateinit var user3: User


        private lateinit var mongoDatabase: MongoDatabase

        @BeforeClass
        @JvmStatic
        fun before() {

            mongoContainer.start()

            user1 = User(userId1, "user1", "email1", Encoder.encode("password1"))
            user2 = User(userId2, "user2", "email2", Encoder.encode("password2"))
            user3 = User(userId3, "user3", "email3", Encoder.encode("password3"));


            mongoDatabase = KMongo.createClient("mongodb://${mongoContainer.containerIpAddress}:${mongoContainer.getMappedPort(27017)}").getDatabase("test")

            //TODO this is temporal. This must be replaced with correct creation on db when executing the code.
            mongoDatabase.getCollection<User>("users").insertMany(mutableListOf(user1, user2))
        }

        @AfterClass
        @JvmStatic
        fun after() {
            mongoDatabase.getCollection("users").drop()
        }
    }

}