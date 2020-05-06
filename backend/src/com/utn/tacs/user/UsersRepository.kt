package com.utn.tacs.user

import com.google.gson.reflect.TypeToken
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.utn.tacs.User
import org.bson.Document
import com.utn.tacs.utils.MongoClientGenerator
import com.utn.tacs.*

const val DB_MONGO_USERS_COLLECTION = "users"
val userDataType = object : TypeToken<User>() {}.type
val db = MongoClientGenerator.getDataBase()

fun getUserFromDatabase(name: String): User? {
    val collection = db.getCollection(DB_MONGO_USERS_COLLECTION)
    val document: Document? = collection.find(Document("name", name)).first()
    return gson.fromJson(document?.toJson(), userDataType)
}

fun getUserFromDatabase(id: Int): User? {
    val collection = db.getCollection(DB_MONGO_USERS_COLLECTION)
    val document: Document? = collection.find(Document("id", id)).first()
    return gson.fromJson(document?.toJson(), userDataType)
}


fun createUser(user: User): User? {
    try {
        val collection = db.getCollection(DB_MONGO_USERS_COLLECTION)
        collection.insertOne(Document.parse(user.toString()))
    } catch (e: MongoException) {
        e.printStackTrace()
    } finally {
        //TODO agregarlo mongoClient!!.close()
    }

    return getUserFromDatabase(user.name)
}

