package com.utn.tacs

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.CommandHandleUpdate
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.entities.Update


const val textoServerCaido = "An error occurred while connecting to the server \uD83D\uDE1F"
const val textoUsuarioNoLogueado = "The current user is not logged in\n" +
                                    "To login write: \n" +
                                    "/login {usuario} {contraseña}"
const val textoUsuarioYaLogueado = "The current user is already logged in. \n" +
                                    "To change users write: \n" +
                                    "/logout"
const val textoArgumentsExpected =  "Error while tryinh to use a command without arguments\n" +
                                    "For help use /help"

/**
* This methods add the backend app status and login checker to a telegram command and query
*/

//Sin argumentos
fun commandHandlerNoLoginRequired(method :CommandHandleUpdate) :CommandHandleUpdate {
    return { bot, update, args->
        when{
            !healthCheck() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoServerCaido)
            isLoggedIn(update.message!!.from!!.id.toString()) -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoUsuarioYaLogueado)
            else -> method(bot, update, args)
        }
    }
}

fun createCommandHandler(command: String, handler: HandleUpdate) :CommandHandler =
        CommandHandler(command, commandHandlerCheckStatusAndSession(handler))
fun commandHandlerCheckStatusAndSession(method :HandleUpdate) :HandleUpdate {
    return { bot, update->
        when{
            !healthCheck() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoServerCaido)
            !isLoggedIn(update.message!!.from!!.id.toString()) -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoUsuarioNoLogueado)
            else -> method(bot, update)
        }
    }
}

fun createCallbackQueryHandler(data: String? = null, body: HandleUpdate) : CallbackQueryHandler =
        CallbackQueryHandler(data, handler = callbackQueryHandlerCheckStatusAndSession(body))
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

//Con argumentos
fun createCommandHandler(command: String, handler: CommandHandleUpdate) :CommandHandler =
        CommandHandler(command, commandHandlerCheckStatusAndSession(handler))
fun commandHandlerCheckStatusAndSession(method :CommandHandleUpdate) :CommandHandleUpdate {
    return { bot, update, args->
        when{
            !healthCheck() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoServerCaido)
            !isLoggedIn(update.message!!.from!!.id.toString()) -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoUsuarioNoLogueado)
            args.isEmpty() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoArgumentsExpected)
            else -> method(bot, update, args)
        }
    }
}

fun createCallbackQueryHandler(data: String? = null, body: CommandHandleUpdate) : CallbackQueryHandler =
        CallbackQueryHandler(data, handler = QueryHandleUpdateProxy(callbackQueryHandlerCheckStatusAndSession(body)))
fun callbackQueryHandlerCheckStatusAndSession(method :CommandHandleUpdate) :CommandHandleUpdate {
    return { bot, update, args ->
        when{
            !healthCheck() -> update.callbackQuery?.let {
                bot.sendMessage(chatId = it.message!!.chat.id, text = textoServerCaido)
            }
            !isLoggedIn(update.callbackQuery?.message!!.chat.id.toString()) -> update.callbackQuery?.let {
                bot.sendMessage(chatId = it.message!!.chat.id, text = textoUsuarioNoLogueado)
            }
            args.isEmpty() -> update.callbackQuery?.let {
                bot.sendMessage(chatId = it.message!!.chat.id, text = textoArgumentsExpected)
            }
            else -> method(bot, update, args)
        }
    }
}

private class QueryHandleUpdateProxy(private val handleUpdate: CommandHandleUpdate) : HandleUpdate {
    override fun invoke(bot: Bot, update: Update) {
        handleUpdate(bot, update, update.callbackQuery?.data?.split("\\s+".toRegex())?.drop(1) ?: listOf())
    }
}
