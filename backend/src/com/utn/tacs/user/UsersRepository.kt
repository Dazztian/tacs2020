package com.utn.tacs.user

import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId
import org.litote.kmongo.util.idValue
import java.lang.Exception

const val DB_MONGO_USERS_COLLECTION = "users"

class UsersRepository(private val database: MongoDatabase) {

    fun getUserByName(name: String): User? {
        return database.getCollection<User>().findOne(User::name eq name)
    }

    fun getUserById(id: Id<User>): User? {
        return database.getCollection<User>().findOneById(id)
    }

    fun getUserById(id: String): User? {
        return getUserById(id.toId())
    }

    fun getUserByEmailAndPass(email: String, password: String): User? {
        return database.getCollection<User>(DB_MONGO_USERS_COLLECTION).findOne(User::email eq email, User::password eq password)
    }

    fun getUserByEmail(email: String): User? {
        return database.getCollection<User>(DB_MONGO_USERS_COLLECTION).findOne(User::email eq email)
    }

    fun createUser(user: User): User? {
        try {
            database.getCollection<User>(DB_MONGO_USERS_COLLECTION).insertOne(user)
            val userId: Id<User> = user._id
            val aa = getUserById(userId)
            return getUserById(userId)
        } catch (e: MongoException) {
            throw e
        }
    }
}