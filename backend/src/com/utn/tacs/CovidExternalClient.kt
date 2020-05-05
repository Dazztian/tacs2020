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

suspend fun getCountriesLatestFromApi(): List<CountryData> {
    return getCountriesLatestFromApi("")
}

suspend fun getCountriesLatestFromApi(queryParams: String): List<CountryData> {
    return HttpClient().use { client ->
        val jsonData: String? = client.get(apiEntryPoint + "latest?" + onlyCountries + queryParams)
        val countryArray = object : TypeToken<ArrayList<CountryData>>() {}.type
        gson.fromJson(jsonData, countryArray)
    }
}

suspend fun getCountriesLatestFromApi(isoCodes2: List<String>): List<CountryData> {
    val result = ArrayList<CountryData>()
    for (countryData in getCountriesLatestFromApi()) {
        try {
            if (isoCodes2.contains(countryData.countrycode.iso2)) {
                result.add(countryData)
            }
        } catch (e: NullPointerException) {
        }
    }
    return result
}

suspend fun getCountryLatestByIsoCodeFromApi(iso2: String): CountryData {
    try {
        return getCountriesLatestFromApi("&iso2=$iso2")[0]
    } catch (e: IndexOutOfBoundsException) {
        throw kotlin.IllegalArgumentException("There was no country with iso2 code $iso2")
    }
}

