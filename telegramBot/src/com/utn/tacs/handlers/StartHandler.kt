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
                                InlineKeyboardButton("Mis listas", callbackData = "MisListas"),
                                InlineKeyboardButton("Paises", callbackData = "Paises")
                            ),
                            listOf(
                                InlineKeyboardButton(text = "Logout", callbackData = "Logout")
                            )))

fun startMessageCallBackQuery(update :Update) = startMessageBuilder(update.callbackQuery!!.from.firstName)
fun loginText(update :Update) = startMessageBuilder(update.message!!.from?.firstName ?: "errorName")
fun startMessageBuilder(firstName :String) = "Bienvenido $firstName!"

const val startText = "Bienvenido al bot de Telegram del grupo 4 de TACS 2020!  \uD83D\uDCBB\n\n" +
                    "Para iniciar escriba el comando /login seguido de su usuario y contraseña\n" +
                    "(Ejemplo: /login user pass)"

const val textoLoginIncorrecto = "Usuario o contraseña es incorrecta"
const val textoLoginHelp = "Escriba el comando /login seguido de su usuario y contraseña\n" +
                            "(Ejemplo: /login user pass)"

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
        CommandHandler("login", commandHandlerLogin { bot, update, args->
            if(args.size != 2){
                bot.sendMessage(
                    chatId = update.message!!.chat.id,
                    text = textoLoginHelp
                )
                return@commandHandlerLogin
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

        createCallbackQueryHandler("Inicio") { bot, update ->
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