package com.utn.tacs.Telegram

import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.utn.tacs.TelegramSession
import com.utn.tacs.TelegramUser
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.telegram.DB_MONGO_TELEGRAM_SESSION_COLLECTION
import com.utn.tacs.telegram.TelegramRepository
import io.ktor.features.NotFoundException
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.litote.kmongo.*
import kotlin.test.*

class TelegramRepositoryTest{
    private val database = mockk<MongoDatabase>()
    private val userListsRepository = mockk<UserListsRepository>()
    private val mongoCollection = mockk<MongoCollection<TelegramSession>>()

    private val userId: Id<User> = newId()
    private val telegramId = "telegramId"
    private val username = "user name"
    private val password = "password"
    private val telegramRepository = TelegramRepository(database, userListsRepository)
    private val telegramUser = TelegramUser(telegramId, username, password)
    private val telegramSession = TelegramSession(userId, telegramId)
    private val user = User(userId, "pepe")

    @Test
    fun testGetTelegramSession_byTelegramUser_ok() {
        coEvery { database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION) } returns mongoCollection
        coEvery { mongoCollection.findOne(TelegramSession::telegramId eq telegramUser.telegramId) } returns telegramSession

        assertEquals(telegramSession, telegramRepository.getTelegramSession(telegramUser))
    }

    @Test
    fun testGetTelegramSession_byTelegramId_ok() {
        coEvery { database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION) } returns mongoCollection
        coEvery { mongoCollection.findOne(TelegramSession::telegramId eq telegramId) } returns telegramSession

        assertEquals(telegramSession, telegramRepository.getTelegramSession(telegramId))
    }

    @Test
    fun testGetTelegramSession_byTelegramId_null() {
        coEvery { database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION) } returns mongoCollection
        coEvery { mongoCollection.findOne(TelegramSession::telegramId eq telegramId) } returns null

        assertNull(telegramRepository.getTelegramSession(telegramId))
    }

    @Test
    fun testCreateNewTelegramSession_nullUser_returnsNull() {
        assertNull(telegramRepository.createNewTelegramSession(null, telegramUser))
    }

    @Test
    fun testCreateNewTelegramSession_ok() {
        coEvery { database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION) } returns mongoCollection
        coEvery { mongoCollection.insertOne(any()) } returns InsertOneResult.acknowledged(null)

        val newTelegramSession = telegramRepository.createNewTelegramSession(user, telegramUser)
        assertEquals(userId, newTelegramSession!!.userId)
        assertEquals(telegramId, newTelegramSession!!.telegramId)
    }

    @Test
    fun testCreateNewTelegramSession_ExceptionThrown() {
        coEvery { database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION) } throws MongoException(1,"")
        assertThrows<MongoException> { telegramRepository.createNewTelegramSession(user, telegramUser) }
    }

    @Test
    fun testDeleteTelegramSession() {
        coEvery { database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION) } returns mongoCollection
        coEvery { mongoCollection.deleteOne(TelegramSession::telegramId eq telegramUser.telegramId.toString()) } returns DeleteResult.acknowledged(1)

        telegramRepository.deleteTelegramSession(telegramUser.telegramId.toString())

        verify { mongoCollection.deleteOne(TelegramSession::telegramId eq telegramUser.telegramId.toString()) }
    }

    @Test
    fun testDeleteTelegramSession_exeptionThrown() {
        coEvery { database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION) } throws MongoException(1,"")
        assertThrows<MongoException> { telegramRepository.deleteTelegramSession(telegramUser.telegramId.toString()) }
    }

    @Test
    fun testAuthenticated_telegramSessionNull_false() {
        val listId = "listId"
        coEvery { database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION) } returns mongoCollection
        coEvery { mongoCollection.findOne(TelegramSession::telegramId eq telegramId) } returns null

        assertFalse(telegramRepository.authenticated(telegramId, listId))
    }

    @Test
    fun testAuthenticated_userListNull_false() {
        val listId = "listId"
        coEvery { database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION) } returns mongoCollection
        coEvery { mongoCollection.findOne(TelegramSession::telegramId eq telegramId) } returns telegramSession
        coEvery { userListsRepository.getUserList(listId) } returns null

        assertFalse(telegramRepository.authenticated(telegramId, listId))
    }

    @Test
    fun testAuthenticated_differentUserId_false() {
        val listId = "listId"
        val userCountriesList = mockk<UserCountriesList>()

        coEvery { userCountriesList.userId } returns newId<User>()
        coEvery { database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION) } returns mongoCollection
        coEvery { mongoCollection.findOne(TelegramSession::telegramId eq telegramId) } returns telegramSession
        coEvery { userListsRepository.getUserList(listId) } returns userCountriesList

        assertFalse(telegramRepository.authenticated(telegramId, listId))
    }

    @Test
    fun testAuthenticated_true() {
        val listId = "listId"
        val userCountriesList = mockk<UserCountriesList>()

        coEvery { userCountriesList.userId } returns userId
        coEvery { database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION) } returns mongoCollection
        coEvery { mongoCollection.findOne(TelegramSession::telegramId eq telegramId) } returns telegramSession
        coEvery { userListsRepository.getUserList(listId) } returns userCountriesList

        assertTrue(telegramRepository.authenticated(telegramId, listId))
    }
}