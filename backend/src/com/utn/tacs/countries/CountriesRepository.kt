package com.utn.tacs.countries

import com.mongodb.client.MongoCollection
import com.utn.tacs.*
import com.utn.tacs.utils.MongoClientGenerator
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection


const val DB_MONGO_COUNTRIES_COLLECTION = "countries"
val db = MongoClientGenerator.getDataBase()

suspend fun getCountriesFromDatabase(): List<Country> {
    val collection: MongoCollection<Country> = db.getCollection<Country>(DB_MONGO_COUNTRIES_COLLECTION)

    val countries = collection.find()
    if (countries.none()) {
        collection.insertMany(getCountriesLatestFromApi())
    }

    return collection.find().toList()
}

suspend fun getCountryFromDatabase(iso2: String): Country {
    val collection: MongoCollection<Country> = db.getCollection<Country>(DB_MONGO_COUNTRIES_COLLECTION)
    return collection.findOne(Country::countrycode / CountryCode::iso2 eq iso2) ?: getCountryLatestByIsoCodeFromApi(iso2)
}