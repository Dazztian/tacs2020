package com.utn.tacs.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.User
import com.utn.tacs.user.UsersRepository
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


fun Application.login(usersRepository: UsersRepository) {
    routing {
        route("/api/login") {
            get {
                //Recibe el body en json como string
                val text: String = call.receiveText()

                val output: User = jacksonObjectMapper().readValue(text)

                val response = usersRepository.getUserByName(output.name)
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