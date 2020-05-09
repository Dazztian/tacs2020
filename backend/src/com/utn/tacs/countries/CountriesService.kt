package com.utn.tacs.countries

import com.utn.tacs.utils.isDistanceLowerThan
import com.utn.tacs.*

const val maxDistance = 2000.0

suspend fun getAllCountries(): List<Country> {
    return getCountriesFromDatabase()
}

//We will consider that nearest countries are the one that are 3000km from latitude and long
suspend fun getNearestCountries(lat: Double, lon: Double): List<Country> {
    return getCountriesFromDatabase().filter { countryData -> isDistanceLowerThan(lat, lon, countryData.location.lat, countryData.location.lng, maxDistance) }
}

suspend fun getCountryLatestByIsoCode(iso2: String): Country {
    try {
        return getCountryFromDatabase(iso2)
    } catch (e: IndexOutOfBoundsException) {
        throw kotlin.IllegalArgumentException("There was no country with iso2 code $iso2")
    }
}