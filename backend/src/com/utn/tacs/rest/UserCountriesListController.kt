package com.utn.tacs.rest

import com.utn.tacs.UserCountriesListModificationRequest
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.user.UsersService
import com.utn.tacs.utils.getLogger
import com.utn.tacs.exception.UnAuthorizedException
import com.utn.tacs.exception.UserAlreadyExistsException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.request.header
import io.ktor.response.respondText
import io.ktor.routing.*
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId
import org.litote.kmongo.toId

val logger = getLogger()

fun Application.userCountriesListRoutes(usersService: UsersService) {
    routing {
        route("/api/user/{userId}/lists") {
            get {
                try {
                    val userId: String = call.parameters["userId"]!!.toString()
                    authorizeUser(call.request.header("Authorization") ?: "", userId)
                    call.respond(usersService.getUserLists(userId))
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
            post {
                val userId: String = call.parameters["userId"]!!.toString()
                try {
                    authorizeUser(call.request.header("Authorization") ?: "", userId)
                    val request = call.receive<UserCountriesListModificationRequest>()
                    call.respond(usersService.createUserList(ObjectId(userId).toId(), request.name!!, request.countries!!) ?: HttpStatusCode.BadRequest)
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: UserAlreadyExistsException) {
                    call.respond(HttpStatusCode.BadRequest)
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
                    authorizeUser(call.request.header("Authorization") ?: "", userId)
                    call.respond(usersService.getUserList(userId, listId) ?: HttpStatusCode.BadRequest)
                } catch (e: UnAuthorizedException) {
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
                    authorizeUser(call.request.header("Authorization") ?: "", userId)
                    usersService.deleteUserList(userId, listId)
                    call.respond(HttpStatusCode.Accepted)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e : Exception) {
                    call.respond(HttpStatusCode.NotModified)
                }
            }
            patch {
                val userId: String = call.parameters["userId"]!!.toString()
                val listId: String = call.parameters["listId"]!!.toString()
                try {
                    authorizeUser(call.request.header("Authorization") ?: "", userId)
                    val request = call.receive<UserCountriesListModificationRequest>()
                    call.respond(usersService.updateUserList(userId, listId, request))
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: Exception) {
                    logger.error("Error parsing patch request", e)
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