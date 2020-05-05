package com.utn.tacs.rest

import com.mongodb.MongoClient
import com.utn.tacs.UserCountriesList
import com.utn.tacs.lists.UserListsRepository
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*


val mongoClient = MongoClient("localhost", 27017)
val userListsRepository = UserListsRepository(mongoClient, "tacs")

fun Route.userCountriesListRoutes() {

    route("/api/user/countries/{userId}") {
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
    route("/api/user/countries/list/table/{idList}") {
        get {
            call.respondText("Envia los datos e/m/r para una lista de paises");
        }
    }
    route("/api/user/countries/list/{userId}/{idList}") {
        get {
            val userId: Int = call.parameters["userId"]!!.toInt()
            val listName: String = call.parameters["idList"].toString()
            call.respond(userListsRepository.getUserLists(userId, listName))
        }

        delete {
            call.respondText("Borra una lista del usuario");
        }
        patch {
            call.respondText("Modifica una lista del usuario");
        }
    }
}