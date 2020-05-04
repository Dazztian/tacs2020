package com.utn.tacs.countries

import com.google.gson.reflect.TypeToken
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.utn.tacs.*
import org.bson.Document

suspend fun getCountriesFromDatabase(): List<CountryData> {
    val result = ArrayList<CountryData>()
    val countryDataType = object : TypeToken<CountryData>() {}.type

    val mongoClient = MongoClient("172.17.0.2", 27017)
    val db = mongoClient.getDatabase("tacs")
    val collection: MongoCollection<Document> = db.getCollection("countries")
    collection?.find()?.toList()?.forEach{
        result.add(
            gson.fromJson(it.toJson(), countryDataType)
        )
    }

    if (result.size.equals(0)) {
        val documents = ArrayList<Document>()
        getCountriesLatest().forEach{
            val a = it.toString()
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
