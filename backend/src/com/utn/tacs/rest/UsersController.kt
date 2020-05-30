package com.utn.tacs.rest

import com.utn.tacs.SignUpRequest
import com.utn.tacs.User
import com.utn.tacs.account.AccountService
import com.utn.tacs.exception.UnAuthorizedException
import com.utn.tacs.exception.UserAlreadyExistsException
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.rest.authorizeUser
import com.utn.tacs.user.UsersService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.request.header
import io.ktor.routing.*

fun Application.users(usersService: UsersService) {
    routing {
        route("api/user") {
            get("/{id}") {
                val userId: String = call.parameters["id"].toString()
                try {
                    authorizeUser(call.request.header("Authorization") ?: "", userId)
                    call.respond(usersService.getUser(userId))
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
            post {
                try {
                    authorizeUserAdmin(call.request.header("Authorization") ?: "")
                    val signUpData = call.receive<SignUpRequest>()
                    call.respond(usersService.createUser(SignUpRequest(
                            signUpData.name.trim().toLowerCase(),
                            signUpData.email.trim().toLowerCase(),
                            signUpData.password.trim(),
                            signUpData.country.trim().toUpperCase(),
                            signUpData.isAdmin ?: false
                    )))
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: UserAlreadyExistsException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}