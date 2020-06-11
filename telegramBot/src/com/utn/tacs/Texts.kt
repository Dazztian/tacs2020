package com.utn.tacs

import com.github.kotlintelegrambot.entities.Update

fun loginText(name: String?) = startMessageBuilder(name ?: "errorName")
fun startMessageBuilder(firstName :String) =    "Welcome $firstName!  \uD83D\uDE04\n\n" +
        "To see your lists press \"My Lists\"\n" +
        "To see command info press \"Help\"\n" +
        "Remember to use telegram in horizonral mode for a better experience!"

const val helpText =    "- This bot maintains a registry of the coronavirus advances in the world\n" +
        "- To begin create an account in $urlBase\n" +
        "- To login write /login followed by your username and password\n" +
        "(Example: /login user pass)\n\n" +
        "- To see the last values of a country use /check {country}\n" +
        "- To see al countries use /countries\n\n" +
        "Tip: you don't need to capitalize the country or write the whole name\n" +
        "Example: /check arg brings Argentina info!"
const val startText =   "Welcome to the Group 4 Telegram Bot!  \uD83D\uDCBB\n\n" +
        "- To see the last values of a country use /check {country}\n" +
        "- To see al countries use /countries\n\n"+
        "- To manage your lists write /login followed by your username and password\n" +
        "(Example: /login user pass)\n\n" +
        "- To create an account go to $urlBase"

const val errorText = "Unknown error occurred"
const val badLogoutText = "Username or password incorrect   \uD83D\uDE15"
const val textoLoginHelp =  "To begin write /login followed by your username and password\n" +
        "(Example: /login user pass)"

const val myListsText = "Select one of the lists"
const val textNoLists = "This user has no lists"
const val textNoCountries = "This list has no countries"
const val createListText =  "To create a new list just send me the name of the list\n" +
        "Optionally you can also write the names of the countries in the list by writing them in a new line each\n\n" +
        "For example:\n\n" +
        "My new list!\nArgentina\nChile\nBrazil"
const val addCountryText =  "Send me a list of the countries you want to add to this list\n" +
        "Each country must be written on a new line and have the exact name from /countries\n\n" +
        "Example:\nArgentina\nBrazil\nChile"

const val countryNotFoundText = "Error: Country not found\n"+
                                "User /paises to check the name of the " +
                                "country you are trying to look"


const val textoServerCaido = "An error occurred while connecting to the server \uD83D\uDE1F"
const val textoUsuarioNoLogueado = "The current user is not logged in\n" +
        "To login write: \n" +
        "/login {usuario} {contraseña}"
const val textoUsuarioYaLogueado = "The current user is already logged in. \n" +
        "To change users write: \n" +
        "/logout"
const val textoArgumentsExpected =  "Error while trying to use a command without arguments\n" +
        "For help use /help"