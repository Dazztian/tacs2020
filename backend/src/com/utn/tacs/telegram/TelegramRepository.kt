package com.utn.tacs.telegram


import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.utn.tacs.TelegramSession
import com.utn.tacs.TelegramUser
import com.utn.tacs.User
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.id.toId

const val DB_MONGO_TELEGRAM_SESSION_COLLECTION = "TelegramSession"

class TelegramRepository(private val database: MongoDatabase) {
    fun getTelegramSession(telegramUser: TelegramUser): TelegramSession?{
        return try {
            database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION).findOne(TelegramSession::telegramId eq telegramUser.telegramId)
        } catch (e: MongoException) {
            throw e
        }
    }
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
    fun deleteTelegramSession(telegramUser: TelegramUser){
        try {
            database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION).deleteOne(TelegramSession::telegramId eq telegramUser.telegramId)
        } catch (e: MongoException){
            throw e
        }
    }
    fun getUserId(telegramId: String): TelegramSession? =
            database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION)
                    .findOne(TelegramSession::telegramId eq telegramId)
}