package com.utn.tacs

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.json.JSONObject
import java.util.ArrayList

const val apiEntryPoint = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/"
const val onlyCountries = "onlyCountries=true"

/**
 * Get all countries covid data from external api
 *
 * @return List<Country>
 */
suspend fun getCountriesLatestFromApi(): List<Country> {
    return getCountriesLatestFromApi("")
}

/**
 * Get countries covid data from external api accepting query params
 *
 * @param queryParams String
 * @return List<Country>
 */
suspend fun getCountriesLatestFromApi(queryParams: String): List<Country> {
    return HttpClient().use { client ->
        val jsonData: String = client.get(apiEntryPoint + "latest?" + onlyCountries + "&" +  queryParams)
        jacksonObjectMapper().readValue(jsonData)
    }
}

/**
 * Get countries covid data timeseries from external api
 *
 * @param queryParams String
 * @return List<TimeSerie>
 */
suspend fun getCountryTimeSeriesFromApi(queryParams: String): List<TimeSeries> {
    return HttpClient().use { client ->
        val result = ArrayList<TimeSeries>()
        val jsonData: String = client.get(apiEntryPoint + "timeseries?" + onlyCountries + "&" + queryParams)
        val timeSeries = JSONObject(jsonData.substring(1, jsonData.length - 1)).get("timeseries") as JSONObject
        val iterator = timeSeries.keys()
        while (iterator.hasNext()) {
            val date = iterator.next() as String
            val timeserie =  timeSeries.get(date) as JSONObject
            result.add(TimeSeries(
                result.size,
                timeserie.get("confirmed") as Int,
                timeserie.get("deaths") as Int,
                timeserie.get("recovered") as Int,
                date
            ))
        }
        result
    }
}

/**
 * Get countries covid data from external api accepting filtering of a countries names list
 *
 * @param isoCodes2 List<String>
 * @return List<Country>
 */
suspend fun getCountriesLatestFromApi(isoCodes2: List<String>): List<Country> {
    val result = ArrayList<Country>()
    for (countryData in getCountriesLatestFromApi()) {
        try {
            if (isoCodes2.contains(countryData.countrycode?.iso2)) {
                result.add(countryData)
            }
        } catch (e: NullPointerException) {
        }
    }
    return result
}

/**
 * Get a country covid data from external api by Iso code2 name
 *
 * @param iso2 String
 * @return Country
 *
 * @throws IllegalArgumentException
 */
suspend fun getCountryLatestByIsoCodeFromApi(iso2: String): Country {
    try {
        return getCountriesLatestFromApi("iso2=$iso2")[0]
    } catch (e: IndexOutOfBoundsException) {
        throw kotlin.IllegalArgumentException("There was no country with iso2 code $iso2")
    }
}

