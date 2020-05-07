package com.utn.tacs.rest

import com.utn.tacs.User
import com.utn.tacs.user.getUserFromDatabase
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


fun Application.login() {
    routing {
        route("/api/login") {
            get {
                //Recibe el body en json como string
                val text: String = call.receiveText()

                val json = Json(JsonConfiguration.Stable)
                val output = json.parse(User.serializer(), text)

                val response = getUserFromDatabase(output.name)
                call.respond(response ?: HttpStatusCode.NotFound)
            }
            post {
                call.respondText("login")
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