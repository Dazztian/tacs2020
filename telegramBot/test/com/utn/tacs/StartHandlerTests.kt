package com.utn.tacs

import com.github.kotlintelegrambot.Bot
import com.utn.tacs.handlers.*
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class StartHandlerTests {
    private val chatId :Long = 123
    private val firstName :String = "FirstName"

    @Before
    fun beforeAll() {
        mockkStatic(RequestManager::class)
        mockkObject(RequestManager)

        every { RequestManager.healthCheck() } returns true
        every { RequestManager.isLoggedIn(chatId.toString()) } returns true

        lastImportantMessages.clear()
    }

    @Test
    fun startCommandTest(){
        assertEquals(listOf(TelegramMessageWrapper(
            chatId = chatId,
            text = loginText(firstName),
            replyMarkup = startButtons()
        )), startCommand(123, firstName)
        )


        every { RequestManager.isLoggedIn(chatId.toString()) } returns false


        assertEquals(listOf(TelegramMessageWrapper(
            chatId = chatId,
            text = startText)),
            startCommand(123, firstName)
        )
    }

    @Test
    fun validateLoginTypeTest(){
        assertEquals(true, validateLoginType(createBot(), chatId, LoginType.NotRequired))
        assertEquals(true, validateLoginType(createBot(), chatId, LoginType.LoggedIn))
        assertEquals(false, validateLoginType(createBot(), chatId, LoginType.NotLoggedIn))


        every { RequestManager.healthCheck() } returns false
        assertEquals(false, validateLoginType(createBot(), chatId, LoginType.NotRequired))
    }

    @Test
    fun helpCommandTest(){
        assertEquals(listOf(TelegramMessageWrapper(chatId, helpText)), helpCommand(chatId))
    }

    @Test
    fun loginCommandTest(){
        assertEquals(listOf(TelegramMessageWrapper(chatId, LoginHelpText)), loginCommand(chatId, firstName, emptyList()))

        every { RequestManager.login("username", "pass", chatId.toString()) } returns true
        assertEquals(listOf(TelegramMessageWrapper(chatId, loginText(firstName), replyMarkup = startButtons())), loginCommand(chatId, firstName, listOf("username", "pass")))

        every { RequestManager.login("username", "pass", chatId.toString()) } returns false

        assertEquals(listOf(TelegramMessageWrapper(chatId, badLogoutText)), loginCommand(chatId, firstName, listOf("username", "pass")))
    }

    @Test
    fun logoutCommandTest(){
        every { RequestManager.logout(chatId.toString()) } returns true

        assertEquals(listOf(TelegramMessageWrapper(chatId, startText)), logoutCommand(chatId))

        every { RequestManager.logout(chatId.toString()) } returns false

        assertEquals(listOf(TelegramMessageWrapper(chatId, errorText)), logoutCommand(chatId))
    }
}