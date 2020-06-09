package com.utn.tacs.handlers

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.entities.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.extensions.filters.Filter
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
                                                        text = "Add country",
                                                        callbackData = "Add_country $listId"),
                                                    InlineKeyboardButton(
                                                        text = "Return",
                                                        callbackData = "My_Lists")
                                                ))
fun newListButtonNoMarkup() = listOf(
                                    listOf(
                                        InlineKeyboardButton(
                                            text = "Create New List",
                                            callbackData = "Add_list"),
                                        InlineKeyboardButton(
                                            text = "Return",
                                            callbackData = "Start")
                                        )
                                    )

fun countryListCommands(updater : Updater){
    listOf(
        CommandHandler("countries") { bot, update->
            bot.sendMessage(
                chatId = update.message!!.chat.id,
                text = "Paises aceptados:\n" +
                        allCountriesNames()?.joinToString(separator = "\n").toString()
            )
        },

        createCallbackQueryHandler("My_Lists") { _, update ->
            val chatId = update.callbackQuery!!.message!!.chat.id

            when(val listas = getCountryLists(chatId.toString())){
                null, emptyList<String>() -> listOf(TelegramMessageWrapper(chatId, textNoLists, replyMarkup = returnButton()))
                else -> listOf(TelegramMessageWrapper(
                                        chatId, myListsText,
                                        replyMarkup = InlineKeyboardMarkup(listas.map { countriesList -> listOf(countriesList.toButton()) } +
                                                            newListButtonNoMarkup())))
            }
        },
        createCallbackQueryHandler("Check_list") { _, update, args ->
            val chatId = update.callbackQuery!!.message!!.chat.id
            val listId = args[0]

            showList(listId, chatId)
        },

        createCallbackQueryHandler("Add_country") { _, update, args ->
            val chatId = update.callbackQuery!!.message!!.chat.id
            val listId = args[0]

            lastImportantMessages[chatId] = PreviousMessageWrapper(MessageType.ADD_COUNTRY, listId)

            listOf(TelegramMessageWrapper(chatId, addCountryText))
        },
        createCallbackQueryHandler("Add_list") { _, update ->
            val chatId = update.callbackQuery!!.message!!.chat.id

            lastImportantMessages[chatId] = PreviousMessageWrapper(MessageType.NEW_LIST, "")

            listOf(TelegramMessageWrapper(chatId, createListText))
        },
        createMessageHandler(Filter.Text) { _, update ->
            val userId = update.message!!.from!!.id
            val chatId = update.message!!.chat.id

            if(healthCheck() && isLoggedIn(userId.toString()) && lastImportantMessages.containsKey(userId)){
                val lastMessage = lastImportantMessages[userId]

                return@createMessageHandler when(lastMessage!!.messageType){
                    MessageType.ADD_COUNTRY  -> {
                        val countriesList = update.message!!.text!!.trim().splitToSequence("\n").filter{ it.isNotEmpty() }.toSet()
                        val response = addCountries(chatId.toString(), lastMessage.countryListId, countriesList)

                        if (response == "Saved"){
                            lastImportantMessages.remove(userId)
                            showList(lastMessage.countryListId, chatId)
                        }else{
                            listOf(TelegramMessageWrapper(chatId, response))
                        }
                    }
                    MessageType.NEW_LIST -> {
                        val countriesList = update.message!!.text!!.trim().splitToSequence("\n").filter{ it.isNotEmpty() }.toMutableList()
                        val listName = countriesList[0]
                        val response = newCountriesList(chatId.toString(), listName, countriesList.drop(1))

                        if (response.startsWith("OK")){
                            lastImportantMessages.remove(userId)
                            showList(response.substringAfter(" "), chatId)
                        }else{
                            listOf(TelegramMessageWrapper(chatId, response))
                        }
                    }
                    MessageType.LAST_X_DAYS -> {
                        emptyList()
                    }
                    else -> emptyList()
                }
            }

            emptyList()
        },

        createCommandHandlerNoLoginRequired("check") { _, update, args ->
            listOf(TelegramMessageWrapper(
                    chatId = update.message!!.chat.id,
                    parseMode = ParseMode.HTML,
                    text = getCountryByName(args[0])?.toTable()?.get(0) ?: countryNotFoundText ))
        }
        /*createCallbackQueryHandler("Countries") { bot, update ->
            update.callbackQuery?.let {
                val paises = allCountries()

                bot.sendMessage(
                    chatId = it.message!!.chat.id,
                    text = "Volver a inicio",
                    replyMarkup = InlineKeyboardMarkup(
                        listOf(
                            listOf(
                                InlineKeyboardButton(text = "Volver", callbackData = "Inicio")
                            ))))
            }
        }*/
    ).forEach{updater.dispatcher.addHandler(it)}
}

fun showList(listId: String, chatId :Long) :List<TelegramMessageWrapper>{
    return when(val countriesList = buildTableArray(getListCountries(listId, chatId.toString()))){
        emptyList<String>() -> listOf(TelegramMessageWrapper(chatId, textNoCountries, replyMarkup = InlineKeyboardMarkup(listButtonsNoMarkup(listId))))
        else -> {
            val telegramMessageWrappers = mutableListOf<TelegramMessageWrapper>()
            countriesList.forEach { row -> telegramMessageWrappers.add(TelegramMessageWrapper(chatId, row, parseMode = ParseMode.HTML)) }
            telegramMessageWrappers[telegramMessageWrappers.lastIndex] =
                    telegramMessageWrappers[telegramMessageWrappers.lastIndex].copy(replyMarkup = InlineKeyboardMarkup(listButtonsNoMarkup(listId)))
            telegramMessageWrappers
        }
    }
}