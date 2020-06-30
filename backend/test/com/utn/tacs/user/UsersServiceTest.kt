package com.utn.tacs.user

import com.utn.tacs.CountriesNamesResponse
import com.utn.tacs.User
import com.utn.tacs.*
import com.utn.tacs.UserCountriesList
import com.utn.tacs.UserCountriesListResponse
import com.utn.tacs.lists.UserListsRepository
import io.ktor.features.NotFoundException
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.time.LocalDate
import kotlin.test.assertEquals

class UsersServiceTest {
    private val usersRepository = mockk<UsersRepository>()
    private val userListsRepository = mockk<UserListsRepository>()

    @Test
    fun testGetUser_ok() {
        val userId = "224"
        val user = User(newId(), "pepe")
        coEvery { usersRepository.getUserById(userId) } returns user

        val usersService = UsersService(usersRepository, userListsRepository)
        assertEquals(user, usersService.getUser(userId))
    }

    @Test
    fun testGetUser_nonExistingUser_illegalArgumentException() {
        val userId = "224"
        coEvery { usersRepository.getUserById(userId) } returns null

        val usersService = UsersService(usersRepository, userListsRepository)
        assertThrows<NotFoundException> {usersService.getUser(userId)}
    }

    @Test
    fun testGetUser_illegalArgumentException() {
        val userId = "224"
        coEvery { usersRepository.getUserById(userId) } throws IllegalArgumentException()

        val usersService = UsersService(usersRepository, userListsRepository)
        assertThrows<NotFoundException> {usersService.getUser(userId)}
    }

    @Test
    fun testGetUserLists_ok() {
        val userId: Id<User> = newId()
        val user = User(userId, "pepe")

        val userLists: List<UserCountriesList> = listOf(UserCountriesList(userId, "miLista", mutableSetOf("AR","BR"), LocalDate.now()))
        val userCountriesListResponse: List<UserCountriesListResponse> = userLists.map { UserCountriesListResponse(
            it._id.toString(),
            it.name,
            it.countries.map { countryName -> CountriesNamesResponse(countryName) }.toMutableSet(),
            it.creationDate.toString()
        )}

        coEvery { usersRepository.getUserById(userId.toString()) } returns user
        coEvery { userListsRepository.getUserLists(userId.toString()) } returns userLists

        val usersService = UsersService(usersRepository, userListsRepository)
        assertEquals(userCountriesListResponse, usersService.getUserLists(userId.toString()))
    }

    @Test
    fun testGetUserList_ok() {
        val userId: Id<User> = newId()
        val listId: Id<UserCountriesList> = newId()

        val userList = UserCountriesList(userId, "miLista", mutableSetOf("AR","BR"), LocalDate.now())
        val userCountriesListResponse = UserCountriesListResponse(
            userList._id.toString(),
            userList.name,
            userList.countries.map { countryName -> CountriesNamesResponse(countryName) }.toMutableSet(),
            userList.creationDate.toString()
        )

        coEvery { userListsRepository.getUserList(listId.toString()) } returns userList

        val usersService = UsersService(usersRepository, userListsRepository)
        assertEquals(userCountriesListResponse, usersService.getUserList(userId.toString(), listId.toString()))
    }

    @Test
    fun testGetUserList_userIsNotTheOwner_notFoundExceptionThrown() {
        val userId: Id<User> = newId()
        val listId: Id<UserCountriesList> = newId()

        val userList = UserCountriesList(userId, "miLista", mutableSetOf("AR","BR"), LocalDate.now())
        coEvery { userListsRepository.getUserList(listId.toString()) } returns userList

        val usersService = UsersService(usersRepository, userListsRepository)
        assertThrows<NotFoundException> {usersService.getUserList("other", listId.toString())}
    }

    @Test
    fun testCreateUserList_ok() {
        val userId: Id<User> = newId()
        val listId: Id<UserCountriesList> = newId()

        val listName = "top 5"
        val countries = mutableSetOf<String>("AR","BR")
        val countriesList = UserCountriesList(userId, listName, countries, LocalDate.now())
        val userCountriesListResponse = UserCountriesListResponse(
            listId.toString(),
            listName,
            countries.map { countryName -> CountriesNamesResponse(countryName) }.toMutableSet(),
            countriesList.creationDate.toString()
        )

        coEvery { userListsRepository.createUserList(any()) } returns listId.toString()

        val usersService = UsersService(usersRepository, userListsRepository)
        assertEquals(userCountriesListResponse, usersService.createUserList(userId.toString(), listName, countries))
    }

    @Test
    fun deleteUserList_ok() {
        val userId: Id<User> = newId()
        val listId: Id<UserCountriesList> = newId()

        val userList = UserCountriesList(userId, "miLista", mutableSetOf("AR","BR"), LocalDate.now())
        val userCountriesListResponse = UserCountriesListResponse(
            userList._id.toString(),
            userList.name,
            userList.countries.map { countryName -> CountriesNamesResponse(countryName) }.toMutableSet(),
            userList.creationDate.toString()
        )

        coEvery { userListsRepository.getUserList(listId.toString()) } returns userList
        coEvery { userListsRepository.delete(listId.toString()) } returns true

        val usersService = UsersService(usersRepository, userListsRepository)
        usersService.deleteUserList(userId.toString(), listId.toString())

        verify { userListsRepository.delete(listId.toString()) }
    }

    @Test
    fun deleteUserList_exception() {
        val userId: Id<User> = newId()
        val listId: Id<UserCountriesList> = newId()

        val userList = UserCountriesList(userId, "miLista", mutableSetOf("AR","BR"), LocalDate.now())
        val userCountriesListResponse = UserCountriesListResponse(
            userList._id.toString(),
            userList.name,
            userList.countries.map { countryName -> CountriesNamesResponse(countryName) }.toMutableSet(),
            userList.creationDate.toString()
        )

        coEvery { userListsRepository.getUserList(listId.toString()) } returns userList
        coEvery { userListsRepository.delete(listId.toString()) } returns false

        val usersService = UsersService(usersRepository, userListsRepository)
        assertThrows<Exception> {usersService.deleteUserList(userId.toString(), listId.toString())}
    }

    @Test
    fun testUpdateUserList_ok() {
        val userId: Id<User> = newId()
        val listId: Id<UserCountriesList> = newId()
        val request = UserCountriesListModificationRequest("nombre nuevo", mutableSetOf("AR","UY") )

        val userList = UserCountriesList(userId, "miLista", mutableSetOf("AR","BR"), LocalDate.now())
        val userListEdited = UserCountriesList(userId, "nombre nuevo", mutableSetOf("AR","UY"), LocalDate.now())

        val userCountriesListResponse = UserCountriesListResponse(
            listId.toString(),
            request.name,
            request.countries.map { countryName -> CountriesNamesResponse(countryName) }.toMutableSet(),
            userListEdited.creationDate.toString()
        )

        coEvery { userListsRepository.getUserList(listId.toString()) } returns userList
        coEvery { userListsRepository.getUserList(listId.toString()) } returns userListEdited
        coEvery { userListsRepository.doUpdate(listId.toString(), request.name, request.countries) } returns listId.toString()

        val usersService = UsersService(usersRepository, userListsRepository)
        assertEquals(userCountriesListResponse, usersService.updateUserList(userId.toString(), listId.toString(), request))
    }

    @Test
    fun testDeleteUser_ok() {
        val userId: Id<User> = newId()
        val user = User(userId, "pepe")

        coEvery { usersRepository.getUserById(userId.toString()) } returns user
        coEvery { usersRepository.delete(user) } returns Unit

        val usersService = UsersService(usersRepository, userListsRepository)
        usersService.deleteUser(userId.toString())

        verify { usersRepository.delete(user) }
    }

    @Test
    fun testDeleteUserByEmail_ok() {
        val userEmail = "test@gmail.com"
        val userId: Id<User> = newId()
        val user = User(userId, "pepe")

        coEvery { usersRepository.getUserByEmail(userEmail) } returns user
        coEvery { usersRepository.delete(user) } returns Unit

        val usersService = UsersService(usersRepository, userListsRepository)
        usersService.deleteUserByEmail(userEmail)

        verify { usersRepository.delete(user) }
    }
}

