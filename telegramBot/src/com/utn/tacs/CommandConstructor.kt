package com.utn.tacs

import com.github.kotlintelegrambot.CommandHandleUpdate
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler

const val textoServerCaido = "Ocurrio un error al conectarse al servidor :("

fun createCommandHandler(command: String, handler: HandleUpdate) :CommandHandler = CommandHandler(command, commandHandlerCheckStatus(handler))
fun commandHandlerCheckStatus(method :HandleUpdate) :HandleUpdate {
    return { bot, update->
        if(healthCheck())
            method(bot, update)
        else
            bot.sendMessage(chatId = update.message!!.chat.id, text = textoServerCaido)
    }
}

fun createCommandHandler(command: String, handler: CommandHandleUpdate) :CommandHandler = CommandHandler(command, commandHandlerCheckStatus(handler))
fun commandHandlerCheckStatus(method :CommandHandleUpdate) :CommandHandleUpdate {
    return { bot, update, args->
        if(healthCheck())
            method(bot, update, args)
        else
            bot.sendMessage(chatId = update.message!!.chat.id, text = textoServerCaido)
    }
}

fun createCallbackQueryHandler(data: String? = null, body: HandleUpdate) : CallbackQueryHandler = CallbackQueryHandler(data, handler = callbackQueryHandlerCheckStatus(body))
fun callbackQueryHandlerCheckStatus(method :HandleUpdate) :HandleUpdate {
    return { bot, update ->
        if(healthCheck())
            method(bot, update)
        else
            update.callbackQuery?.let {
                bot.sendMessage(chatId = it.message!!.chat.id, text = textoServerCaido)
            }
    }
}