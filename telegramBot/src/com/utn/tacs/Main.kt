package com.utn.tacs

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.utn.tacs.handlers.countryListCommands
import com.utn.tacs.handlers.startCommands
import java.io.File


//Map of last important messages separated by each telegram user Id
val lastImportantMessages = mutableMapOf<Long, PreviousMessageWrapper>()
var urlBase :String = "http://localhost:8083/"

fun main() {
    val bot = createBot()
    bot.startPolling()
}

fun createBot() :Bot = bot {
    token = this::class.java.getResource("/APIKey.txt").readText()
    if(token.isBlank()){
        throw Exception("No API Key specified for the bot")
    }

    urlBase = this::class.java.getResource("/Base_Url.txt").readText()
    if(urlBase.isBlank()){
        throw Exception("No base url specified for the bot")
    }

    startCommands(updater)
    countryListCommands(updater)
}