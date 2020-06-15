package com.utn.tacs.rest

import com.utn.tacs.SignUpRequest
import com.utn.tacs.UserResponse
import com.utn.tacs.user.UsersService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Application.users(usersService: UsersService) {
    routing {
        authenticate {
            route("api/user") {
                get("/{id}") {
                    val userId: String = call.parameters["id"].toString()
                    call.respond(UserResponse(usersService.getUser(userId), usersService.getUserLists(userId)))
                }
                post {
                    val signUpData = call.receive<SignUpRequest>()
                    val user = usersService.createUser(SignUpRequest(
                            signUpData.name.trim().toLowerCase(),
                            signUpData.email.trim().toLowerCase(),
                            signUpData.password.trim(),
                            signUpData.country.trim().toUpperCase(),
                            signUpData.isAdmin ?: false
                    ))
                    call.respond(UserResponse(user, usersService.getUserLists(user._id.toString())))

                }
                delete("/{id}") {
                    val userId: String = call.parameters["id"].toString()
                    usersService.deleteUser(userId)
                    call.respond(HttpStatusCode.Accepted)
                }
            }
        }
    }
}