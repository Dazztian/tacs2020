package com.utn.tacs

import java.util.*

data class Location(
    val name: String,
    val lat: Double,
    val lng: Double
){
    override fun toString(): String{
        return "{ \"name\":\"$name\", \"lat\": $lat, \"lng\": $lng}"
}}

data class CountryCode(
    val iso2: String,
    val iso3: String
){
    override fun toString(): String{
        return "{ \"iso2\": \"$iso2\", \"iso3\": \"$iso3\"}"
    }
}

data class CountryData(
    val countryregion: String,
    val lastupdate: String,
    val location: Location,
    val countrycode: CountryCode,
    val confirmed: Int,
    val deaths: Int,
    val recovered: Int
){
    override fun toString(): String {
        return "{\"countryregion\": \"$countryregion\", \"lastupdate\" : \"$lastupdate \", \"location\" : $location, \"countrycode\":$countrycode, \"confirmed\": $confirmed, \"deaths\":$deaths, \"recovered\": $recovered}"
    }
}

data class UserCountriesList(
    val name: String,
    val countries: List<String>
)

data class User(
   val id: Int?,
   val name: String,
   val email: String?,
   val password: String?
   val countriesLists: List<UserCountriesList>?
)