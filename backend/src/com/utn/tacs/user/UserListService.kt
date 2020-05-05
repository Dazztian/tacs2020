package com.utn.tacs.user

import com.utn.tacs.CountryData
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.getCountriesLatestFromApi
import com.utn.tacs.rest.UserListResponse
import java.lang.Exception

suspend fun getUserCountriesList(userId: Int, listName: String): UserListResponse {
    val user = User(userId, "Name" , "email" , "pass" , listOf(
        UserCountriesList("lista1",listOf("AR","AF","EG")),
        UserCountriesList("lista2",listOf("MC","MN"))
    ))

    val list = user.countriesLists.filter{ l -> l.name.equals(listName) }.first()
    return UserListResponse(listName, getCountriesLatestFromApi(list.countries).map { it.countryregion })
}

suspend fun getUserCountriesList(userId: Int): List<UserListResponse> {
    val user = User(userId, "Name" , "email" , "pass" , listOf(
        UserCountriesList("lista1",listOf("AR","AF","EG")),
        UserCountriesList("lista2",listOf("MC","MN"))
    ))
    val result = ArrayList<UserListResponse>()

    user.countriesLists.forEach{
        result.add(
            UserListResponse(it.name,getCountriesLatestFromApi(it.countries).map { it.countryregion } )
        )
    }

    return result;
}