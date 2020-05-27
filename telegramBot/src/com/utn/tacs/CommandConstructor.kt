package com.utn.tacs

import com.github.kotlintelegrambot.CommandHandleUpdate
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler


const val textoServerCaido = "Ocurrio un error al conectarse al servidor :("
const val textoUsuarioNoLogueado = "El usuario actual no esta logueado con ninguna cuenta\n" +
                                    "Para loguearse utilice el comando: \n" +
                                    "/login usuario contraseña"
const val textoUsuarioYaLogueado = "El usuario ya se encuentra logueado. \n" +
                                    "Para cambiar usuario utilice el comando: \n" +
                                    "/logout"

fun createCommandHandler(command: String, handler: HandleUpdate) :CommandHandler = CommandHandler(command, commandHandlerCheckStatusAndSession(handler))
fun commandHandlerCheckStatusAndSession(method :HandleUpdate) :HandleUpdate {
    return { bot, update->
        when{
            !healthCheck() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoServerCaido)
            !isLoggedIn(update.message!!.from!!.id.toString()) -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoUsuarioNoLogueado)
            else -> method(bot, update)
        }
    }
}

fun createCommandHandler(command: String, handler: CommandHandleUpdate) :CommandHandler = CommandHandler(command, commandHandlerCheckStatusAndSession(handler))
fun commandHandlerCheckStatusAndSession(method :CommandHandleUpdate) :CommandHandleUpdate {
    return { bot, update, args->
        when{
            !healthCheck() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoServerCaido)
            !isLoggedIn(update.message!!.from!!.id.toString()) -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoUsuarioNoLogueado)
            else -> method(bot, update, args)
        }
    }
}

fun commandHandlerLogin(method :CommandHandleUpdate) :CommandHandleUpdate {
    return { bot, update, args->
        when{
            !healthCheck() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoServerCaido)
            isLoggedIn(update.message!!.from!!.id.toString()) -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoUsuarioYaLogueado)
            else -> method(bot, update, args)
        }
    }
}

fun createCallbackQueryHandler(data: String? = null, body: HandleUpdate) : CallbackQueryHandler = CallbackQueryHandler(data, handler = callbackQueryHandlerCheckStatusAndSession(body))
fun callbackQueryHandlerCheckStatusAndSession(method :HandleUpdate) :HandleUpdate {
    return { bot, update ->
        when{
            !healthCheck() -> update.callbackQuery?.let {
                                    bot.sendMessage(chatId = it.message!!.chat.id, text = textoServerCaido)
                                }
            !isLoggedIn(update.callbackQuery?.message!!.chat.id.toString()) -> update.callbackQuery?.let {
                                                                                    bot.sendMessage(chatId = it.message!!.chat.id, text = textoUsuarioNoLogueado)
                                                                                }
            else -> method(bot, update)
        }
    }
}

