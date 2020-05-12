package com.utn.tacs.countries

import com.utn.tacs.utils.isDistanceLowerThan
import com.utn.tacs.*

class CountriesService(private val countriesRepository: CountriesRepository) {

    val maxDistance = 2000.0

    public suspend fun getAllCountries(): List<Country> {
        return countriesRepository.getCountries()
    }

    //We will consider that nearest countries are the one that are 3000km from latitude and long
    public suspend fun getNearestCountries(lat: Double, lon: Double): List<Country> {
        return countriesRepository.getCountries().filter { countryData -> isDistanceLowerThan(lat, lon, countryData.location.lat, countryData.location.lng, maxDistance) }
    }

    public suspend fun getCountryLatestByIsoCode(iso2: String): Country {
        try {
            return countriesRepository.getCountry(iso2)
        } catch (e: IndexOutOfBoundsException) {
            throw kotlin.IllegalArgumentException("There was no country with iso2 code $iso2")
        }
    }
}




