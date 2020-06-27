package com.utn.tacs.handlers

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
                                                        text = "Check last X days",
                                                        callbackData = "Check_days_list $listId")
                                                ),
                                                listOf(
                                                    InlineKeyboardButton(
                                                        text = "Add country",
                                                        callbackData = "Add_country $listId"),
                                                    InlineKeyboardButton(
                                                        text = "Return",
                                                        callbackData = "My_Lists")
                                                ))
fun newListButtonsNoMarkup() = listOf(
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
        createCommandHandler("countries", LoginType.NotRequired) { _, update->
            countriesCommand(update.message!!.chat.id)
        },

        createCallbackQueryHandler("My_Lists", LoginType.LoggedIn) { _, update ->
            myListsCommand(update.callbackQuery!!.message!!.chat.id)
        },
        createCallbackQueryHandler("Check_list", LoginType.LoggedIn) { _, update, args ->
            showList(args[0], update.callbackQuery!!.message!!.chat.id)
        },
        createCallbackQueryHandler("Check_days_list", LoginType.LoggedIn) { _, update, args ->
            checkLastNDays(args[0], update.callbackQuery!!.message!!.chat.id)
        },

        createCallbackQueryHandler("Add_country", LoginType.LoggedIn) { _, update, args ->
            addCountryCommand(update.callbackQuery!!.message!!.chat.id, args[0])
        },
        createCallbackQueryHandler("Add_list", LoginType.LoggedIn) { _, update ->
            addListCommand(update.callbackQuery!!.message!!.chat.id)
        },

        createMessageHandler(Filter.Text) { _, update ->
            messageCommand(update.message!!.from!!.id, update.message!!.chat.id, update.message!!.text!!)
        },

        createCommandHandler("check", LoginType.NotRequired) { _, update, args ->
            checkCommand(update.message!!.chat.id, args)
        }
    ).forEach{updater.dispatcher.addHandler(it)}
}

fun countriesCommand(chatId: Long) :responseMessages =
    listOf(TelegramMessageWrapper(chatId, acceptedCountriesText + RequestManager.allCountriesNames()
            .mapNotNull { it.name }.sortedWith(String.CASE_INSENSITIVE_ORDER)
            .joinToString(separator = "\n")))

fun myListsCommand(chatId: Long) :responseMessages{
    return when(val countriesLists = RequestManager.getCountryLists(chatId.toString())){
        emptyList<String>() -> listOf(TelegramMessageWrapper(chatId, textNoLists, replyMarkup = InlineKeyboardMarkup(newListButtonsNoMarkup())))
        else -> listOf(TelegramMessageWrapper(
                chatId, myListsText,
                replyMarkup = InlineKeyboardMarkup(countriesLists.map { countriesList -> listOf(countriesList.toButton()) } +
                        newListButtonsNoMarkup())))
    }
}
fun showList(listId: String, chatId :Long) :responseMessages{
    return when(val countriesList = buildTableArray(RequestManager.getListCountries(listId, chatId.toString()))){
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

fun addCountryCommand(chatId: Long, listId: String) :responseMessages{
    lastImportantMessages[chatId] = PreviousMessageWrapper(MessageType.ADD_COUNTRY, listId)

    return listOf(TelegramMessageWrapper(chatId, addCountryText))
}
fun addListCommand(chatId: Long) :responseMessages{
    lastImportantMessages[chatId] = PreviousMessageWrapper(MessageType.NEW_LIST, "")

    return listOf(TelegramMessageWrapper(chatId, createListText))
}
fun checkLastNDays(listId: String, chatId :Long) :responseMessages{
    lastImportantMessages[chatId] = PreviousMessageWrapper(MessageType.LAST_X_DAYS, listId)
    return listOf(TelegramMessageWrapper(chatId, textCheckLastNDays))
}

fun messageCommand(userId :Long, chatId :Long, text :String) :responseMessages{
    if(RequestManager.healthCheck() && RequestManager.isLoggedIn(userId.toString()) && lastImportantMessages.containsKey(userId)){
        val lastMessage = lastImportantMessages[userId]

        return when(lastMessage!!.messageType){
            MessageType.ADD_COUNTRY  -> {
                val countriesList = text.trim().splitToSequence("\n").filter{ it.isNotEmpty() }.toSet()
                val response = RequestManager.addCountries(chatId.toString(), lastMessage.countryListId, countriesList)

                if (response == "Saved"){
                    lastImportantMessages.remove(userId)
                    showList(lastMessage.countryListId, chatId)
                }else{
                    listOf(TelegramMessageWrapper(chatId, response))
                }
            }
            MessageType.NEW_LIST -> {
                val countriesList = text.trim().splitToSequence("\n").filter{ it.isNotEmpty() }.toMutableList()
                val listName = countriesList[0]
                val response = RequestManager.newCountriesList(chatId.toString(), listName, countriesList.drop(1))

                if (response.startsWith("OK")){
                    lastImportantMessages.remove(userId)
                    showList(response.substringAfter(" "), chatId)
                }else{
                    listOf(TelegramMessageWrapper(chatId, response))
                }
            }
            else -> {
                val days = text.trim()
                if (days.toLongOrNull() == null || days.toLong() < 1)
                    return listOf(TelegramMessageWrapper(chatId, textInvalidNumber))

                lastImportantMessages.remove(userId)
                val telegramMessageWrappers = buildTableTimeseries(RequestManager.getTimesesiesList(lastMessage.countryListId, days.toLong()))
                        .map { row -> TelegramMessageWrapper(chatId, row, parseMode = ParseMode.HTML) }.toMutableList()
                if(telegramMessageWrappers.isEmpty()){
                    telegramMessageWrappers
                }else{
                    telegramMessageWrappers[telegramMessageWrappers.lastIndex] =
                            telegramMessageWrappers[telegramMessageWrappers.lastIndex].copy(replyMarkup = InlineKeyboardMarkup(listButtonsNoMarkup(lastMessage.countryListId)))
                    telegramMessageWrappers
                }
            }
        }
    }

    return emptyList()
}

fun checkCommand(chatId :Long, args :List<String>) :responseMessages {
    return listOf(TelegramMessageWrapper(
            chatId = chatId,
            parseMode = ParseMode.HTML,
            text = RequestManager.getCountryByName(args[0])?.toTable()?.get(0) ?: countryNotFoundText))
}