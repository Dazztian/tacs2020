package com.utn.tacs.handlers

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
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
fun returnButtonNoMarkup() = listOf(
                                    listOf(
                                        InlineKeyboardButton(
                                            text = "Return",
                                            callbackData = "Start")
                                    ))
fun returnButton() = InlineKeyboardMarkup(returnButtonNoMarkup())

fun startCommands(updater :Updater){
    listOf(
        CommandHandler("start") { bot, update->
            if(healthCheck() && isLoggedIn(update.message!!.from!!.id.toString()))
                bot.sendMessage(
                    chatId = update.message!!.chat.id,
                    text = loginText(update),
                    replyMarkup = startButtons()
                )
            else
                bot.sendMessage(
                    chatId = update.message!!.chat.id,
                    text = startText
                )
        },
        CommandHandler("help") { bot, update->
            bot.sendMessage(
                    chatId = update.message!!.chat.id,
                    text = helpText
            )
        },
        CommandHandler("login", commandHandlerNoLoginRequired { _, update, args->
            val chatId = update.message!!.chat.id
            if(args.size != 2){
                return@commandHandlerNoLoginRequired listOf(TelegramMessageWrapper(chatId, textoLoginHelp))
            }

            if(login(args[0], args[1], update.message!!.from!!.id.toString()))
                listOf(TelegramMessageWrapper(chatId, loginText(update), replyMarkup = startButtons()))
            else
                listOf(TelegramMessageWrapper(chatId, badLogoutText))

        }),
        createCommandHandler("logout") { _, update->
            val chatId = update.message!!.chat.id

            return@createCommandHandler if(logout(chatId.toString()))
                listOf(TelegramMessageWrapper(chatId, startText))
            else
                listOf(TelegramMessageWrapper(chatId, errorText))
        },

        createCallbackQueryHandler("Help") { _, update ->
            listOf(TelegramMessageWrapper(update.callbackQuery!!.message!!.chat.id, helpText))
        },
        createCallbackQueryHandler("Start") { _, update ->
                listOf(TelegramMessageWrapper(
                        update.callbackQuery!!.message!!.chat.id,
                        startMessageCallBackQuery(update),
                        replyMarkup = startButtons()))
        },
        createCallbackQueryHandler("Logout") { _, update ->
            val chatId = update.callbackQuery!!.message!!.chat.id

            if(logout(chatId.toString())) {
                lastImportantMessages.remove(chatId)

                listOf(TelegramMessageWrapper(chatId, startText))
            } else {
                listOf(TelegramMessageWrapper(chatId, errorText))
            }
        }
    ).forEach{updater.dispatcher.addHandler(it)}
}