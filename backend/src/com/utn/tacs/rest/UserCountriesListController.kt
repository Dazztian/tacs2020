package com.utn.tacs.rest

import com.utn.tacs.UserCountriesList
import com.utn.tacs.UserCountriesListModificationRequest
import com.utn.tacs.lists.UserListsRepository
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import org.litote.kmongo.toId

fun Application.userCountriesListRoutes(userListsRepository: UserListsRepository) {
    routing {
        route("/api/user/{userId}/countries") {
            get {
                val userId: String = call.parameters["userId"]!!.toString()
                call.respond(userListsRepository.getUserLists(userId))
            }
            post {
                val userId: String = call.parameters["userId"]!!.toString()
                val request = call.receive<UserCountriesList>()
                val response = userListsRepository.createUserList(UserCountriesList(userId.toId(), request.name, request.countries))
                if (response != null) {
                    call.respond(HttpStatusCode.Created, response.toString())
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
        route("/api/user/{userId}/countries/list/{name}") {
            get {
                val userId: String = call.parameters["userId"]!!.toString()
                val listName: String = call.parameters["name"].toString()

                val response = userListsRepository.getUserList(userId, listName)
                if (response != null) {
                    call.respond(response)
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }
            }
            delete {
                val userId: String = call.parameters["userId"]!!.toString()
                val listName: String = call.parameters["name"].toString()
                val response = userListsRepository.delete(userId, listName)

                if (response != null && response) {
                    call.respond(HttpStatusCode.Accepted)
                } else if (response != null && !response) {
                    call.respond(HttpStatusCode.NotModified)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            //Adds new countries to one specific list.
            patch {
                val userId: String = call.parameters["userId"]!!.toString()
                val listName: String = call.parameters["name"].toString()

                val request = call.receive<UserCountriesListModificationRequest>()

                val response = userListsRepository.update(userId, listName, request.name, request.countries)

                if (response != null) {
                    call.respond(HttpStatusCode.Accepted, response)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
        route("/api/user/{userId}/countries/list/table/{name}") {
            get {
                call.respondText("Envia los datos e/m/r para una lista de paises");
            }
        }
    }
}