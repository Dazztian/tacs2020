package com.utn.tacs.rest

data class UserListResponse(
    val listName: String,
    val countries: List<String>
)