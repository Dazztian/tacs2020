package com.utn.tacs.user

import com.google.gson.reflect.TypeToken
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.utn.tacs.User
import org.bson.Document
import com.utn.tacs.dao.*
import com.utn.tacs.*

const val DB_MONGO_USERS_COLLECTION = "users"
val userDataType = object : TypeToken<User>() {}.type

fun getUserFromDatabase(unNombre: String): User {
    val db = mongoClient.getDatabase(DB_MONGO_DATABASE_NAME)
    val collection = db.getCollection(DB_MONGO_USERS_COLLECTION)
    return gson.fromJson(
        collection.find((Document("name", unNombre))).first().toJson(),userDataType
    )
}


fun createUser(unUser: User): String{
    try {
        val db = mongoClient.getDatabase(DB_MONGO_DATABASE_NAME)
        val collection = db.getCollection(DB_MONGO_USERS_COLLECTION)
        collection.insertOne(Document.parse(user.toString()))
    } catch (e: MongoException) {
        println("\n\nflaco hay un error\n\n")
        e.printStackTrace()
    } finally {
        mongoClient!!.close()
    }

    return getUserFromDatabase(unUser.name)
}

