package com.utn.tacs

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.utn.tacs.utils.isDistanceLowerThan
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.util.*

data class Location(
        val name: String,
        val lat: Double,
        val lng: Double
)

data class CountryCode(
        val iso2: String,
        val iso3: String
)

data class CountryData(
        val countryregion: String,
        val lastupdate: Date,
        val location: Location,
        val countrycode: CountryCode,
        val confirmed: Int,
        val deaths: Int,
        val recovered: Int
)

val gson: Gson = GsonBuilder().setPrettyPrinting().create()
const val apiEntryPoint = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/"
const val onlyCountries = "onlyCountries=true"
const val maxDistance = 2000.0


suspend fun getCountriesLatest(): Array<CountryData> {
    return getCountriesLatest("")
}

suspend fun getCountriesLatest(queryParams: String): Array<CountryData> {
    return HttpClient().use { client ->
        val jsonData: String? = client.get(apiEntryPoint + "latest?" + onlyCountries + queryParams)
        val countryArray = object : TypeToken<Array<CountryData>>() {}.type
        gson.fromJson(jsonData, countryArray)
    }
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
    val data: Array<CountryData> = getCountriesLatest()
    return data.filter { countryData -> isDistanceLowerThan(lat, lon, countryData.location.lat, countryData.location.lng, maxDistance) }.map { it.countryregion }
}

suspend fun getAllCountries(): List<String> {
    val data: Array<CountryData> = getCountriesLatest()
    return data.map { it.countryregion }
}
