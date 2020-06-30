package com.utn.tacs.handlers

import com.github.kotlintelegrambot.entities.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.updater.Updater
import com.utn.tacs.*

fun startButtons() = InlineKeyboardMarkup(
                        listOf(
                            listOf(
                                InlineKeyboardButton("My Lists", callbackData = "My_Lists"),
                                InlineKeyboardButton("Help", callbackData = "Help")
                            ),
                            listOf(
                                InlineKeyboardButton(text = "Logout", callbackData = "Logout")
                            )))

fun startCommands(updater :Updater){
    listOf(
        createCommandHandler("start", LoginType.NotRequired) { _, update->
            startCommand(update.message!!.from!!.id, update.message!!.from?.firstName)
        },
        createCommandHandler("help", LoginType.NotRequired) { _, update->
            helpCommand(update.message!!.chat.id)
        },
        createCommandHandler("login", LoginType.NotLoggedIn) { _, update, args->
            loginCommand(update.message!!.chat.id, update.message!!.from?.firstName, args)
        },
        createCommandHandler("logout", LoginType.LoggedIn) { _, update->
            logoutCommand(update.message!!.chat.id)
        },

        createCallbackQueryHandler("Help", LoginType.NotRequired) { _, update ->
            helpCommand(update.callbackQuery!!.message!!.chat.id)
        },
        createCallbackQueryHandler("Start", LoginType.NotRequired) { _, update ->
            startCommand(update.callbackQuery!!.message!!.chat.id, update.callbackQuery!!.from.firstName)
        },
        createCallbackQueryHandler("Logout", LoginType.LoggedIn) { _, update ->
            logoutCommand(update.callbackQuery!!.message!!.chat.id)
        }
    ).forEach{updater.dispatcher.addHandler(it)}
}

fun startCommand(chatId :Long, firstName :String?) :responseMessages{
    return if(RequestManager.isLoggedIn(chatId.toString()))
        listOf(TelegramMessageWrapper(
                chatId = chatId,
                text = loginText(firstName),
                replyMarkup = startButtons()))
    else
        listOf(TelegramMessageWrapper(
                chatId = chatId,
                text = startText))
}
fun helpCommand(chatId: Long) :responseMessages = listOf(TelegramMessageWrapper(chatId, helpText))
fun loginCommand(chatId: Long, firstName :String?, args: List<String>) :responseMessages{
    if(args.size != 2){
        return listOf(TelegramMessageWrapper(chatId, LoginHelpText))
    }

    return if(RequestManager.login(args[0], args[1], chatId.toString()))
        listOf(TelegramMessageWrapper(chatId, loginText(firstName), replyMarkup = startButtons()))
    else
        listOf(TelegramMessageWrapper(chatId, badLogoutText))
}
fun logoutCommand(chatId: Long) :responseMessages{
    return if(RequestManager.logout(chatId.toString()))
        listOf(TelegramMessageWrapper(chatId, startText))
    else
        listOf(TelegramMessageWrapper(chatId, errorText))
}