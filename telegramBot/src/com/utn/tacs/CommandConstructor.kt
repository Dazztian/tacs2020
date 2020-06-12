package com.utn.tacs

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.CommandHandleUpdate
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandler
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.extensions.filters.Filter

/**
* This methods add the backend app status and login checker to a telegram command and query
*/

fun sendMessages(bot: Bot, listMessage: List<TelegramMessageWrapper>){
    listMessage.forEach { m -> bot.sendMessage( m.chatId,
                                                m.text,
                                                m.parseMode,
                                                m.disableWebPagePreview,
                                                m.disableNotification,
                                                m.replyToMessageId,
                                                m.replyMarkup) }
}

interface LoginType{
    object NotLoggedIn : LoginType
    object LoggedIn : LoginType
    object NotRequired : LoginType
}

private fun validateLoginType(bot: Bot, chatId :Long, type: LoginType) :Boolean{
    when{
        !RequestManager.healthCheck() -> bot.sendMessage(chatId = chatId, text = unresponsiveServerText)
        type == LoginType.NotRequired -> return true
        else -> {
            val loggedIn = RequestManager.isLoggedIn(chatId.toString())

            when{
                loggedIn && type == LoginType.NotLoggedIn -> bot.sendMessage(chatId = chatId, text = UserLoggedInText)
                !loggedIn && type == LoginType.LoggedIn -> bot.sendMessage(chatId = chatId, text = UserNotLoggedInText)
                else -> return true
            }
        }
    }

    return false
}

//Sin argumentos
fun createCommandHandler(command: String, type: LoginType, handler: updateHandler) :CommandHandler =
        CommandHandler(command, commandHandlerBuilder(handler, type))
private fun commandHandlerBuilder(method :updateHandler, type :LoginType) :HandleUpdate {
    return { bot, update ->
        val chatId = update.message!!.chat.id
        if(validateLoginType(bot, chatId, type)) sendMessages(bot, method(bot, update))
    }
}

fun createCallbackQueryHandler(data: String? = null, type: LoginType, body: updateHandler) : CallbackQueryHandler =
        CallbackQueryHandler(data, handler = callbackQueryHandlerCheckStatusAndSession(body, type))
private fun callbackQueryHandlerCheckStatusAndSession(method :updateHandler, type: LoginType) :HandleUpdate {
    return { bot, update ->
        val chatId = update.callbackQuery!!.message!!.chat.id
        if(validateLoginType(bot, chatId, type)) sendMessages(bot, method(bot, update))
    }
}

fun createMessageHandler(filter: Filter, handler: updateHandler) :MessageHandler =
        MessageHandler({bot, update ->  sendMessages(bot, handler(bot, update))}, filter)


//Con argumentos
fun createCommandHandler(command: String, type: LoginType, handler: updateHandlerArgs) :CommandHandler =
        CommandHandler(command, commandHandlerBuilder(handler, type))
private fun commandHandlerBuilder(method :updateHandlerArgs, type :LoginType) :CommandHandleUpdate {
    return { bot, update, args->
        val chatId = update.message!!.chat.id
        when{
            args.isEmpty() -> bot.sendMessage(chatId = chatId, text = ArgumentsExpectedText)
            else -> if(validateLoginType(bot, chatId, type)) sendMessages(bot, method(bot, update, args))
        }
    }
}

fun createCallbackQueryHandler(data: String? = null, type: LoginType, body: updateHandlerArgs) : CallbackQueryHandler =
        CallbackQueryHandler(data, handler = QueryHandleUpdateProxy(callbackQueryHandlerCheckStatusAndSession(body, type)))
private fun callbackQueryHandlerCheckStatusAndSession(method :updateHandlerArgs, type: LoginType) :CommandHandleUpdate {
    return { bot, update, args ->
        val chatId = update.callbackQuery!!.message!!.chat.id
        when{
            args.isEmpty() -> bot.sendMessage(chatId = chatId, text = ArgumentsExpectedText)
            else -> if(validateLoginType(bot, chatId, type)) sendMessages(bot, method(bot, update, args))
        }
    }
}

private class QueryHandleUpdateProxy(private val handleUpdate: CommandHandleUpdate) : HandleUpdate {
    override fun invoke(bot: Bot, update: Update) {
        handleUpdate(bot, update, update.callbackQuery?.data?.split("\\s+".toRegex())?.drop(1) ?: listOf())
    }
}