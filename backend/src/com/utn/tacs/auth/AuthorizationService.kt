package com.utn.tacs.auth

import com.utn.tacs.SignUpRequest
import com.utn.tacs.User
import com.utn.tacs.exception.UnauthorizedException
import com.utn.tacs.exception.UserAlreadyExistsException
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.user.UsersService
import io.ktor.features.NotFoundException

class AuthorizationService(private val usersRepository: UsersRepository, private val usersService: UsersService) {

    /**
     * Creates an user account and returns the access token
     *
     * @param loginRequest LoginRequest
     * @return User
     *
     * @throws NotFoundException
     * @throws UnauthorizedException
     */
    fun auth(email: String, password: String): User {
        val user = usersRepository.getUserByEmailAndPass(email, password)
        return usersRepository.setUserLastLogin(user)
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
    fun signUp(signUpRequest: SignUpRequest): User {
        return usersRepository.setUserLastLogin(usersService.createUser(signUpRequest))
    }

}