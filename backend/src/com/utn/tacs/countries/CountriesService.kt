package com.utn.tacs.countries

import com.utn.tacs.utils.isDistanceLowerThan
import com.utn.tacs.*

class CountriesService(private val countriesRepository: CountriesRepository) {

    private val maxDistance = 2000.0

    /**
     * Returns all countries covid data from cache repository or external api
     *
     * @return List<Country>
     */
    suspend fun getAllCountries(): List<Country> {
        return countriesRepository.getCountries()
    }

    /**
     * Returns all countries in the area of {{maxDistance}} area around
     *
     * @param lat Double
     * @param lon Double
     * @return List<Country>
     */
    suspend fun getNearestCountries(lat: Double, lon: Double): List<Country> {
        return countriesRepository.getCountries().filter { countryData -> isDistanceLowerThan(lat, lon, countryData.location.lat, countryData.location.lng, maxDistance) }
    }

    /**
     * Get one country covid data by iso2 code name
     * @sample AR
     * @sample EU
     *
     * @param iso2 String
     * @return Country
     */
    suspend fun getCountryLatestByIsoCode(iso2: String): Country {
        try {
            return countriesRepository.getCountry(iso2)
        } catch (e: IndexOutOfBoundsException) {
            throw kotlin.IllegalArgumentException("There was no country with iso2 code $iso2")
        }
    }

    /**
     * Get one country covid timeseries by iso2 code name separated by dates
     *
     * @param iso2 String
     * @return Country
     */
    suspend fun getCountryTimesSeries(iso2: String): Country {
        val country = getCountryLatestByIsoCode(iso2)
        country.timeseries = getCountryTimeSeriesFromApi("iso2=$iso2")
        return country
    }
}