package com.utn.tacs.user

import com.utn.tacs.CountryData
import com.utn.tacs.User
import com.utn.tacs.getCountriesLatest
import com.utn.tacs.UserCountriesList
import com.utn.tacs.rest.UserListResponse
import java.lang.Exception

suspend fun getUserCountriesList(userId: Int, listName: String): UserListResponse {
    val user = User(userId, listOf(
        UserCountriesList("lista1",listOf("AR","AF","EG")),
        UserCountriesList("lista2",listOf("MC","MN"))
    ))

    val list = user.countriesLists.filter{ l -> l.name.equals(listName) }.first()
    if (list == null) {
        throw Exception()
    }
    return UserListResponse(listName, getCountriesLatest(list.countries).map { it.countryregion })

}

suspend fun getUserCountriesList(userId: Int): List<UserListResponse> {
    val user = User(userId, listOf(
        UserCountriesList("lista1",listOf("AR","AF","EG")),
        UserCountriesList("lista2",listOf("MC","MN"))
    ))
    val result = ArrayList<UserListResponse>()

    user.countriesLists.forEach{
        result.add(
            UserListResponse(it.name,getCountriesLatest(it.countries).map { it.countryregion } )
        )
    }

    return result;
}