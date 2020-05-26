package com.utn.tacs.account

import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import com.utn.tacs.UserAccount
import org.bson.types.ObjectId
import org.litote.kmongo.*

const val DB_MONGO_ACCOUNTS_COLLECTION = "accounts"

class AccountRepository(private val database: MongoDatabase) {

    fun getUserAccount(id: Id<UserAccount>): UserAccount? {
        return database.getCollection<UserAccount>(DB_MONGO_ACCOUNTS_COLLECTION).findOne(UserAccount::_id eq id)
    }

    fun getUserAccount(user: User): UserAccount? {
        return database.getCollection<UserAccount>(DB_MONGO_ACCOUNTS_COLLECTION).findOne(UserAccount::userId eq user._id)
    }

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