package com.utn.tacs

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.utn.tacs.handlers.countryListCommands
import com.utn.tacs.handlers.startCommands
import java.io.File


//Map of last important messages separated by each telegram user Id
val lastImportantMessages = mutableMapOf<Long, PreviousMessageWrapper>()

fun main() {
    val f = File("APIKey.txt")
    if(!f.exists()){
        println("No API Key specified for the bot")
        return
    }

    val bot = createBot(f.readText(Charsets.UTF_8))
    bot.startPolling()
}

fun createBot(apiKey :String) :Bot = bot {
    token = apiKey

    startCommands(updater)
    countryListCommands(updater)
}