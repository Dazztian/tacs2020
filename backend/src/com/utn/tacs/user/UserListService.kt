package com.utn.tacs.user

import com.utn.tacs.CountryData
import com.utn.tacs.User
import com.utn.tacs.getCountriesLatest

 fun getUserCountriesList(userId: Int): List<CountryData> {
    val user = User(1, listOf("AR","EU","EG"))
    return getCountriesLatest(user.countries)
}