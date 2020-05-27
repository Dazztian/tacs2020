package com.utn.tacs.account

import com.utn.tacs.*
import com.utn.tacs.user.UsersRepository
import java.lang.Exception
import java.security.MessageDigest
import java.math.BigInteger

class AccountService(private val usersRepository: UsersRepository, private val accountRepository: AccountRepository) {

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

    fun logIn(loginRequest: LoginRequest): LoginResponse? {
        val user: User = usersRepository.getUserByEmailAndPass(loginRequest.email, loginRequest.password) ?: return null
        val userAccount = accountRepository.getUserAccount(user)
            ?: accountRepository.createUserAccount(UserAccount(user._id, generateToken(user))) ?: return null
        return LoginResponse(user, userAccount.token)
    }

    fun logOut(logOutRequest: LogOutRequest) {
        val userAccount = accountRepository.getUserAccount(logOutRequest.token) ?: throw Exception("session Expired")
        accountRepository.removeUserAccount(userAccount)
    }

    fun signUp(signUpRequest: SignUpRequest): LoginResponse? {
        if (null != usersRepository.getUserByEmail(signUpRequest.email.trim().toLowerCase())) {
            return null
        }

        val user = usersRepository.createUser(
            User(signUpRequest.name.trim().toLowerCase(), signUpRequest.email.trim().toLowerCase(), signUpRequest.password, signUpRequest.country.trim().toLowerCase())
        ) ?: throw Exception("user not created")

        return LoginResponse(user, accountRepository.createUserAccount(UserAccount(user._id, generateToken(user)))!!.token)
    }
}