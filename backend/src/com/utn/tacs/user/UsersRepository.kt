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
    val document: Document? = collection.find(Document("_id", id)).first()
    return gson.fromJson(document?.toJson(), userDataType)
}

fun createUser(user: User): User? {
    var id = 1
    try {
        val collection = db.getCollection(DB_MONGO_USERS_COLLECTION)
        val lastDocument: Document? = collection.find().sort( Document("_id", -1)).limit(1).first()
        val lastUser: User? = gson.fromJson(lastDocument?.toJson(), userDataType)
        if (null != lastUser) {
            id = lastUser.getId() + 1
        }
        collection.insertOne(
            Document.parse(User(id, user.name, user.email, user.password, user.countriesLists).toString())
        )
    } catch (e: MongoException) {
        e.printStackTrace()
    } finally {
        //TODO agregarlo mongoClient!!.close()
    }

    return getUserFromDatabase(id)
}

