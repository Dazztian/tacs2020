package com.utn.tacs

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.gson.gson
import java.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Location (
    val name: String,
    val lat: Double,
    val lon: Double
)

data class CountryCode (
    val iso2: String,
    val iso3: String
)

data class CountryData (
    val countryregion: String,
    val lastupdate: Date,
    val location: Location,
    val countrycode: CountryCode,
    val confirmed: Int,
    val deaths: Int,
    val recovered: Int
)

suspend fun getExternalData(): Array<CountryData> {
    
    val client = HttpClient(CIO)
    val jsonData: String? = client.get("https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/latest?onlyCountries=true")
    val gson = Gson()
    val arrayDePaisesType = object : TypeToken<Array<CountryData>>() {}.type
    var paises: Array<CountryData> = gson.fromJson(jsonData, arrayDePaisesType)
    return paises
}

suspend fun getNearestCountries(lat: Double, lon: Double): List<String> {
    val data: Array<CountryData> = getExternalData()
    return data.map{it.countryregion}
}

suspend fun getAllCountries(): List<String> {
    val data: Array<CountryData> = getExternalData()
    return data.map{it.countryregion}
}
