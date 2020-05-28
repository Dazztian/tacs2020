package com.utn.tacs.rest

import com.utn.tacs.User
import com.utn.tacs.account.AccountRepository
import com.utn.tacs.account.AccountService
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.utils.MongoClientGenerator

val accountService = AccountService(UsersRepository(MongoClientGenerator.getDataBase()), AccountRepository(MongoClientGenerator.getDataBase()))

fun getUserByAuthenticationHeader(authenticationHeader: String): User? {
    val token = authenticationHeader.split(" ").get(1)
    return accountService.getUserByToken(token)
}

