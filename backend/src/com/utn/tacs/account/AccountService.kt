package com.utn.tacs.account

import com.utn.tacs.*
import com.utn.tacs.exception.UnAuthorizedException
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.user.UsersService
import io.ktor.features.NotFoundException
import java.lang.Exception
import java.security.MessageDigest
import java.math.BigInteger

class AccountService(private val usersRepository: UsersRepository, private val accountRepository: AccountRepository, private val usersService: UsersService) {

    public fun getUserByToken(token: String): User? {
        val userAccount = accountRepository.getUserAccount(token) ?: return null
        return usersRepository.getUserById(userAccount.userId)
    }

    private fun encriptPassword(password: String): String {
        return password
    }

    private fun generateToken(user: User): String {
        return (String.format("%032x", BigInteger(1, MessageDigest.getInstance("SHA-256").digest(user.email.toByteArray(Charsets.UTF_8))))
                + String.format("%032x", BigInteger(1, MessageDigest.getInstance("SHA-256").digest(user.password.toByteArray(Charsets.UTF_8))))
                + String.format("%032x", BigInteger(1, MessageDigest.getInstance("SHA-256").digest(user.name.toByteArray(Charsets.UTF_8)))))
    }

    fun logIn(loginRequest: LoginRequest): LoginResponse {
        val user: User = usersRepository.getUserByEmailAndPass(loginRequest.email, loginRequest.password) ?: throw NotFoundException("User does not exists or password is invalid")
        val userAccount = accountRepository.getUserAccount(user)
            ?: accountRepository.createUserAccount(UserAccount(user._id, generateToken(user))) ?: throw UnAuthorizedException("invalid password")
        return LoginResponse(user, userAccount.token)
    }

    fun logOut(logOutRequest: LogOutRequest) {
        val userAccount = accountRepository.getUserAccount(logOutRequest.token) ?: throw Exception("session Expired")
        accountRepository.removeUserAccount(userAccount)
    }

    fun signUp(signUpRequest: SignUpRequest): LoginResponse {
        signUpRequest.isAdmin = false;
        val user = usersService.createUser(signUpRequest)
        return LoginResponse(user, accountRepository.createUserAccount(UserAccount(user._id, generateToken(user)))!!.token)
    }
}