package com.utn.tacs.user

import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId
import org.litote.kmongo.util.idValue


const val DB_MONGO_USERS_COLLECTION = "users"

class UsersRepository(private val database: MongoDatabase) {

    fun getUserByName(name: String): User? {
        return database.getCollection<User>(DB_MONGO_USERS_COLLECTION).findOne(User::name eq name)
    }

    fun getUserById(id: Id<User>): User? {
        return database.getCollection<User>(DB_MONGO_USERS_COLLECTION).findOne(User::_id eq id)
    }

    fun getUserById(id: String): User? {
        return database.getCollection<User>(DB_MONGO_USERS_COLLECTION).findOne(User::_id eq id.toId())
    }

    fun createUser(user: User): Id<User>? {
        try {
            return (database.getCollection<User>(DB_MONGO_USERS_COLLECTION).insertOne(user).idValue as ObjectId?)?.toId()
        } catch (e: MongoException) {
            throw e
        }
    }
}