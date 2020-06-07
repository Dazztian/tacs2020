package com.utn.tacs

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.utn.tacs.handlers.countryListCommands
import com.utn.tacs.handlers.startCommands


//Map of last important messages separatad by each telegram user Id
val lastImportantMessages = mutableMapOf<Long, MessageWrapper>()

fun main(args: Array<String>) {
    val bot = bot {
        token = "1250247908:AAFafjMtBEQGMpH1cl4_xk2u5PA--ckVF6A"

        startCommands(updater)
        countryListCommands(updater)

        dispatch {
            /*command("a") { bot, update->

                val keyboardMarkup = InlineKeyboardMarkup(
                                        inlineKeyboard = listOf(
                                                    listOf(InlineKeyboardButton("test", callbackData = "cfguiokjhgfdtyuj"))
                                            ))
                bot.sendMessage(
                        chatId = update.message!!.chat.id,
                        text = update.message!!.chat.id.toString()+" "+update.message!!.from!!.id.toString(),
                        replyMarkup = keyboardMarkup
                )
            }
            callbackQuery("cfguiokjhgfdtyuj") { bot, update ->
                update.callbackQuery?.let {
                    val chatId = it.message?.chat?.id ?: return@callbackQuery

                    bot.sendMessage(
                            chatId = chatId,
                            text = it.message!!.from!!.id.toString() + " " + it.message!!.chat.id
                    )
                }
            }
            message(Filter.Text){ bot, update ->
                lastImportantMessages[update.message!!.from!!.id] = MessageWrapper(MessageType.ADD_COUNTRY, "")
                bot.sendMessage(update.message!!.chat.id,
                        text = update.message!!.from!!.id.toString() +" "+ update.message!!.chat.id)
            }*/

            // DATABASE
            /*
            command("db"){ bot, update->
                val response = getResponse(URL("http://localhost:8080/api/countries/tree"))
                val dataList :Array<CountryData> = gson.fromJson(response, object : TypeToken<Array<CountryData>>() {}.type)

                dataList.forEachIndexed  { _, data ->
                    val result = bot.sendMessage(
                            chatId = update.message!!.chat.id,
                            text = data.toString()
                    )
                }
            }
            command("iso") { bot, update, args ->
                val response = getResponse(URL("http://localhost:8080/api/countries/"+args.joinToString(separator = "")))
                val countryData :CountryData = gson.fromJson(response, object : TypeToken<CountryData>(){}.type)

                bot.sendMessage(
                        chatId = update.message!!.chat.id,
                        text = TelegramMessageParser().parse(countryData)
                )
            }


            //  TABLA HARDCODEADA
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

            //  IMAGE TEST
            command("photo") { bot, update->
                bot.sendChatAction(chatId = update.message!!.chat.id, action = UPLOAD_PHOTO)
                val result = bot.sendPhoto(chatId = update.message!!.chat.id, photo = File(System.getProperty("user.dir")+"\\cat.jpg"))
                bot.sendMessage(chatId = update.message!!.chat.id, text = "sent")
            }
             */
        }
    }
    bot.startPolling()
}