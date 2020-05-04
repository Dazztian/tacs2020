package com.utn.tacs.user

import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import org.bson.Document

fun getUserFromDatabase(unNombre: String): String {

    var mongoClient = MongoClient("127.0.0.1", 27017)
    val db = mongoClient.getDatabase("testDB")
    val users: MongoCollection<Document> = db.getCollection("user")
    val user = users.find(Document("name", unNombre)).first()
    return user.toJson()
}


fun createUser(unUser: User): String{

    var mongoClient = MongoClient("localhost", 27017)
    try {

        val db = mongoClient.getDatabase("testDB")
        val tbl = db.getCollection("user")

        val document = Document()


        document["name"] = unUser.name
        document["email"] = unUser.email
        document["password"] = unUser.password

        tbl.insertOne(document)

    } catch (e: MongoException) {
        println("\n\nflaco hay un error\n\n")
        e.printStackTrace()
    } finally {
        mongoClient!!.close()
    }

    return getUserFromDatabase(unUser.name)
}
