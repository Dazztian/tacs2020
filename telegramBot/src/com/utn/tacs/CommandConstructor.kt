package com.utn.tacs

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.CommandHandleUpdate
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandler
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.extensions.filters.Filter


const val textoServerCaido = "An error occurred while connecting to the server \uD83D\uDE1F"
const val textoUsuarioNoLogueado = "The current user is not logged in\n" +
                                    "To login write: \n" +
                                    "/login {usuario} {contraseña}"
const val textoUsuarioYaLogueado = "The current user is already logged in. \n" +
                                    "To change users write: \n" +
                                    "/logout"
const val textoArgumentsExpected =  "Error while trying to use a command without arguments\n" +
                                    "For help use /help"

/**
* This methods add the backend app status and login checker to a telegram command and query
*/

private class QueryHandleUpdateProxy(private val handleUpdate: CommandHandleUpdate) : HandleUpdate {
    override fun invoke(bot: Bot, update: Update) {
        handleUpdate(bot, update, update.callbackQuery?.data?.split("\\s+".toRegex())?.drop(1) ?: listOf())
    }
}

fun sendMessages(bot: Bot, listMessage: List<TelegramMessageWrapper>){
    listMessage.forEach { m -> bot.sendMessage( m.chatId,
                                                m.text,
                                                m.parseMode,
                                                m.disableWebPagePreview,
                                                m.disableNotification,
                                                m.replyToMessageId,
                                                m.replyMarkup) }
}


//Sin argumentos
fun createCommandHandler(command: String, handler: updateHandler) :CommandHandler =
        CommandHandler(command, commandHandlerCheckStatusAndSession(handler))
fun commandHandlerCheckStatusAndSession(method :updateHandler) :HandleUpdate {
    return { bot, update->
        when{
            !healthCheck() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoServerCaido)
            !isLoggedIn(update.message!!.from!!.id.toString()) -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoUsuarioNoLogueado)
            else -> sendMessages(bot, method(bot, update))
        }
    }
}

fun createCallbackQueryHandler(data: String? = null, body: updateHandler) : CallbackQueryHandler =
        CallbackQueryHandler(data, handler = callbackQueryHandlerCheckStatusAndSession(body))
fun callbackQueryHandlerCheckStatusAndSession(method :updateHandler) :HandleUpdate {
    return { bot, update ->
        when{
            !healthCheck() -> update.callbackQuery?.let {
                bot.sendMessage(chatId = it.message!!.chat.id, text = textoServerCaido)
            }
            !isLoggedIn(update.callbackQuery?.message!!.chat.id.toString()) -> update.callbackQuery?.let {
                bot.sendMessage(chatId = it.message!!.chat.id, text = textoUsuarioNoLogueado)
            }
            else -> sendMessages(bot, method(bot, update))
        }
    }
}

fun createMessageHandler(filter: Filter, handler: updateHandler) :MessageHandler =
        MessageHandler({bot, update ->  sendMessages(bot, handler(bot, update))}, filter)


//Con argumentos
fun createCommandHandlerNoLoginRequired(command: String, handler: updateHandlerArgs) :CommandHandler =
        CommandHandler(command, commandHandlerNoLoginRequired(handler))
fun commandHandlerNoLoginRequired(method :updateHandlerArgs) :CommandHandleUpdate {
    return { bot, update, args->
        when{
            !healthCheck() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoServerCaido)
            isLoggedIn(update.message!!.from!!.id.toString()) -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoUsuarioYaLogueado)
            args.isEmpty() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoArgumentsExpected)
            else -> sendMessages(bot, method(bot, update, args))
        }
    }
}

fun createCommandHandler(command: String, handler: updateHandlerArgs) :CommandHandler =
        CommandHandler(command, commandHandlerCheckStatusAndSession(handler))
fun commandHandlerCheckStatusAndSession(method :updateHandlerArgs) :CommandHandleUpdate {
    return { bot, update, args->
        when{
            !healthCheck() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoServerCaido)
            !isLoggedIn(update.message!!.from!!.id.toString()) -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoUsuarioNoLogueado)
            args.isEmpty() -> bot.sendMessage(chatId = update.message!!.chat.id, text = textoArgumentsExpected)
            else -> sendMessages(bot, method(bot, update, args))
        }
    }
}

fun createCallbackQueryHandler(data: String? = null, body: updateHandlerArgs) : CallbackQueryHandler =
        CallbackQueryHandler(data, handler = QueryHandleUpdateProxy(callbackQueryHandlerCheckStatusAndSession(body)))
fun callbackQueryHandlerCheckStatusAndSession(method :updateHandlerArgs) :CommandHandleUpdate {
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
            else -> sendMessages(bot, method(bot, update, args))
        }
    }
}