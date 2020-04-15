package com.utn.tacs

import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import org.bson.Document


fun getCountriesFromDatabase(): String {

    //Only for testing mongo, this must be deleted in future iterations.
    var mongoClient: MongoClient? = null
    try {
        mongoClient = MongoClient("localhost", 27017)
        val db = mongoClient.getDatabase("testDB")
        val tbl = db.getCollection("country")

        val document = Document()

        document["name"] = "Argentina"
        document["continent"] = "America"

        tbl.insertOne(document)
    } catch (e: MongoException) {
        e.printStackTrace()
    } finally {
        mongoClient!!.close()
    }


    mongoClient = MongoClient("127.0.0.1", 27017)
    val db = mongoClient.getDatabase("testDB")
    val countries: MongoCollection<Document> = db.getCollection("country")
    val country = countries.find(Document("name", "Argentina")).first()
    return country.toJson();
}


