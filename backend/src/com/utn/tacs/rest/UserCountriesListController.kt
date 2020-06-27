package com.utn.tacs.rest

import com.fasterxml.jackson.core.JsonParseException
import com.utn.tacs.UserCountriesListModificationRequest
import com.utn.tacs.user.UsersService
import com.utn.tacs.utils.getLogger
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.features.BadRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

val logger = getLogger()

fun Application.userCountriesListRoutes(usersService: UsersService) {
    routing {
        authenticate {
            route("/api/user/{userId}/lists") {
                get {
                    val userId: String = call.parameters["userId"]!!.toString()
                    call.respond(usersService.getUserLists(userId))
                }
                post {
                    val userId: String = call.parameters["userId"]!!.toString()
                    try {
                        val request = call.receive<UserCountriesListModificationRequest>()
                        call.respond(usersService.createUserList(userId, request.name, request.countries))
                    } catch (e: JsonParseException) {
                        throw BadRequestException(e.localizedMessage)
                    }
                }
            }
            route("/api/user/{userId}/lists/{listId}") {
                get {
                    val userId: String = call.parameters["userId"]!!.toString()
                    val listId: String = call.parameters["listId"]!!.toString()
                    call.respond(usersService.getUserList(userId, listId))
                }
                delete {
                    val userId: String = call.parameters["userId"]!!.toString()
                    val listId: String = call.parameters["listId"]!!.toString()
                    usersService.deleteUserList(userId, listId)
                    call.respond(HttpStatusCode.Accepted)
                }
                put {
                    val userId: String = call.parameters["userId"]!!.toString()
                    val listId: String = call.parameters["listId"]!!.toString()
                    try {
                        val request = call.receive<UserCountriesListModificationRequest>()
                        call.respond(usersService.updateUserList(userId, listId, request))
                    } catch (e: JsonParseException) {
                        throw BadRequestException(e.localizedMessage)
                    }
                }
            }
        }
    }
}