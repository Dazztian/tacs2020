package com.utn.tacs

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.inlineQuery
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatAction.UPLOAD_PHOTO
import com.github.kotlintelegrambot.entities.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.KeyboardButton
import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.ParseMode.HTML
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.network.fold
import java.io.File


fun main(args: Array<String>) {
    val bot = bot {
        token = "1250247908:AAEWItlMvAubZPRyZJt9H2mCANIxWrsii68"

        dispatch {
            command("start") { bot, update->
                val result = bot.sendMessage(chatId = update.message!!.chat.id, text = "Hi there!")
                result.fold({
                    // do something here with the response
                },{
                    // do something with the error
                })
            }

            command("tabla") { bot, update->
                val a = "<pre>\n" +
                        "|    Pais   | Infectados | Muertos | Curados |\n" +
                        "|:---------:|------------|---------|---------|\n" +
                        "| Argentina | 1.500      | 300     | 150     |\n" +
                        "| Chile     | 3.000      | 500     | 350     |\n" +
                        "| Uruguay   | 200        | 26      | 10      |\n" +
                        "| Brasil    | 4.000      | 1.000   | 350     |\n" +
                        "| EEUU      | 1.500.000  | 300.000 | 100.000 |\n" +
                        "| Canada    | 700.000    | 150.000 | 200.000 |\n" +
                        "</pre>"


                val result = bot.sendMessage(
                    chatId = update.message!!.chat.id,
                    text = a
                )

            }
            command("paises") { bot, update->

                val result = bot.sendMessage(
                    chatId = update.message!!.chat.id,
                    text = "Paises:",
                    parseMode= HTML,
                    replyMarkup = InlineKeyboardMarkup(
                        listOf(
                            listOf(
                                InlineKeyboardButton("Argentina", callbackData = "Argentina"),
                                InlineKeyboardButton("Chile", callbackData = "Chile")
                            ),
                            listOf(
                                InlineKeyboardButton(text = "Uruguay", callbackData = "Uruguay"),
                                InlineKeyboardButton(text = "Brasil", callbackData = "Brasil")
                            ))
                    )
                )
            }
            callbackQuery("Argentina") { bot, update ->
                update.callbackQuery?.let {
                    val chatId = it.message?.chat?.id ?: return@callbackQuery


                    val a = "<pre>\n" +
                            "|    Pais   | Infectados | Muertos | Curados |\n" +
                            "|:---------:|------------|---------|---------|\n" +
                            "| Chile     | 3.000      | 500     | 350     |\n" +
                            "</pre>"

                    bot.sendMessage(
                        chatId = chatId,
                        text = a,
                        parseMode= HTML
                    )
                }
            }
            callbackQuery("Chile") { bot, update ->
                update.callbackQuery?.let {
                    val chatId = it.message?.chat?.id ?: return@callbackQuery


                    val a = "<pre>\n" +
                            "|    Pais   | Infectados | Muertos | Curados |\n" +
                            "|:---------:|------------|---------|---------|\n" +
                            "| Argentina | 1.500      | 300     | 150     |\n" +
                            "</pre>"

                    bot.sendMessage(
                        chatId = chatId,
                        text = a,
                        parseMode= HTML
                    )
                }
            }
            command("photo") { bot, update->
                bot.sendChatAction(chatId = update.message!!.chat.id, action = UPLOAD_PHOTO)
                val result = bot.sendPhoto(chatId = update.message!!.chat.id, photo = File(System.getProperty("user.dir")+"\\cat.jpg"))
                bot.sendMessage(chatId = update.message!!.chat.id, text = "sent")
            }
        }
    }
    bot.startPolling()
}