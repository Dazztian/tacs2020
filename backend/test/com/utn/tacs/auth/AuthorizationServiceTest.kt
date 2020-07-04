package com.utn.tacs.auth

import com.utn.tacs.SignUpRequest
import com.utn.tacs.User
import com.utn.tacs.exception.UserAlreadyExistsException
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.user.UsersService
import io.ktor.features.NotFoundException
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows

class AuthorizationServiceTest {

    private val usersService = mockk<UsersService>()
    private val usersRepository = mockk<UsersRepository>()

    @Test
    fun testAuthWithCorrectCredentials() {
        val user = User("testName", "testEmail", "testPassword", "country", false)
        every { usersRepository.getUserByEmailAndPass(any(), any()) } returns user
        every { usersRepository.setUserLastLogin(any()) } returns user

        val service = AuthorizationService(usersRepository, usersService)
        assertEquals(user, service.auth("anyMail", "anyPassword"))
    }

    @Test
    fun testAuthWithIncorrectCredentials() {
        every { usersRepository.getUserByEmailAndPass(any(), any()) } throws NotFoundException("User does not exists or password is invalid")
        val service = AuthorizationService(usersRepository, usersService)
        assertThrows<NotFoundException> { service.auth("anyMail", "anyPassword") }
    }

    @Test
    fun testSignUpCorrect() {
        val user = User("testName", "testEmail", "testPassword", "country", false)
        every { usersService.createUser(any()) } returns user
        every { usersRepository.setUserLastLogin(any()) } returns user

        val service = AuthorizationService(usersRepository, usersService)
        assertEquals(user, service.signUp(SignUpRequest("request", "email", "pass", "country")))
    }

    @Test
    fun testFailedSignUp() {
        every { usersService.createUser(any()) } throws UserAlreadyExistsException()
        val service = AuthorizationService(usersRepository, usersService)
        assertThrows<UserAlreadyExistsException> { service.signUp(SignUpRequest("request", "email", "pass", "country")) }
    }
}