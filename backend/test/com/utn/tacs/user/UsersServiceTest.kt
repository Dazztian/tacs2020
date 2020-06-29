package com.utn.tacs.user

import com.utn.tacs.CountriesNamesResponse
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.UserCountriesListResponse
import com.utn.tacs.lists.UserListsRepository
import io.ktor.features.NotFoundException
import io.mockk.coEvery
import io.mockk.mockk
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
}