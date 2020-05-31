package com.utn.tacs.account

import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import com.utn.tacs.UserAccount
import org.bson.types.ObjectId
import org.litote.kmongo.*

const val DB_MONGO_ACCOUNTS_COLLECTION = "accounts"

class AccountRepository(private val database: MongoDatabase) {

    /**
     * Get User Account by id
     *
     * @param id Id<UserAccount>
     * @return UserAccount?
     */
    private fun getUserAccount(id: Id<UserAccount>): UserAccount? {
        return database.getCollection<UserAccount>(DB_MONGO_ACCOUNTS_COLLECTION).findOne(UserAccount::_id eq id)
    }

    /**
     * Get User account by User object
     *
     * @param user User
     * @return UserAccount?
     */
    fun getUserAccount(user: User): UserAccount? {
        return database.getCollection<UserAccount>(DB_MONGO_ACCOUNTS_COLLECTION).findOne(UserAccount::userId eq user._id)
    }

    /**
     * Get User Account by access token
     *
     * @param token String
     * @return UserAccount?
     */
    fun getUserAccount(token: String): UserAccount? {
        return database.getCollection<UserAccount>(DB_MONGO_ACCOUNTS_COLLECTION).findOne(UserAccount::token eq token)
    }

    /**
     * Get User Account by user Account Object
     *
     * @param userAccount UserAccount
     */
    fun removeUserAccount(userAccount: UserAccount) {
        database.getCollection<UserAccount>(DB_MONGO_ACCOUNTS_COLLECTION).deleteOne(UserAccount::_id eq userAccount._id)
    }

    /**
     * Creates an User Account
     *
     * @param userAccount UserAccount
     * @return UserAccount?
     *
     * @throws MongoException
     */
    fun createUserAccount(userAccount: UserAccount): UserAccount? {
        try {
            database.getCollection<UserAccount>(DB_MONGO_ACCOUNTS_COLLECTION).insertOne(userAccount)
            val accountId: Id<UserAccount> = userAccount._id
            return getUserAccount(accountId)
        } catch (e: MongoException) {
            throw e
        }
    }
}