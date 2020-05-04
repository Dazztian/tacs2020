package com.utn.tacs.countries

import com.google.gson.reflect.TypeToken
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.utn.tacs.*
import org.bson.Document
import com.utn.tacs.dao.*

const val DB_MONGO_COUNTRIES_COLLECTION = "countries"

suspend fun getCountriesFromDatabase(): List<CountryData> {
    val result = ArrayList<CountryData>()
    val countryDataType = object : TypeToken<CountryData>() {}.type

    val db = mongoClient.getDatabase("tacs")
    val collection: MongoCollection<Document> = db.getCollection(DB_MONGO_COUNTRIES_COLLECTION)
    collection.find().toList().forEach{
        result.add(
            gson.fromJson(it.toJson(), countryDataType)
        )
    }

    if (result.size.equals(0)) {
        val documents = ArrayList<Document>()
        getCountriesLatest().forEach{
            documents.add(
                Document.parse(it.toString())
            )
        }
        collection.insertMany(documents)
    } else {
        return result
    }

    return getCountriesFromDatabase()
}
