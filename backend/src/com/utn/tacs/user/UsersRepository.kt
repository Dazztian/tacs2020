package com.utn.tacs.user

import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId

const val USERS_COLLECTION_NAME = "users"
class UsersRepository(private val database: MongoDatabase) {

    /**
     * Get User by name
     *
     * @param name String
     * @return User?
     */
    fun getUserByName(name: String): User? {
        return database.getCollection<User>(USERS_COLLECTION_NAME).findOne(User::name eq name)
    }

    /**
     * Get user by Object id
     *
     * @param id Id<User>
     * @return User?
     */
    fun getUserById(id: Id<User>): User? {
        return database.getCollection<User>(USERS_COLLECTION_NAME).findOneById(id)
    }

    /**
     * Get user by id
     *
     * @param id String
     * @return User?
     */
    fun getUserById(id: String): User? {
        return getUserById(ObjectId(id).toId())
    }

    /**
     * Get user by email and pass
     *
     * @param email String
     * @param password String
     * @return User?
     */
    fun getUserByEmailAndPass(email: String, password: String): User? {
        return database.getCollection<User>(USERS_COLLECTION_NAME).findOne(User::email eq email, User::password eq password)
    }

    /**
     * Get user by email
     *
     * @param email String
     * @return User?
     */
    fun getUserByEmail(email: String): User? {
        return database.getCollection<User>(USERS_COLLECTION_NAME).findOne(User::email eq email)
    }

    /**
     * Create user
     *
     * @param user User
     * @return User?
     */
    fun createUser(user: User): User? {
        try {
            database.getCollection<User>(USERS_COLLECTION_NAME).insertOne(user)
            return getUserById(user._id)
        } catch (e: MongoException) {
            throw e
        }
    }
}