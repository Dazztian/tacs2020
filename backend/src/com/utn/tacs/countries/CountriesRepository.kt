package com.utn.tacs.countries

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.typesafe.config.ConfigFactory
import com.utn.tacs.Country
import com.utn.tacs.CountryCode
import com.utn.tacs.CovidExternalClient
import io.ktor.features.NotFoundException
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.*
import java.util.concurrent.TimeUnit

const val DB_MONGO_COUNTRIES_COLLECTION = "countries"

class CountriesRepository(database: MongoDatabase, private val externalClient: CovidExternalClient) {
    private var cacheLastLoad: Long = 0
    private val cacheExpirationTime: Long = ConfigFactory.load().getLong("cache.countries")
    private val collection: MongoCollection<Country> = database.getCollection<Country>(DB_MONGO_COUNTRIES_COLLECTION)


    init {
        //Initialize the cache
        runBlocking {
            collection.insertMany(externalClient.getCountriesLatestFromApi())
        }
    }

    /**
     * Get all countries covid data from cache, or if cache is expired or empty get countries from external api
     *
     * @return List<Country>
     */
    suspend fun getCountries(): List<Country> {
        val countries = collection.find()
        if (countries.none() || cacheTimeExpired()) {
            collection.insertMany(externalClient.getCountriesLatestFromApi())
        }
        return collection.find().toList()
    }


    /**
     * Get country from cache if its present, or from external client if that country is not in cache
     *
     * @param iso2 String
     * @return Country
     *
     * @throws IllegalArgumentException
     */
    suspend fun getCountry(iso2: String): Country {
        return collection.findOne(Country::countrycode / CountryCode::iso2 eq iso2)
                ?: externalClient.getCountryLatestByIsoCodeFromApi(iso2)
    }

    /**
     * Get country from cache if its present
     *
     * @param name String
     * @return Country
     *
     * @throws IllegalArgumentException
     */
    fun getCountryByName(name: String): Country {
        return collection.findOne(Country::countryregion regex name)
                ?: throw kotlin.IllegalArgumentException("There was no country with name $name")
    }

    /**
     * Get country from cache if its present
     *
     * @param names String
     * @return Country
     */
    fun getCountriesByName(names: List<String>): List<Country> {
        val result = collection.find(or(names.map { Country::countryregion eq it })).toList()
        if (result.isEmpty()) {
            throw NotFoundException("There are no countries in that list")
        }
        return result
    }

    /**
     * Get country from cache if its present
     *
     * @param names String
     * @return Country
     */
    fun getCountriesByIso(isos: List<String>): List<Country> {
        val result = collection.find(or(isos.map { Country::countrycode / CountryCode::iso2 eq it })).toList().distinctBy { it.countryregion }
        if (result.isEmpty()) {
            throw NotFoundException("There are no countries in that list")
        }
        return result
    }

    /**
     * Checks if countries covid data cache should be refreshed
     *
     * @return Boolean
     */
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