package com.utn.tacs.rest

import com.utn.tacs.User
import com.utn.tacs.user.UsersRepository
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

fun Application.users(usersRepository: UsersRepository) {
    routing {
        route("api/user") {
            get("/{id}") {
                val userId: String = call.parameters["id"]!!
                call.respond(usersRepository.getUserById(userId) ?: HttpStatusCode.NotFound)
            }
            post {
                //Recibe el body en json como string
                val text: String = call.receiveText()
                val json = Json(JsonConfiguration.Stable)
                val output = json.parse(User.serializer(), text)
                call.respond(usersRepository.createUser(output) ?: HttpStatusCode.NotFound)
            }
        }
    }
}