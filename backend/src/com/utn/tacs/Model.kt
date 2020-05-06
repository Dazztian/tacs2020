package com.utn.tacs

import java.util.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class User(
        val id: Int?,
        val name: String,
        val email: String?,
        val password: String?,
        val countriesLists: List<UserCountriesList>?
) {
    override fun toString(): String {
        return "{\"id\":$id, \"name\":\"$name\", \"email\":\"$email\", \"password\":\"$password\", \"countriesLists\": $countriesLists}"
    }
}

@Serializable
data class Location(
        val name: String,
        val lat: Double,
        val lng: Double
) {
    override fun toString(): String {
        return "{ \"name\":\"$name\", \"lat\": $lat, \"lng\": $lng}"
    }
}

@Serializable
data class CountryCode(
        val iso2: String,
        val iso3: String
) {
    override fun toString(): String {
        return "{ \"iso2\": \"$iso2\", \"iso3\": \"$iso3\"}"
    }
}

@Serializable
data class CountryData(
        val countryregion: String,
        val lastupdate: String,
        val location: Location,
        val countrycode: CountryCode,
        val confirmed: Int,
        val deaths: Int,
        val recovered: Int
) {
    override fun toString(): String {
        return "{\"countryregion\": \"$countryregion\", \"lastupdate\" : \"$lastupdate \", \"location\" : $location, \"countrycode\":$countrycode, \"confirmed\": $confirmed, \"deaths\":$deaths, \"recovered\": $recovered}"
    }
}

@Serializable
data class UserCountriesList(
        val name: String,
        val countries: List<String>
) {
    override fun toString(): String {
        return "{\"name\": \"$name\", \"countries\": \"$countries\"}"
    }
}