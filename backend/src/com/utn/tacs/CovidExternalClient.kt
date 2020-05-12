package com.utn.tacs


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.util.*

const val apiEntryPoint = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/"
const val onlyCountries = "onlyCountries=true"

suspend fun getCountriesLatestFromApi(): List<Country> {
    return getCountriesLatestFromApi("")
}

suspend fun getCountriesLatestFromApi(queryParams: String): List<Country> {
    return HttpClient().use { client ->
        //This needs to be done because the api returns as html text, not json type, so ktor can not automagically parse it.
        val jsonData: String = client.get(apiEntryPoint + "latest?" + onlyCountries + queryParams)
        jacksonObjectMapper().readValue(jsonData)
    }
}

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

suspend fun getCountryLatestByIsoCodeFromApi(iso2: String): Country {
    try {
        return getCountriesLatestFromApi("&iso2=$iso2")[0]
    } catch (e: IndexOutOfBoundsException) {
        throw kotlin.IllegalArgumentException("There was no country with iso2 code $iso2")
    }
}

