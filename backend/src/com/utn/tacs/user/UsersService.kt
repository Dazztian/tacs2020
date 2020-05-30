package com.utn.tacs.user

import com.utn.tacs.*
import com.utn.tacs.lists.*
import io.ktor.features.NotFoundException
import org.litote.kmongo.Id
import java.lang.Exception

class UsersService(private val usersRepository: UsersRepository, private val userListsRepository: UserListsRepository) {

    fun getUser(id: String): UserResponse? {
        val user = usersRepository.getUserById(id) ?: return null
        return UserResponse(
            user._id.toString(),
            user.name,
            user.email,
            user.creationDate ?: "",
            user.country!!,
            user.isAdmin,
            getUserLists(id)
        )
    }

    fun createUser(signUpRequest: SignUpRequest): User? {
        if (null != usersRepository.getUserByEmail(signUpRequest.email.trim().toLowerCase())) {
            return null
        }

        return usersRepository.createUser(
            User(signUpRequest.name, signUpRequest.email, signUpRequest.password, signUpRequest.country, signUpRequest.isAdmin ?: false)
        )
    }

    fun getUserLists(userId: String): List<UserCountriesListResponse> {
        usersRepository.getUserById(userId) ?: throw NotFoundException()
        val userLists: ArrayList<UserCountriesListResponse> = ArrayList()
        userListsRepository.getUserLists(userId).forEach { it -> userLists.add(
            UserCountriesListResponse(it._id.toString(), it.name, it.countries)
        )}
        return userLists
    }

    fun getUserList(userId: String, listId: String): UserCountriesListResponse {
        val userList = userListsRepository.getUserList(listId) ?: throw NotFoundException()
        return UserCountriesListResponse(
            userList._id.toString(),
            userList.name,
            userList.countries
        )
    }

    fun createUserList(userId: Id<User>, listName: String, countries: MutableSet<String>): UserCountriesListResponse {
        val id = userListsRepository.createUserList(UserCountriesList(userId, listName.trim(), countries)) ?: throw NotFoundException()
        return UserCountriesListResponse(
            id.toString(),
            listName,
            countries
        )
    }

    fun deleteUserList(userId: String, listId: String) {
        getUserList(userId, listId) ?: throw NotFoundException()
        if (! userListsRepository.delete(listId)) {
            throw Exception()
        }
    }

    fun updateUserList(userId: String, listId: String, request: UserCountriesListModificationRequest): UserCountriesListResponse {
        getUserList(userId, listId) ?: throw NotFoundException()
        val newListId = userListsRepository.doUpdate(listId, request.name, request.countries) ?: throw Exception()
        return UserCountriesListResponse(
            newListId,
            request.name,
            request.countries
        )
    }
}