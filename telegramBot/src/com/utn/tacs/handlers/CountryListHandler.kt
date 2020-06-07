package com.utn.tacs.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandler
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

const val textNoLists = "This user has no lists"
const val newListSavedText = "New list saved successfully!"
const val createListText =  "To create a new list just send me the name of the list\n" +
                            "Optionally you can also write the names of the countries in the list by writing them in a new line each\n\n" +
                            "For example:\n\n" +
                            "My new list!\nArgentina\nChile\nBrazil"
const val addCountryText =  "Send me a list of the countries you want to add to this list\n" +
                            "Each country must be written on a new line and have the exact name from /countries\n\n" +
                            "Example:\nArgentina\nBrazil\nChile"

fun countryListCommands(updater : Updater){
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
                myLists(bot, it.message!!.chat.id)
            }
        },
        createCallbackQueryHandler("Check_list") { bot, update, args ->
            update.callbackQuery?.let {
                val chatId = it.message!!.chat.id
                val listId = args[0]

                showList(bot, listId, chatId)
            }
        },

        createCallbackQueryHandler("Add_country") { bot, update, args ->
            update.callbackQuery?.let {
                val chatId = it.message!!.chat.id
                val listId = args[0]

                lastImportantMessages[chatId] = MessageWrapper(MessageType.ADD_COUNTRY, listId)

                bot.sendMessage(
                    chatId = chatId,
                    text = addCountryText)
            }
        },
        createCallbackQueryHandler("Add_list") { bot, update ->
            update.callbackQuery?.let {
                val chatId = it.message!!.chat.id

                lastImportantMessages[chatId] = MessageWrapper(MessageType.NEW_LIST, "")

                bot.sendMessage(
                    chatId = chatId,
                    text = createListText)
            }
        },

        MessageHandler({ bot, update ->
            val userId = update.message!!.from!!.id
            val chatId = update.message!!.chat.id
            if(healthCheck() && isLoggedIn(userId.toString()) && lastImportantMessages.containsKey(userId)){
                val lastMessage = lastImportantMessages[userId]
                when(lastMessage!!.messageType){
                    MessageType.ADD_COUNTRY  -> {
                        val countriesList = update.message!!.text!!.trim().splitToSequence("\n").filter{ it.isNotEmpty() }.toSet()
                        val response = addCountries(chatId.toString(), lastMessage.countryListId, countriesList)

                        if (response == "Saved"){
                            lastImportantMessages.remove(userId)
                            showList(bot, lastMessage.countryListId, chatId)
                        }else{
                            bot.sendMessage(update.message!!.chat.id, text = response)
                        }
                    }
                    MessageType.NEW_LIST -> {
                        val countriesList = update.message!!.text!!.trim().splitToSequence("\n").filter{ it.isNotEmpty() }.toMutableList()
                        val listName = countriesList[0]
                        val response = newCountriesList(chatId.toString(), listName, countriesList.drop(1))

                        if (response == "Saved"){
                            lastImportantMessages.remove(userId)
                            bot.sendMessage(update.message!!.chat.id, text = newListSavedText)
                            myLists(bot, chatId)
                        }else{
                            bot.sendMessage(update.message!!.chat.id, text = response)
                        }
                    }
                    MessageType.LAST_X_DAYS -> {

                    }
                }
            }
        }, Filter.Text),

        CommandHandler("check", commandHandlerNoLoginRequired { bot, update, args ->
            bot.sendMessage(
                chatId = update.message!!.chat.id,
                parseMode = ParseMode.HTML,
                text = getCountryByName(args[0])?.toTable()?.get(0) ?: "Error: Country not found\n" +
                                                                    "User /paises to check the name of the " +
                                                                    "country you are trying to look"
            )
        })
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

fun showList(bot: Bot, listId: String, chatId :Long){
    try {
        when(val countriesList = buildTableArray(getListCountries(listId, chatId.toString()))){
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
                        replyMarkup = InlineKeyboardMarkup(listButtonsNoMarkup(listId)))
            }
        }

    } catch (e :Exception){
        bot.sendMessage(
                chatId = chatId,
                text = e.toString())
    }
}

fun myLists(bot: Bot, chatId: Long){
    try {
        when(val listas = getCountryLists(chatId.toString())){
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
                    replyMarkup = InlineKeyboardMarkup(listas.map { countriesList -> listOf(countriesList.toButton()) } +
                            newListButtonNoMarkup()))
            }
        }

    }catch (e :Exception){
        bot.sendMessage(
            chatId = chatId,
            text = e.toString())
    }
}