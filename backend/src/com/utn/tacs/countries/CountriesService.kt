package com.utn.tacs.countries

import com.utn.tacs.utils.isDistanceLowerThan
import com.utn.tacs.*
import io.ktor.features.BadRequestException
import io.ktor.features.NotFoundException
import java.time.LocalDate

class CountriesService(private val countriesRepository: CountriesRepository) {

    private val maxDistance = 2000.0

    /**
     * Returns all countries covid data from cache repository or external api
     *
     * @return List<CountryResponse>
     */
    suspend fun getAllCountries(): List<CountryResponse> {
        val countries = ArrayList<CountryResponse>()
        countriesRepository.getCountries().forEach { countries.add(mapToResponse(it)) }
        return countries
    }

    /**
     * Returns all countries in the area of {{maxDistance}} area around
     *
     * @param lat Double
     * @param lon Double
     * @return List<CountryResponse>
     */
    suspend fun getNearestCountries(lat: Double, lon: Double): List<CountryResponse> {
        return getAllCountries().filter { countryData -> isDistanceLowerThan(lat, lon, countryData.location.lat, countryData.location.lng, maxDistance) }
    }

    /**
     * Get one country covid data by iso2 code name
     * @sample AR
     * @sample EU
     *
     * @param iso2 String
     * @return CountryResponse
     *
     * @throws NotFoundException
     */
    suspend fun getCountryLatestByIsoCode(iso2: String): CountryResponse {
        try {
            return mapToResponse(countriesRepository.getCountry(iso2))
        } catch (e: Exception) {
            throw NotFoundException("There was no country with iso2 code $iso2")
        }
    }

    /**
     * Get one country covid data by iso2 code name
     * @sample Argentina
     *
     * @param name String
     * @return CountryResponse
     *
     * @throws NotFoundException
     */
    suspend fun getCountryLatestByName(name: String): CountryResponse {
        try {
            return mapToResponse(countriesRepository.getCountryByName("^${name.toLowerCase().capitalize()}"))
        } catch (e: Exception) {
            throw NotFoundException("There was no country with name $name")
        }
    }

    /**
     * Get one country covid data by iso2 code name
     * @sample Argentina
     *
     * @param name String
     * @return CountryResponse
     */
    suspend fun getCountriesByName(names: List<String>): List<CountryResponse> {
        try {
            val countries = ArrayList<CountryResponse>()
            (if (names.isEmpty()) emptyList() else countriesRepository.getCountriesByName(names).toList()).forEach {
                it -> countries.add(mapToResponse(it))
            }
            return countries
        } catch (e: IndexOutOfBoundsException) {
            throw kotlin.IllegalArgumentException("There are no countries in that list")
        }
    }

    /**
     * Get one country covid timeseries by iso2 code name separated by dates
     *
     * @param iso2 String
     * @param fromDay Int?
     * @param toDay Int?
     * @param fromDate String?
     * @param toDate String?
     * @return CountryResponse
     *
     * @throws NotFoundException
     * @throws BadRequestException
     */
    suspend fun getCountryTimesSeries(
        iso2: String,
        fromDay: Int?,
        toDay: Int?,
        fromDate: String?,
        toDate: String?
    ): CountryResponse {
        val country = getCountryLatestByIsoCode(iso2)
        var timeseries = getCountryTimeSeriesFromApi("iso2=$iso2")
        if (null != fromDay) {
            timeseries = timeseries.dropWhile { it.number < fromDay }
        }
        if (null != toDay) {
            timeseries = timeseries.dropLastWhile { it.number > toDay }
        }
        if (null != fromDate && fromDate.isNotEmpty()) {
            timeseries = timeseries.dropWhile { LocalDate.of(
                it.date.split("/").get(2).toInt(),
                it.date.split("/").get(0).toInt(),
                it.date.split("/").get(1).toInt()
            ) < LocalDate.of(
                fromDate.split("/").get(2).toInt(),
                fromDate.split("/").get(0).toInt(),
                fromDate.split("/").get(1).toInt()
            ) }
        }
        if (null != toDate && toDate.isNotEmpty()) {
            timeseries = timeseries.dropLastWhile { LocalDate.of(
                it.date.split("/").get(2).toInt(),
                it.date.split("/").get(0).toInt(),
                it.date.split("/").get(1).toInt()
            ) > LocalDate.of(
                toDate.split("/").get(2).toInt(),
                toDate.split("/").get(0).toInt(),
                toDate.split("/").get(1).toInt()
            ) }
        }
        country.timeseries = timeseries
        return country
    }

    private fun mapToResponse(country: Country): CountryResponse {
        return CountryResponse(
            country.countryregion,
            country.lastupdate,
            country.location,
            country.countrycode,
            country.confirmed,
            country.deaths,
            country.recovered,
            country.timeseries ?: listOf()
        )
    }
}