package com.utn.tacs

import com.github.kotlintelegrambot.entities.ParseMode
import org.junit.Test

class BotConstructorTests {
    @Test
    fun countriesCommandTest(){
        main()
    }

    @Test
    fun createBotTest(){
        val bot = createBot("any")

        sendMessages(bot, listOf(TelegramMessageWrapper(1, "Test", ParseMode.HTML, false,false,2,null)))

        sendTypingAction(bot, 1)

        createCommandHandler("command", LoginType.NotRequired) { _, _, _ -> emptyList() }

        createCallbackQueryHandler("data", LoginType.NotRequired) { _, _, _ -> emptyList() }
    }
}