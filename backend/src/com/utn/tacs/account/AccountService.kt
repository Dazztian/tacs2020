package com.utn.tacs.account

import com.utn.tacs.SignUpRequest
import com.utn.tacs.SignUpResponse
import com.utn.tacs.User
import com.utn.tacs.UserAccount
import com.utn.tacs.user.UsersRepository
import java.lang.Exception

class AccountService(private val usersRepository: UsersRepository, private val accountRepository: AccountRepository) {

    public fun validateToken(authorizationHeader: String) {

    }

    private fun encriptPassword(password: String): String {
        return password
    }

    private fun generateToken(user: User): String {
        return user.password ?: user.name
    }

    fun signUp(signUpRequest: SignUpRequest): SignUpResponse? {
        if (null != usersRepository.getUserByEmail(signUpRequest.email.trim().toLowerCase())) {
            return null
        }

        val user = usersRepository.createUser(
            User(signUpRequest.name.trim().toLowerCase(), signUpRequest.email.trim().toLowerCase(), signUpRequest.password, signUpRequest.country.trim().toLowerCase())
        ) ?: throw Exception("user not created")

        return SignUpResponse(user, accountRepository.createUserAccount(UserAccount(user._id, generateToken(user)))!!.token)
    }
}