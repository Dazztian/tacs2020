package com.utn.tacs

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

data class UserCountriesList(
    val name: String,
    val countries: List<String>
)

data class User(
   val id: Int,
   val countriesLists: List<UserCountriesList>
)