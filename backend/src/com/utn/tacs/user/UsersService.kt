package com.utn.tacs.user

import com.utn.tacs.*
import com.utn.tacs.exception.UserAlreadyExistsException
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.utils.Encoder
import io.ktor.features.NotFoundException
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId

class UsersService(private val usersRepository: UsersRepository, private val userListsRepository: UserListsRepository) {

    /**
     * Get user by Id
     *
     * @param id String
     * @return User
     *
     * @throws NotFoundException
     */
    fun getUser(id: String): User {
        try {
            return usersRepository.getUserById(id) ?: throw NotFoundException()
        } catch (e: IllegalArgumentException) {
            throw NotFoundException()
        }
    }

    /**
     * Create user only if its doesn't exists
     *
     * @param signUpRequest SignUpRequest
     * @return User
     *
     * @throws UserAlreadyExistsException
     * @throws Exception
     */
    fun createUser(signUpRequest: SignUpRequest): User {
        if (null != usersRepository.getUserByEmail(signUpRequest.email.trim().toLowerCase())) {
            throw UserAlreadyExistsException()
        }
        return usersRepository.createUser(User(signUpRequest.name, signUpRequest.email, Encoder.encode(signUpRequest.password),
                signUpRequest.country, signUpRequest.isAdmin ?: false)) ?: throw Exception()
    }

    /**
     * Get all user's countries lists or empty List if the user does not have any
     *
     * @param userId String
     * @return List<UserCountriesListResponse>
     */
    fun getUserLists(userId: String): List<UserCountriesListResponse> {
        usersRepository.getUserById(userId) ?: throw NotFoundException()
        val userLists: ArrayList<UserCountriesListResponse> = ArrayList()
        userListsRepository.getUserLists(userId).forEach {
            userLists.add(
                    UserCountriesListResponse(it._id.toString(), it.name, it.countries)
            )
        }
        return userLists
    }

    /**
     * Get one user's countries list by list id. Only if the list owns to that user
     *
     * @param userId String
     * @param listId String
     * @return UserCountriesListResponse
     *
     * @throws NotFoundException
     */
    fun getUserList(userId: String, listId: String): UserCountriesListResponse {
        val userList = userListsRepository.getUserList(listId) ?: throw NotFoundException()
        if (!userList.userId.toString().equals(userId)) {
            throw NotFoundException()
        }
        return UserCountriesListResponse(
                userList._id.toString(),
                userList.name,
                userList.countries
        )
    }

    /**
     * Create a user list of countries
     *
     * @param userId String
     * @param listName String
     * @param countries MutableSet<String>
     * @return UserCountriesListResponse
     *
     * @throws NotFoundException
     */
    fun createUserList(userId: String, listName: String, countries: MutableSet<String>): UserCountriesListResponse {
        val id = userListsRepository.createUserList(UserCountriesList(ObjectId(userId).toId(), listName.trim(), countries))
                ?: throw NotFoundException()
        return UserCountriesListResponse(
                id,
                listName,
                countries
        )
    }

    /**
     * Deletes a user list of countries
     *
     * @param userId String
     * @param listId String
     *
     * @throws Exception
     * @throws NotFoundException
     */
    fun deleteUserList(userId: String, listId: String) {
        getUserList(userId, listId)
        if (!userListsRepository.delete(listId)) {
            throw Exception()
        }
    }

    /**
     * Update a user list of countries
     *
     * @param userId String
     * @param listId String
     * @param request UserCountriesListModificationRequest
     * @return UserCountriesListResponse
     *
     * @throws Exception
     * @throws NotFoundException
     */
    fun updateUserList(userId: String, listId: String, request: UserCountriesListModificationRequest): UserCountriesListResponse {
        getUserList(userId, listId)
        val newListId = userListsRepository.doUpdate(listId, request.name, request.countries) ?: throw Exception()
        return UserCountriesListResponse(
                newListId,
                request.name,
                request.countries
        )
    }

    /**
     * Delete a user by id
     * @param userId String
     * @throws NotFoundException
     * @throws Exception
     */
    fun deleteUser(userId: String) {
        usersRepository.delete(usersRepository.getUserById(userId) ?: throw NotFoundException())
    }

    /**
     * Delete a user by his email
     * @param userEmail String
     * @throws NotFoundException
     * @throws Exception
     */
    fun deleteUserByEmail(userEmail: String) {
        usersRepository.delete(usersRepository.getUserByEmail(userEmail) ?: throw NotFoundException())
    }
}