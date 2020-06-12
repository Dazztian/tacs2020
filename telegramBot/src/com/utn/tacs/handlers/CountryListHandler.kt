package com.utn.tacs.handlers

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.entities.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.updater.Updater
import com.utn.tacs.*

fun listButtonsNoMarkup(listId :String) = listOf(
                                                listOf(
                                                    InlineKeyboardButton(
                                                        text = "Refresh",
                                                        callbackData = "Check_list $listId"),
                                                    InlineKeyboardButton(
                                                        text = "Check last X days", //TODO:
                                                        callbackData = "Check_last_days_list")
                                                ),
                                                listOf(
                                                    InlineKeyboardButton(
                                                        text = "Add country", //TODO:
                                                        callbackData = "Add country $listId"),
                                                    InlineKeyboardButton(
                                                        text = "Remove country", //TODO:
                                                        callbackData = "Remove_country")
                                                ))
fun backToMyListsButtonNoMarkup() = listOf(
                                        listOf(
                                            InlineKeyboardButton(
                                                text = "Return",
                                                callbackData = "My_Lists")
                                        ))

const val textNoLists = "This user has no lists"

fun addCountryListCommands(updater : Updater){
    listOf(
        CommandHandler("countries") { bot, update->
            bot.sendMessage(
                chatId = update.message!!.chat.id,
                text = "Paises aceptados:\n" +
                        allCountriesNames()?.joinToString(separator = "\n").toString()
            )
        },

        createCallbackQueryHandler("My_Lists") { bot, update ->
            update.callbackQuery?.let {
                val chatId = it.message!!.chat.id

                try {
                    when(val listas = getCountryLists(update.callbackQuery?.message!!.chat.id.toString())){
                        null, emptyList<String>() -> {
                            bot.sendMessage(
                                    chatId = chatId,
                                    text = textNoLists,
                                    replyMarkup = returnButton())
                        }
                        else -> {
                            bot.sendMessage(
                                    chatId = chatId,
                                    text = "Select one of the lists:",
                                    replyMarkup = InlineKeyboardMarkup(
                                                        listas.map { countriesList -> listOf(countriesList.toButton()) } +
                                                                returnButtonNoMarkup()))
                        }
                    }

                }catch (e :Exception){
                    bot.sendMessage(
                            chatId = chatId,
                            text = e.toString())
                }
            }
        },
        createCallbackQueryHandler("Check_list") { bot, update, args ->
            update.callbackQuery?.let {
                val chatId = it.message!!.chat.id
                val listId = args[0]

                try {
                    when(val countriesList = buildTableArray(getListCountries(listId))){
                        emptyList<String>() -> {
                            bot.sendMessage(
                                    chatId = chatId,
                                    text = textNoLists,
                                    replyMarkup = returnButton())
                        }
                        else -> {
                            countriesList.forEach { row ->
                                bot.sendMessage(
                                    chatId = chatId,
                                    text = row,
                                    parseMode = ParseMode.HTML)
                            }

                            bot.sendMessage(
                                    chatId = chatId,
                                    text = "Commands:",
                                    replyMarkup = InlineKeyboardMarkup(listButtonsNoMarkup(listId) +
                                                        backToMyListsButtonNoMarkup()))
                        }
                    }

                } catch (e :Exception){
                    bot.sendMessage(
                            chatId = chatId,
                            text = e.toString())
                }
            }
        },

        //TODO: Consultar pais ultimos X dias
        createCommandHandler("check") { bot, update, args ->
            val pais = args[0]

            bot.sendMessage(
                chatId = update.message!!.chat.id,
                text = getCountryByName(pais)?.toTable()?.get(0) ?: "Error: Country not found\n" +
                                                                    "User /paises to check the name of the " +
                                                                    "country you are trying to look",
                parseMode = ParseMode.HTML
            )
        },
        createCallbackQueryHandler("Countries") { bot, update ->
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