package com.utn.tacs

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.*
import java.time.LocalDate


typealias updateHandler = (Bot, Update) -> List<TelegramMessageWrapper>
typealias updateHandlerArgs = (Bot, Update, List<String>) -> List<TelegramMessageWrapper>

interface MessageType{
    object ADD_COUNTRY : MessageType
    object NEW_LIST : MessageType
    object LAST_X_DAYS : MessageType
}
class PreviousMessageWrapper(
        val messageType: MessageType,
        val countryListId: String)

interface RequestModelInterface{
    //Retorna el model a una fila de tabla para mensaje telegram
    fun toTableRowString() :String
    //Retorna el header de la tabla
    fun tableHeader() :String
    //Returns the a table with the list
    fun toTable() :List<String> = organizeInCharacters(listOf(tableHeader()) + toTableRowString(), 4084)
                                        .map { row -> "<pre>\n$row</pre>" }
}
sealed class RequestModel :RequestModelInterface

data class CountriesList (
        val _id: String?,
        val userId: String?,
        val name: String?,
        val countries: Set<String>?,
        val creationDate: LocalDate?
) :RequestModel() {
    fun toButton(): InlineKeyboardButton = InlineKeyboardButton("$name", callbackData = "Check_list $_id")

    override fun toTableRowString(): String {
        return createTableRowString(
                            mapOf(  (name ?: "") to 19,
                                    (countries?.size?.toString() ?: "0") to 10))
    }

    override fun tableHeader(): String {
        return  "|        Name        | Countries |\n" +
                "|:------------------:|:---------:|\n"
    }
}


data class Country(
        val _id: String?,
        val countryregion: String?,
        val lastupdate: String?,
        val location: Location?,
        val countrycode: CountryCode?,
        val confirmed: Int?,
        val deaths: Int?,
        val recovered: Int?,
        var timeseries: List<TimeSerie>? = listOf()
) :RequestModel() {
    override fun toTableRowString(): String {
        return createTableRowString(
                mapOf(  (countryregion ?: "") to 13,
                        (confirmed?.toString() ?: "0") to 10,
                        (deaths?.toString() ?: "0") to 9,
                        (recovered?.toString() ?: "0") to 10))
    }

    override fun tableHeader(): String {
        return  "|     Name     | Confirmed |  Deaths  | Recovered |\n" +
                "|:------------:|:---------:|:--------:|:---------:|\n"
    }
}

data class TimeSerie(
        val number: Int,
        val confirmed: Int,
        val deaths: Int,
        val recovered: Int,
        val date: String
)

data class UserCountriesListResponse(
        val id: String,
        val name: String,
        val countries: MutableSet<String>
)

data class TelegramMessageWrapper(
        val chatId: Long,
        val text: String,
        val parseMode: ParseMode? = null,
        val disableWebPagePreview: Boolean? = null,
        val disableNotification: Boolean? = null,
        val replyToMessageId: Long? = null,
        val replyMarkup: ReplyMarkup? = null)

data class UserNamesResponse(
    val name: String?,
    val iso2: String?
)