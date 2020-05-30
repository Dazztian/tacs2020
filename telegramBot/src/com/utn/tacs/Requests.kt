package com.utn.tacs

import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL


//Returns if server is running
fun healthCheck() : Boolean{
    return try {
        HttpURLConnection.setFollowRedirects(false)
        val con = URL("http://localhost:8080/").openConnection()
        con.connectTimeout = 5000 //set timeout to 5 seconds

        (con.inputStream.bufferedReader().readText() == "Application running")
    } catch (exc :Exception) {
        false
    }
}

//Returns if telegram user has an active session
fun isLoggedIn(telegramUserId :String) :Boolean{
    return try {
        val (_, response, _) = Fuel.get("http://localhost:8080/api/telegram?id=$telegramUserId")
            .responseString()

        //val (payload, _) = result // payload is a String
        //val responseJson = payload.toString()

        response.statusCode == 200
    } catch (exc :Exception) {
        false
    }
}

//Returns if the user is logged in
fun login(username :String, password :String, telegramUserId :String) :Boolean{
    return try {
        val (_, response, _) = Fuel.post("http://localhost:8080/api/telegram/login")
            .body(Gson().toJson(TelegramUser(telegramUserId, username, password)).toString())
            .responseString()

        response.statusCode == 200
    } catch (exc :Exception) {
        false
    }
}

//Returns if the logout was successful
fun logout(telegramUserId :String) :Boolean{
    return try {
        val (_, response, _) = Fuel.post("http://localhost:8080/api/telegram/logout")
            .body(Gson().toJson(TelegramUser(telegramUserId, null, null)).toString())
            .responseString()

        response.statusCode == 200
    } catch (exc :Exception) {
        false
    }
}

//Returns the coutry lists from the logged in user
fun countryLists(telegramUserId :String) :List<CountriesList>?{
    return try {
        val (_, response, result) = Fuel.get("http://localhost:8080/api/telegram/countries?id=$telegramUserId")
            .responseString()

        val (payload, _) = result // payload is a String
        val responseJson = payload.toString()

        when (response.statusCode) {
            200 -> Gson().fromJson(responseJson, Array<CountriesList>::class.java).asList()
            else -> emptyList()
        }
    } catch (exc :Exception) {
        null
    }
}

//Returns all countries
fun allCountries() :Array<Country>?{
    return try {
        val (_, response, result) = Fuel.get("http://localhost:8080/api/countries/").responseString()

        val (payload, _) = result // payload is a String
        val responseJson = payload.toString()

        when (response.statusCode) {
            200 -> Gson().fromJson(responseJson, Array<Country>::class.java)
            else -> arrayOf()
        }
    } catch (exc :Exception) {
        null
    }
}