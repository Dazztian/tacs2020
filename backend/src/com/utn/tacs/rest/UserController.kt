package com.utn.tacs.rest

import io.ktor.application.call
import io.ktor.routing.*
import io.ktor.response.*
import com.utn.tacs.*
import com.utn.tacs.user.*


fun Route.userRoutes() {
    route("/api/user/countries") {
        get {
            val userId = 1
            call.respond(getUserCountriesList(userId))
        }
        post {
            call.respondText("Guarda una nueva listas del usuario")
        }
    }
    route("/api/user/countries/list/{idList}") {
        get{
            val listName: String = call.parameters["idList"].toString()
            val userId = 1
            call.respond(getUserCountriesList(userId,listName))
        }
        delete {
            call.respondText("Borra una lista del usuario");
        }
        patch {
            call.respondText("Modifica una lista del usuario");
        }
    }
    route("/api/user/countries/list/{idList}/table") {
        get {
            call.respondText("Envia los datos e/m/r para una lista de paises");
        }
    }
}