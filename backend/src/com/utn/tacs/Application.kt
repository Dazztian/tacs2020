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
import io.ktor.features.CORS
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import io.ktor.request.receive
import io.ktor.request.receiveText


//Changed the package to work with intellij.
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(CORS) {
        method(HttpMethod.Post)
        method(HttpMethod.Get)
        header(HttpHeaders.Accept)
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
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
    createUser()
    login()
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

fun Application.createUser() {
    routing {
        route("/createUser"){
            get {
                val response = getUserFromDatabase("juan")
                call.respond(response)
            }
            post {
                //Recibe el body en json como string
                val text: String = call.receiveText()

                val json = Json(JsonConfiguration.Stable)
                val output = json.parse(User.serializer(),text)

                call.respond(createUser(output));
            }
        }
    }
}

fun Application.login() {
    routing {
        route("/login") {
            get {
                //Recibe el body en json como string
                val text: String = call.receiveText()

                val json = Json(JsonConfiguration.Stable)
                val output = json.parse(User.serializer(),text)

                val response = getUserFromDatabase(output.name)
                call.respond(response)
            }
        }
    }
}