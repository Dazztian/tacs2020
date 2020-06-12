package com.utn.tacs.rest

import com.utn.tacs.UserCountriesListModificationRequest
import com.utn.tacs.exception.UnauthorizedException
import com.utn.tacs.user.UsersService
import com.utn.tacs.utils.getLogger
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.features.NotFoundException
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
                    try {
                        val userId: String = call.parameters["userId"]!!.toString()
                        call.respond(usersService.getUserLists(userId))
                    } catch (e: NotFoundException) {
                        call.respond(HttpStatusCode.NotFound)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
                post {
                    val userId: String = call.parameters["userId"]!!.toString()
                    try {
                        val request = call.receive<UserCountriesListModificationRequest>()
                        call.respond(usersService.createUserList(userId, request.name, request.countries))
                    } catch (e: Exception) {
                        logger.error("Request could not be parsed...", e)
                        call.respond(HttpStatusCode.BadRequest, "Please check that body complies to { name: \"name\", countries: [\"countries\"]}")
                    }
                }
            }
            route("/api/user/{userId}/lists/{listId}") {
                get {
                    val userId: String = call.parameters["userId"]!!.toString()
                    val listId: String = call.parameters["listId"]!!.toString()
                    try {
                        call.respond(usersService.getUserList(userId, listId))
                    } catch (e: UnauthorizedException) {
                        call.respond(HttpStatusCode.Unauthorized)
                    } catch (e: NotFoundException) {
                        call.respond(HttpStatusCode.NotFound)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
                delete {
                    try {
                        val userId: String = call.parameters["userId"]!!.toString()
                        val listId: String = call.parameters["listId"]!!.toString()
                        usersService.deleteUserList(userId, listId)
                        call.respond(HttpStatusCode.Accepted)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.NotModified)
                    }
                }
                put {
                    val userId: String = call.parameters["userId"]!!.toString()
                    val listId: String = call.parameters["listId"]!!.toString()
                    try {
                        val request = call.receive<UserCountriesListModificationRequest>()
                        call.respond(usersService.updateUserList(userId, listId, request))
                    } catch (e: NotFoundException) {
                        call.respond(HttpStatusCode.NotFound)
                    } catch (e: UnauthorizedException) {
                        call.respond(HttpStatusCode.Unauthorized)
                    } catch (e: Exception) {
                        logger.error("Error parsing patch request", e)
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }
        }
    }
}