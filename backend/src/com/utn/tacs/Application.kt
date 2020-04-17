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
        route("/countries") {
            get {
                val response = getExternalData()
                call.respond(response)
            }
        }
        get("/") {
                call.respondText("Hello World!")
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
    }
}

fun Application.database() {
    routing {
        route("/database"){
            get {
                val response = getCountriesFromDatabase()
                call.respond(response)
            }
        }
    }
}