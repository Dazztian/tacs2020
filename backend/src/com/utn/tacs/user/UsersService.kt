package com.utn.tacs.user

import com.utn.tacs.User

class UsersService(private val usersRepository: UsersRepository) {
    public fun getUser(id: String): User? {
        return usersRepository.getUserById(id)
    }
}