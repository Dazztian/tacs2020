package com.utn.tacs

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatAction.UPLOAD_PHOTO
import com.github.kotlintelegrambot.entities.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode.HTML
import com.google.gson.reflect.TypeToken
import com.utn.tacs.handlers.addStartCommands
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

fun main(args: Array<String>) {
    val bot = bot {
        token = "1250247908:AAEWItlMvAubZPRyZJt9H2mCANIxWrsii68"

        addStartCommands(updater)
        dispatch {
            // DATABASE
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
        }
    }
    bot.startPolling()
}


//Returns if server is running
fun healthCheck() : Boolean{
    try {
        HttpURLConnection.setFollowRedirects(false)
        val con = URL("http://localhost:8080/").openConnection()
        con.connectTimeout = 5000 //set timeout to 5 seconds

        return (con.inputStream.bufferedReader().readText() == "Application running");
    }catch (exc :Exception) {
        return false
    }
}
//Returns the response of the connection
fun getResponse(url : URL) :String{
    return try {
        val connection = url.openConnection() as HttpURLConnection
        connection.inputStream.bufferedReader().readText()
    }catch (exc : Exception){
        exc.toString()
    }
}