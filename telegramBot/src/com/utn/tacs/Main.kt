package com.utn.tacs

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.utn.tacs.handlers.countryListCommands
import com.utn.tacs.handlers.startCommands
import java.io.File


//Map of last important messages separated by each telegram user Id
val lastImportantMessages = mutableMapOf<Long, PreviousMessageWrapper>()
lateinit var urlBase :String

fun main() {
    val f = File("APIKey.txt")
    if(!f.exists()){
        println("No API Key specified for the bot")
        return
    }
    val f2 = File("Base_Url.txt")
    if(!f2.exists()){
        println("No base url specified for the bot")
        return
    }
    urlBase = f2.readText(Charsets.UTF_8)

    val bot = createBot(f.readText(Charsets.UTF_8))
    bot.startPolling()
}

fun createBot(apiKey :String) :Bot = bot {
    token = apiKey

    startCommands(updater)
    countryListCommands(updater)
}