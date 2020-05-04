package com.utn.tacs

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.utn.tacs.utils.isDistanceLowerThan
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.lang.NullPointerException
import java.util.*

val gson: Gson = GsonBuilder().setPrettyPrinting().create()
const val apiEntryPoint = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/"
const val onlyCountries = "onlyCountries=true"
const val maxDistance = 2000.0

suspend fun getCountriesLatest(): List<CountryData> {
    return getCountriesLatest("")
}

suspend fun getCountriesLatest(queryParams: String): List<CountryData> {
    return HttpClient().use { client ->
        val jsonData: String? = client.get(apiEntryPoint + "latest?" + onlyCountries + queryParams)
        val countryArray = object : TypeToken<ArrayList<CountryData>>() {}.type
        gson.fromJson(jsonData, countryArray)
    }
}

suspend fun getCountriesLatest(isoCodes2: List<String>): List<CountryData> {
    val result = ArrayList<CountryData>();
    for (countryData in getCountriesLatest()) {
        try {
            if (isoCodes2.contains(countryData.countrycode.iso2)) {
                result.add(countryData)
            }
        } catch (e: NullPointerException) {
        }
    }
    return result
}

suspend fun getCountryLatestByIsoCode(iso2: String): CountryData {
    try {
        return getCountriesLatest("&iso2=$iso2")[0]
    } catch (e: IndexOutOfBoundsException) {
        throw kotlin.IllegalArgumentException("There was no country with iso2 code $iso2")
    }
}

//We will consider that nearest countries are the one that are 3000km from latitude and long
suspend fun getNearestCountries(lat: Double, lon: Double): List<String> {
    return getCountriesLatest().filter { countryData -> isDistanceLowerThan(lat, lon, countryData.location.lat, countryData.location.lng, maxDistance) }.map { it.countryregion }
}

suspend fun getAllCountries(): List<String> {
    return getCountriesLatest().map { it.countryregion }
}

