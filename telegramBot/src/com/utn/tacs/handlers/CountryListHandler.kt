package com.utn.tacs.handlers

import com.github.kotlintelegrambot.entities.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.updater.Updater
import com.utn.tacs.buildTableArray
import com.utn.tacs.allCountries
import com.utn.tacs.countryLists
import com.utn.tacs.createCallbackQueryHandler

fun addCountryListCommands(updater : Updater){
    listOf(
        createCallbackQueryHandler("MisListas") { bot, update ->
            update.callbackQuery?.let {
                val chatId = it.message!!.chat.id

                try {
                    buildTableArray(countryLists(update.callbackQuery?.message!!.chat.id.toString()))
                            .forEach {
                                row -> bot.sendMessage(
                                    chatId = chatId,
                                    text = row,
                                    parseMode= ParseMode.HTML)
                            }
                }catch (e :Exception){
                    bot.sendMessage(
                            chatId = chatId,
                            text = e.toString())
                }

                bot.sendMessage(
                    chatId = chatId,
                    text = "Volver a inicio",
                    replyMarkup = InlineKeyboardMarkup(
                        listOf(
                            listOf(
                                InlineKeyboardButton(text = "Volver", callbackData = "Inicio")
                            ))))
            }
        },
        createCallbackQueryHandler("Paises") { bot, update ->
            update.callbackQuery?.let {
                val chatId = it.message!!.chat.id

                val paises = allCountries()

                bot.sendMessage(
                    chatId = chatId,
                    text = "Volver a inicio",
                    replyMarkup = InlineKeyboardMarkup(
                        listOf(
                            listOf(
                                InlineKeyboardButton(text = "Volver", callbackData = "Inicio")
                            ))))
            }
        }
    ).forEach{updater.dispatcher.addHandler(it)}
}