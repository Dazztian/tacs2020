package com.utn.tacs

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.*
import io.ktor.routing.*
import com.utn.tacs.user.*
import com.utn.tacs.rest.*

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

    routes()
}

fun Application.routes() {
    routing {
        countriesRoutes()
        healthCheckRoutes()
        userRoutes()
        database()
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
                call.respondText("logout")
            }

        }
    }
}