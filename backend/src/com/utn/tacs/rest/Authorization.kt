package com.utn.tacs.rest

import com.utn.tacs.User
import com.utn.tacs.account.AccountRepository
import com.utn.tacs.account.AccountService
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.utils.MongoClientGenerator
import com.utn.tacs.exception.UnAuthorizedException
import io.ktor.features.NotFoundException
import com.utn.tacs.accountService

fun authorizeUser(authenticationHeader: String, userId: String): User {
    val user = accountService.getUserByToken(getToken(authenticationHeader)) ?: throw NotFoundException()
    if (!userId.equals(user._id.toString()) && !user.isAdmin) {
        throw UnAuthorizedException()
    }
    return user
}

fun authorizeUser(authenticationHeader: String): User {
    return accountService.getUserByToken(getToken(authenticationHeader)) ?: throw NotFoundException()
}

fun authorizeUserAdmin(authenticationHeader: String): User {
    val user = accountService.getUserByToken(getToken(authenticationHeader)) ?: throw NotFoundException()
    if (!user.isAdmin) {
        throw UnAuthorizedException()
    }
    return user
}

fun getToken(authenticationHeader: String): String {
    try {
        if(authenticationHeader.split(" ").size != 2) {
            throw UnAuthorizedException()
        }
        if (!authenticationHeader.split(" ").get(0).toLowerCase().equals("bearer")) {
            throw UnAuthorizedException()
        }
        return authenticationHeader.split(" ").get(1)
    } catch (e: IndexOutOfBoundsException) {
        throw UnAuthorizedException()
    }
}