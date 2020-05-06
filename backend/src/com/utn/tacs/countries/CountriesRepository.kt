package com.utn.tacs.countries

import com.google.gson.reflect.TypeToken
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.utn.tacs.*
import org.bson.Document
import com.utn.tacs.utils.MongoClientGenerator

const val DB_MONGO_COUNTRIES_COLLECTION = "countries"
val countryDataType = object : TypeToken<CountryData>() {}.type
val db = MongoClientGenerator.getDataBase()

suspend fun getCountriesFromDatabase(): List<CountryData> {
    val result = ArrayList<CountryData>()
    val collection: MongoCollection<Document> = db.getCollection(DB_MONGO_COUNTRIES_COLLECTION)
    collection.find().toList().forEach{
        result.add(
            gson.fromJson(it.toJson(), countryDataType)
        )
    }

    if (result.size.equals(0)) {
        val documents = ArrayList<Document>()
        getCountriesLatestFromApi().forEach{
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

suspend fun getCountryFromDatabase(iso2: String): CountryData {
    val collection: MongoCollection<Document> = db.getCollection(DB_MONGO_COUNTRIES_COLLECTION)
    val document: Document? =  collection.find(Document("countrycode.iso2", iso2)).first()
    return gson.fromJson(document?.toJson(), countryDataType) ?: getCountryLatestByIsoCodeFromApi(iso2)
}