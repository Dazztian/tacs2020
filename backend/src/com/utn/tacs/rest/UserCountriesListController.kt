package com.utn.tacs.rest

import com.utn.tacs.UserCountriesList
import com.utn.tacs.lists.UserListsRepository
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*

fun Application.userCountriesListRoutes(userListsRepository: UserListsRepository) {
    routing {
        route("/api/user/{userId}/countries") {
            get {
                val userId: Int = call.parameters["userId"]!!.toInt()
                call.respond(userListsRepository.getUserLists(userId))
            }
            post {
                val userId: Int = call.parameters["userId"]!!.toInt()
                val request = call.receive<UserCountriesList>()
                userListsRepository.createUserList(userId, request.name, request.countries)
                call.respond(HttpStatusCode.OK)
            }
        }
        route("/api/user/{userId}/countries/list/table/{idList}") {
            get {
                call.respondText("Envia los datos e/m/r para una lista de paises");
            }
        }
        route("/api/user/{userId}/countries/list/{idList}") {
            get {
                val userId: Int = call.parameters["userId"]!!.toInt()
                val listName: String = call.parameters["idList"].toString()
                call.respond(userListsRepository.getUserLists(userId, listName))
            }
            delete {
                call.respondText("Borra una lista del usuario")
            }
            patch {
                call.respondText("Modifica una lista del usuario")
            }
        }
    }
}