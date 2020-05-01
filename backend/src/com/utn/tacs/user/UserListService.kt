package com.utn.tacs.user

import com.utn.tacs.CountryData
import com.utn.tacs.User
import com.utn.tacs.getCountriesLatest

suspend fun getUserCountriesList(userId: Int): List<CountryData> {
    val user = User(userId, listOf("AR","AF","EG"))
    return getCountriesLatest(user.countries)
}