package com.utn.tacs.rest

import com.utn.tacs.User
import com.utn.tacs.account.AccountRepository
import com.utn.tacs.account.AccountService
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.utils.MongoClientGenerator

val accountService = AccountService(UsersRepository(MongoClientGenerator.getDataBase()), AccountRepository(MongoClientGenerator.getDataBase()))

fun authorizeUser(authenticationHeader: String, userId: String): User {
    val user = accountService.getUserByToken(authenticationHeader.split(" ").get(1)) ?: throw Exception()
    if (!userId.equals(user._id.toString()) && !user.isAdmin) {
        throw Exception()
    }
    return user
}

fun authorizeUserAdmin(authenticationHeader: String): User {
    val user = accountService.getUserByToken(authenticationHeader.split(" ").get(1)) ?: throw Exception()
    if (!user.isAdmin) {
        throw Exception()
    }
    return user
}