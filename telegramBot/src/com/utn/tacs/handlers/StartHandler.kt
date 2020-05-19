package com.utn.tacs.handlers

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.entities.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.updater.Updater
import com.utn.tacs.createCallbackQueryHandler
import com.utn.tacs.createCommandHandler

fun startButtons() = InlineKeyboardMarkup(
                        listOf(
                            listOf(
                                InlineKeyboardButton("Mis listas", callbackData = "MisListas"),
                                InlineKeyboardButton("Listas", callbackData = "Listas")
                            ),
                            listOf(
                                InlineKeyboardButton(text = "Logout", callbackData = "Logout")
                            )))

fun startMessageCallBackQuery(update :Update) = startMessageBuilder(update.callbackQuery!!.from.firstName)
fun loginText(update :Update) = startMessageBuilder(update.message!!.from?.firstName ?: "errorName")
fun startMessageBuilder(firstName :String) = "Bienvenido $firstName!"

fun addStartCommands(updater :Updater){
    listOf(
            CommandHandler("start") { bot, update->
                bot.sendMessage(
                        chatId = update.message!!.chat.id,
                        text = "Bienvenido al bot de Telegram del grupo 4 de TACS 2020!\n\n" +
                                "Para iniciar escriba el comando /login seguido de su usuario y contraseña\n" +
                                "(Ejemplo: /login user pass)"
                )
            },
            createCommandHandler("up") { _, _ ->},
            createCommandHandler("login") { bot, update, args->
                if(args.size != 2){
                    bot.sendMessage(
                        chatId = update.message!!.chat.id,
                        text = "Escriba el comando /login seguido de su usuario y contraseña\n" +
                                "(Ejemplo: /login user pass)"
                    )
                    return@createCommandHandler
                }

                bot.sendMessage(
                    chatId = update.message!!.chat.id,
                    text = loginText(update),
                    replyMarkup = startButtons()
                )
            },

            createCallbackQueryHandler("startCallBackQuery") { bot, update ->
                update.callbackQuery?.let {
                    val chatId = it.message!!.chat.id
                    bot.sendMessage(chatId = chatId,
                                    text = startMessageCallBackQuery(update),
                                    replyMarkup = startButtons())
                }
            },
            createCallbackQueryHandler("MisListas") { bot, update ->
                update.callbackQuery?.let {
                    val chatId = it.message!!.chat.id

                    bot.sendMessage(
                        chatId = chatId,
                        text = "Listas: ",
                        replyMarkup = InlineKeyboardMarkup(
                            listOf(
                                listOf(
                                    InlineKeyboardButton(text = "Volver", callbackData = "startCallBackQuery")
                                ))
                        )
                    )
                }
            },
            createCallbackQueryHandler("Logout") { bot, update ->
                update.callbackQuery?.let {
                    val chatId = it.message!!.chat.id

                    bot.sendMessage(
                            chatId = chatId,
                            text = "Logout"
                    )
                }
            }
    ).forEach{updater.dispatcher.addHandler(it)}
}