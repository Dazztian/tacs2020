package com.utn.tacs.account

import com.utn.tacs.*
import com.utn.tacs.exception.UnAuthorizedException
import com.utn.tacs.exception.UserAlreadyExistsException
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.user.UsersService
import io.ktor.features.NotFoundException
import java.lang.Exception
import java.security.MessageDigest
import java.math.BigInteger
import java.util.concurrent.TimeUnit

class AccountService(private val usersRepository: UsersRepository, private val accountRepository: AccountRepository, private val usersService: UsersService) {

    /**
     * Get one user by its token session or null if session doesn't exists
     *
     * @param token String
     * @return User?
     */
    public fun getUserByToken(token: String): User? {
        val userAccount = accountRepository.getUserAccount(token) ?: return null
        return usersRepository.getUserById(userAccount.userId)
    }

    /**
     * Generates a user token using the user email, password and name to return a Hash using SHA-256 algorithm
     *
     * @param user User
     * @return String
     */
    private fun generateToken(user: User): String {
        val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toString()
        return (String.format("%032x", BigInteger(1, MessageDigest.getInstance("SHA-256").digest((user.email + timestamp).toByteArray(Charsets.UTF_8))))
                + String.format("%032x", BigInteger(1, MessageDigest.getInstance("SHA-256").digest((user.password + timestamp).toByteArray(Charsets.UTF_8))))
                + String.format("%032x", BigInteger(1, MessageDigest.getInstance("SHA-256").digest((user.name + timestamp).toByteArray(Charsets.UTF_8)))))
    }

    /**
     * Creates an user account and returns the access token
     *
     * @param loginRequest LoginRequest
     * @return LoginResponse
     *
     * @throws NotFoundException
     * @throws UnAuthorizedException
     */
    fun logIn(loginRequest: LoginRequest): LoginResponse {
        val user: User = usersRepository.getUserByEmailAndPass(loginRequest.email, loginRequest.password) ?: throw NotFoundException("User does not exists or password is invalid")
        val userAccount = accountRepository.getUserAccount(user)
            ?: accountRepository.createUserAccount(UserAccount(user._id, generateToken(user))) ?: throw UnAuthorizedException("invalid password")
        return LoginResponse(user, userAccount.token)
    }

    /**
     * Log out one User if the session has not expired yet
     *
     * @param logOutRequest LogOutRequest
     *
     * @throws Exception
     */
    fun logOut(logOutRequest: LogOutRequest) {
        val userAccount = accountRepository.getUserAccount(logOutRequest.token) ?: throw Exception("session Expired")
        accountRepository.removeUserAccount(userAccount)
    }

    /**
     * Creates an user and user account, return the access token
     *
     * @param signUpRequest SignUpRequest
     * @return LoginResponse
     *
     * @throws UserAlreadyExistsException
     * @throws Exception
     */
    fun signUp(signUpRequest: SignUpRequest): LoginResponse {
        signUpRequest.isAdmin = false;
        val user = usersService.createUser(signUpRequest)
        return LoginResponse(user, accountRepository.createUserAccount(UserAccount(user._id, generateToken(user)))!!.token)
    }
}