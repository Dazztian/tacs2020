package com.utn.tacs.rest

import com.utn.tacs.User
import com.utn.tacs.account.AccountService
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.rest.getUserByAuthenticationHeader
import com.utn.tacs.user.UsersService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.request.header

fun Application.users(usersService: UsersService) {
    routing {
        route("api/user") {
            get("/{id}") {
                val userId: String = call.parameters["id"].toString()
                try {
                    val user: User = getUserByAuthenticationHeader(call.request.header("Authorization") ?: "") ?: throw Exception()
                    if (!userId.equals(user._id.toString())) {
                        throw Exception()
                    }

                    call.respond(usersService.getUser(userId) ?: HttpStatusCode.NotFound)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}