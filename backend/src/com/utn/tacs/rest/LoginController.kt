package com.utn.tacs.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.User
import com.utn.tacs.LoginRequest
import com.utn.tacs.SignUpRequest
import com.utn.tacs.account.AccountService
import com.utn.tacs.user.UsersRepository
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

fun Application.login(usersRepository: UsersRepository, accountService: AccountService) {
    routing {
        route("/api/login") {
            post {
                val loginData = call.receive<LoginRequest>()
                call.respond(accountService.logIn(loginData) ?: HttpStatusCode.Unauthorized)
            }
        }
        route("/api/signup") {
            post {
                val signUpData = call.receive<SignUpRequest>()
                call.respond(accountService.signUp(SignUpRequest(
                    signUpData.name.trim().toLowerCase(),
                    signUpData.email.trim().toLowerCase(),
                    signUpData.password.trim(),
                    signUpData.country.trim().toLowerCase()
                )) ?: HttpStatusCode.BadRequest)
            }
        }
        route("/auth/google") {
            post {
                call.respondText("Oauth");
            }
        }
        route("/logout") {
            post {
                call.respondText("logout")
            }
        }
    }
}