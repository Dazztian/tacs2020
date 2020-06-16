package com.utn.tacs

import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Response(
    val status: Status,
    val responseString: String)
data class Status(
    val code:Int,
    val message :String)

class RequestManager{
    companion object {
        fun getResponse(url :String) :Response {
            val (_, response, result) = Fuel.get(url).responseString()

            val (payload, _) = result
            return Response(Status(response.statusCode, String(response.data)), payload.toString())
        }
        fun getResponse(url :String, json :String) :Response {
            val (_, response, result) = Fuel.post(url)
                .header("Content-Type", "application/json")
                .body(json)
                .responseString()

            val (payload, _) = result
            return Response(Status(response.statusCode, String(response.data)), payload.toString())
        }

        //Returns if server is running
        fun healthCheck() : Boolean{
            return try {
                val response = getResponse(urlBase+"configuration")
                response.responseString == "Application running"
            } catch (exc :Exception) {
                val a = exc
                false
            }
        }

        //Returns if telegram user has an active session
        fun isLoggedIn(telegramUserId :String) :Boolean{
            return try {
                val response = getResponse(urlBase+"api/telegram?telegramId=$telegramUserId")

                response.status.code == 200
            } catch (exc :Exception) {
                false
            }
        }

        //Returns if the user is logged in
        fun login(username :String, password :String, telegramUserId :String) :Boolean{
            return try {
                val response = getResponse(urlBase+"api/telegram/login",
                    Gson().toJson(TelegramUser(telegramUserId, username, password)).toString())

                response.status.code == 200
            } catch (exc :Exception) {
                false
            }
        }

        //Returns if the logout was successful
        fun logout(telegramUserId :String) :Boolean{
            return try {
                val response = getResponse(urlBase+"api/telegram/logout",
                    Gson().toJson(TelegramUser(telegramUserId, null, null)).toString())

                response.status.code == 200
            } catch (exc :Exception) {
                false
            }
        }

        //Returns the coutry lists from the logged in user
        fun getCountryLists(telegramUserId :String) :List<CountriesList>{
            return try {
                val response = getResponse(urlBase+"api/telegram/countryList?telegramId=$telegramUserId")

                when (response.status.code) {
                    200 -> Gson().fromJson(response.responseString, Array<CountriesList>::class.java).asList()
                    else -> emptyList()
                }
            } catch (exc :Exception) {
                emptyList()
            }
        }

        //Returns the countryList with the id
        fun getCountryByName(name :String) :Country?{
            return try {
                val response = getResponse(urlBase+"api/countries?name=$name")

                when (response.status.code) {
                    200 -> Gson().fromJson(response.responseString, Country::class.java)
                    else -> null
                }
            } catch (exc :Exception) {
                println(exc.toString())
                null
            }
        }

        //Returns all countries names
        fun allCountriesNames() :Array<CountryNamesResponse>{
            return try {
                val response = getResponse(urlBase+"api/countries/names")

                when (response.status.code) {
                    200 -> Gson().fromJson(response.responseString, Array<CountryNamesResponse>::class.java)
                    else -> emptyArray()
                }
            } catch (exc :Exception) {
                println(exc.message)
                emptyArray()
            }
        }

        //Returns the last country values from a list
        fun getListCountries(listId :String, telegramId :String) :List<Country>{
            return try {
                val response = getResponse(urlBase+"api/telegram/countryList/$listId?telegramId=$telegramId")

                when (response.status.code) {
                    200 -> Gson().fromJson(response.responseString, Array<Country>::class.java).toList()
                    else -> emptyList()
                }
            } catch (exc :Exception) {
                emptyList()
            }
        }

        fun addCountries(telegramUserId: String, listId :String, countries :Set<String>) :String{
            return try {
                val response = getResponse(urlBase+"api/telegram/countryList/$listId/add?telegramId=$telegramUserId",
                    Gson().toJson(UserCountriesListModificationRequest("list", countries.toMutableSet())).toString())

                response.status.message
            } catch (exc :Exception) {
                "Error"
            }
        }

        fun newCountriesList(telegramUserId: String, listName :String, countries :List<String>) :String{
            return try {
                val response = getResponse(urlBase+"api/telegram/countryList?telegramId=$telegramUserId",
                    Gson().toJson(UserCountriesListModificationRequest(listName, countries.toMutableSet())).toString())

                return if(response.status.code == 200){
                    val userCountriesList = Gson().fromJson(response.responseString, UserCountriesListResponse::class.java)

                    "OK "+userCountriesList.id
                }else{
                    response.status.message
                }
            } catch (exc :Exception) {
                "Error"
            }
        }

        fun getTimesesiesList(listId :String, minusDays: Long) :List<CountryResponseTimeseries>{
            val today = LocalDateTime.now()
            val toDateString = today.format(DateTimeFormatter.ofPattern("MM/dd/yy"))
            val fromDateString = today.minusDays(minusDays).format(DateTimeFormatter.ofPattern("MM/dd/yy"))
            return try {
                val url = urlBase+"api/telegram/countryList/$listId/timeseries?" +
                        "toDate=$toDateString" +
                        "&fromDate=$fromDateString"
                val response = getResponse(url)

                when (response.status.code) {
                    200 -> {
                        Gson().fromJson(response.responseString, Array<CountryResponseTimeseries>::class.java).toList()
                    }
                    else -> emptyList()
                }
            } catch (exc :Exception) {
                emptyList()
            }
        }
    }
}