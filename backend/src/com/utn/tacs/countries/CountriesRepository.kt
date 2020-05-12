package com.utn.tacs.countries

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.utn.tacs.*
import com.utn.tacs.utils.MongoClientGenerator
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import com.typesafe.config.ConfigFactory
import java.util.concurrent.TimeUnit

const val DB_MONGO_COUNTRIES_COLLECTION = "countries"

class CountriesRepository(private val database: MongoDatabase) {
    private var cacheLastLoad: Long = 0
    private val cacheExpirationTime: Long = ConfigFactory.load().getLong("cache.countries")
    private val collection: MongoCollection<Country> = database.getCollection<Country>(DB_MONGO_COUNTRIES_COLLECTION)

    public suspend fun getCountries(): List<Country> {
        val countries = collection.find()
        if (countries.none() || cacheTimeExpired()) {
            collection.insertMany(getCountriesLatestFromApi())
        }
        return collection.find().toList()
    }

    public suspend fun getCountry(iso2: String): Country {
        return collection.findOne(Country::countrycode / CountryCode::iso2 eq iso2) ?: getCountryLatestByIsoCodeFromApi(iso2)
    }

    private fun cacheTimeExpired(): Boolean {
        val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        if (timestamp > (cacheLastLoad + cacheExpirationTime)) {
            collection.drop()
            cacheLastLoad = timestamp
            return true
        }
        return false
    }

}