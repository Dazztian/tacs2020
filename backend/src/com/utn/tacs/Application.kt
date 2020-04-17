package com.utn.tacs

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.features.*
import io.ktor.gson.gson

//Changed the package to work with intellij.
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        gson {
        }
    }
    countries()
    database()
}

fun Application.countries() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        route("/countries") {
            get {
                val response = getExternalData()
                call.respond(response)
            }
        }
        route("/api/countries") {
            get {
                val lat = call.request.queryParameters["lat"]?.toDouble()
                val lon = call.request.queryParameters["lon"]?.toDouble()
                if(lat != null && lon != null){
                    call.respond(getNearestCountries(lat,lon)); 
                }else{
                    call.respond(getAllCountries()); 
                }
            }
        }
        route("/api/countries/list") {
            get{
                call.respondText("Retornal las listas del usuario");                 
            }
            post{
                call.respondText("Guarda una nueva listas del usuario");       
            }
        }
        route("/api/countries/list/{idList}") {
            delete{
                call.respondText("Borra una lista del usuario"); 
            }
            patch{
                call.respondText("Modifica una lista del usuario"); 
            }
        }
        route("/api/countries/list/{idList}/table"){
            get{
                call.respondText("Envia los datos e/m/r para una lista de paises"); 
            }
        }
    }
}

fun Application.database() {
    routing {
        route("/register"){
            post{
                call.respondText("register");
            }
        }
        route("/login"){
            post{
                call.respondText("login");
            }
        }
        route("/auth/google"){
            post{
                call.respondText("Oauth");
            }
        }
        route("/logout"){
                call.respondText("logout");
        }
        route("/database"){
            get {
                val response = getCountriesFromDatabase()
                call.respond(response)
            }
        }
    }
}