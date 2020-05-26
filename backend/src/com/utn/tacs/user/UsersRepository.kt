package com.utn.tacs.user

import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId
import org.litote.kmongo.util.idValue

const val USERS_COLLECTION_NAME = "users"
class UsersRepository(private val database: MongoDatabase) {

    fun getUserByName(name: String): User? {
        return database.getCollection<User>(USERS_COLLECTION_NAME).findOne(User::name eq name)
    }

    fun getUserById(id: Id<User>): User? {
        return database.getCollection<User>(USERS_COLLECTION_NAME).findOneById(id)
    }

    fun getUserById(id: String): User? {
        return getUserById(ObjectId(id).toId())
    }

    fun createUser(user: User): Id<User>? {
        try {
            return (database.getCollection<User>(USERS_COLLECTION_NAME).insertOne(user).idValue as ObjectId?)?.toId()
        } catch (e: MongoException) {
            throw e
        }
    }
}