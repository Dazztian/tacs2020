package com.utn.tacs.user

import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.utn.tacs.User
import org.bson.Document

fun getUserFromDatabase(unNombre: String): String {

    var mongoClient = MongoClient("127.0.0.1", 27017)
    val db = mongoClient.getDatabase("testDB")
    val users: MongoCollection<Document> = db.getCollection("user")
    val user = users.find(Document("name", unNombre)).first()
    return user.toJson()
}

fun getUserById(id: Int): String {

    var mongoClient = MongoClient("127.0.0.1", 27017)
    val db = mongoClient.getDatabase("testDB")
    val users: MongoCollection<Document> = db.getCollection("user")
    val user = users.find(Document("id", id)).first()
    return user.toJson()
}


fun createUser(user: User): String{

    var mongoClient = MongoClient("localhost", 27017)
    try {

        val db = mongoClient.getDatabase("testDB")
        val tbl = db.getCollection("user")

        val document = Document()


        document["name"] = user.name
        document["email"] = user.email
        document["password"] = user.password

        tbl.insertOne(document)

    } catch (e: MongoException) {
        println("\n\nflaco hay un error\n\n")
        e.printStackTrace()
    } finally {
        mongoClient!!.close()
    }

    return getUserFromDatabase(user.name)
}
