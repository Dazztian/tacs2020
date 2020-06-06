package com.utn.tacs.handlers

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.entities.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Update
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

fun startMessageCallBackQuery(update :Update) = startMessageBuilder(update.callbackQuery!!.from.firstName)
fun loginText(update :Update) = startMessageBuilder(update.message!!.from?.firstName ?: "errorName")
fun startMessageBuilder(firstName :String) =    "Welcome $firstName!  \uD83D\uDE04\n\n" +
                                                "To see your lists press \"My Lists\"\n" +
                                                "To see command info press \"Help\"\n" +
                                                "Remember to use telegram in horizonral mode for a better experience!"

const val helpText =    "- This bot maintains a registry of the coronavirus advances in the world\n" +
                        "- To begin create an account in $urlBase\n" +
                        "- To login write /login followed by your username and password\n" +
                        "(Example: /login user pass)\n\n" +
                        "- To see the last values of a country use /check {country}\n" +
                        "- To see al countries use /countries\n\n" +
                        "Tip: you don't need to capitalize the country or write the whole name\n" +
                        "Example: /check arg brings Argentina info!"
const val startText =   "Welcome to the Group 4 Telegram Bot!  \uD83D\uDCBB\n\n" +
                        "- To begin write /login followed by your username and password\n" +
                        "(Example: /login user pass)\n\n" +
                        "- To create an account go to $urlBase"

const val textoLoginIncorrecto = "Username or password incorrect   \uD83D\uDE15"
const val textoLoginHelp =  "To begin write /login followed by your username and password\n" +
                            "(Example: /login user pass)"

fun addStartCommands(updater :Updater){
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
        CommandHandler("login", commandHandlerNoLoginRequired { bot, update, args->
            if(args.size != 2){
                bot.sendMessage(
                    chatId = update.message!!.chat.id,
                    text = textoLoginHelp
                )
                return@commandHandlerNoLoginRequired
            }

            if(login(args[0], args[1], update.message!!.from!!.id.toString()))
                bot.sendMessage(
                    chatId = update.message!!.chat.id,
                    text = loginText(update),
                    replyMarkup = startButtons()
                )
            else
                bot.sendMessage(
                    chatId = update.message!!.chat.id,
                    text = textoLoginIncorrecto
                )

        }),
        createCommandHandler("logout") { bot, update->
            val chatId = update.message!!.chat.id
            if(logout(update.message!!.from!!.id.toString())) {
                bot.sendMessage(
                        chatId = chatId,
                        text = "Logout exitoso!"
                )
                bot.sendMessage(
                        chatId = chatId,
                        text = startText
                )
            } else {
                bot.sendMessage(
                        chatId = chatId,
                        text = "Error al procesar la solicitud"
                )
            }
        },

        createCallbackQueryHandler("Help") { bot, update ->
                update.callbackQuery?.let {
                    val chatId = it.message!!.chat.id
                    bot.sendMessage(
                            chatId = chatId,
                            text = helpText
                    )
                }
            },
        createCallbackQueryHandler("Start") { bot, update ->
            update.callbackQuery?.let {
                val chatId = it.message!!.chat.id
                bot.sendMessage(chatId = chatId,
                                text = startMessageCallBackQuery(update),
                                replyMarkup = startButtons())
            }
        },
        createCallbackQueryHandler("Logout") { bot, update ->
                update.callbackQuery?.let {
                    val chatId = it.message!!.chat.id

                    if(logout(update.callbackQuery?.message!!.chat.id.toString())) {
                        bot.sendMessage(
                            chatId = chatId,
                            text = "Logout exitoso!"
                        )
                        bot.sendMessage(
                            chatId = chatId,
                            text = startText
                        )
                    } else {
                        bot.sendMessage(
                            chatId = chatId,
                            text = "Error al procesar la solicitud"
                        )
                    }
                }
            }
    ).forEach{updater.dispatcher.addHandler(it)}
}