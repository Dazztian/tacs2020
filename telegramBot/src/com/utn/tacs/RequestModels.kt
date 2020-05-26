package com.utn.tacs

data class CountriesList(
    val _id: String,
    val userId: String,
    val name: String,
    val countries: Set<String>,
    val creationDate: String
)