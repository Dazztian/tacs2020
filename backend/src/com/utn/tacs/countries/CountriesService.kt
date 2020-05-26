package com.utn.tacs.countries

import com.utn.tacs.utils.isDistanceLowerThan
import com.utn.tacs.*

class CountriesService(private val countriesRepository: CountriesRepository) {

    private val maxDistance = 2000.0

    suspend fun getAllCountries(): List<Country> {
        return countriesRepository.getCountries()
    }

    //We will consider that nearest countries are the one that are 3000km from latitude and long
    suspend fun getNearestCountries(lat: Double, lon: Double): List<Country> {
        return countriesRepository.getCountries().filter { countryData -> isDistanceLowerThan(lat, lon, countryData.location.lat, countryData.location.lng, maxDistance) }
    }

    suspend fun getCountryLatestByIsoCode(iso2: String): Country {
        try {
            return countriesRepository.getCountry(iso2)
        } catch (e: IndexOutOfBoundsException) {
            throw kotlin.IllegalArgumentException("There was no country with iso2 code $iso2")
        }
    }

    suspend fun getCountryTimesSeries(iso2: String): Country {
        val country = getCountryLatestByIsoCode(iso2)
        country.timeseries = getCountryTimeSeriesFromApi("iso2=$iso2")
        return country
    }
}