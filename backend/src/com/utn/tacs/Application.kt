package com.utn.tacs

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import com.utn.tacs.user.*

//Changed the package to work with intellij.
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        gson {
        }
    }
    install(StatusPages) {
        exception<Throwable> { cause ->
            val error = HttpBinError(code = HttpStatusCode.InternalServerError, request = call.request.local.uri, message = cause.toString(), cause = cause)
            call.respond(error)
        }
    }
    countries()
    database()
}

fun Application.countries() {
    routing {
        get("/") {
            call.respondText("Application running")
        }

        get("/api/tree") {
            call.respond(getCountriesLatest())
        }

        route("/api/countries") {
            get {
                val lat = call.request.queryParameters["lat"]?.toDouble()
                val lon = call.request.queryParameters["lon"]?.toDouble()
                if (lat != null && lon != null) {
                    call.respond(getNearestCountries(lat, lon));
                } else {
                    call.respond(getAllCountries());
                }
            }
        }
        //Returns a country latest information based on iso2 code.
        route("/api/countries/{iso2}") {
            get {
                val iso2: String = call.parameters["iso2"].toString()
                call.respond(getCountryLatestByIsoCode(iso2.toUpperCase()))
            }
        }
        route("/api/user/countries/list") {
            get {
                val userId = 1
                call.respond(getUserCountriesList(userId))
            }
            post {
                call.respondText("Guarda una nueva listas del usuario")
            }
        }
        route("/api/countries/list/{idList}") {
            delete {
                call.respondText("Borra una lista del usuario");
            }
            patch {
                call.respondText("Modifica una lista del usuario");
            }
        }
        route("/api/countries/list/{idList}/table") {
            get {
                call.respondText("Envia los datos e/m/r para una lista de paises");
            }
        }
    }
}

fun Application.database() {
    routing {
        route("/register") {
            post {
                call.respondText("register");
            }
        }
        route("/login") {
            post {
                call.respondText("login");
            }
        }
        route("/auth/google") {
            post {
                call.respondText("Oauth");
            }
        }
        route("/logout") {
            get {
                call.respondText("logout");
            }

        }
        route("/database") {
            get {
                val response = getCountriesFromDatabase()
                call.respond(response)
            }
        }
    }
}