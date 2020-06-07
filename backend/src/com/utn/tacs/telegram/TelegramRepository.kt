package com.utn.tacs.telegram


import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.utn.tacs.TelegramSession
import com.utn.tacs.TelegramUser
import com.utn.tacs.User
import com.utn.tacs.userListsRepository
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.id.toId

const val DB_MONGO_TELEGRAM_SESSION_COLLECTION = "TelegramSession"

class TelegramRepository(private val database: MongoDatabase) {
    //Gets the telegram session associated to the telegram user
    fun getTelegramSession(telegramUser: TelegramUser): TelegramSession? =  database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION)
                                                                                    .findOne(TelegramSession::telegramId eq telegramUser.telegramId)

    //Gets the telegram session associated to the telegram user
    fun getTelegramSession(telegramId: String): TelegramSession? =  database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION)
                                                                            .findOne(TelegramSession::telegramId eq telegramId)

    //Creates a new telegram session (login)
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

    //Deletes the telegram session (logout)
    fun deleteTelegramSession(telegramUser: TelegramUser){
        try {
            database.getCollection<TelegramSession>(DB_MONGO_TELEGRAM_SESSION_COLLECTION).deleteOne(TelegramSession::telegramId eq telegramUser.telegramId)
        } catch (e: MongoException){
            throw e
        }
    }

    //Returns if a telegram user is logged in and the list belongs to him
    fun authenticated(telegramId :String, listId :String) :Boolean{
        return if (getTelegramSession(telegramId) == null)
            false
        else
            when(val userCountriesList = userListsRepository.getUserList(listId)){
                null -> false
                else -> getTelegramSession(telegramId)!!.userId == userCountriesList.userId
            }
    }
}