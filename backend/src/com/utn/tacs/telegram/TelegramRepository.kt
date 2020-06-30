package com.utn.tacs.telegram

import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.utn.tacs.TelegramSession
import com.utn.tacs.TelegramUser
import com.utn.tacs.User
import com.utn.tacs.lists.UserListsRepository
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

const val DB_MONGO_TELEGRAM_SESSION_COLLECTION = "TelegramSession"

class TelegramRepository(private val database: MongoDatabase, private val userListsRepository: UserListsRepository) {

    /**
     * Gets the telegram session associated to the telegram user
     *
     * @param telegramUser TelegramUser
     * @return TelegramSession?
     */
    fun getTelegramSession(telegramUser: TelegramUser): TelegramSession? {
        return database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION)
            .findOne(TelegramSession::telegramId eq telegramUser.telegramId)
    }

    /**
     * Gets the telegram session associated to the telegram user
     *
     * @param telegramId String
     * @return TelegramSession?
     */
    fun getTelegramSession(telegramId: String): TelegramSession? = database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION)
            .findOne(TelegramSession::telegramId eq telegramId)

    /**
     * Creates a new telegram session (login)
     *
     * @param user User?
     * @param telegramUser TelegramUser
     * @return TelegramSession?
     *
     * @throws MongoException
     */
    fun createNewTelegramSession(user: User?, telegramUser: TelegramUser): TelegramSession? {
        if (user == null)
            return null

        return try {
            val newTelegramSession = TelegramSession(user._id, telegramUser.telegramId)
            database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION).insertOne(newTelegramSession)
            newTelegramSession
        } catch (e: MongoException) {
            throw e
        }
    }

    /**
     * Deletes the telegram session (logout)
     *
     * @param telegramUserId String
     *
     * @throws MongoException
     */
    fun deleteTelegramSession(telegramUserId: String) {
        try {
            database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION).deleteOne(TelegramSession::telegramId eq telegramUserId)
        } catch (e: MongoException) {
            throw e
        }
    }

    /**
     * Returns if a telegram user is logged in and the list belongs to him
     *
     * @param telegramId String
     * @param listId String
     * @return Boolean
     */
    fun authenticated(telegramId: String, listId: String): Boolean {
        return if (getTelegramSession(telegramId) == null)
            false
        else
            when (val userCountriesList = userListsRepository.getUserList(listId)) {
                null -> false
                else -> getTelegramSession(telegramId)!!.userId.equals(userCountriesList.userId)
            }
    }
}