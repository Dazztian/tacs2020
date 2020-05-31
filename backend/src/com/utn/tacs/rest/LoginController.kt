package com.utn.tacs.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.LogOutRequest
import com.utn.tacs.User
import com.utn.tacs.LoginRequest
import com.utn.tacs.SignUpRequest
import com.utn.tacs.account.AccountService
import com.utn.tacs.exception.UnAuthorizedException
import com.utn.tacs.exception.UserAlreadyExistsException
import com.utn.tacs.user.UsersRepository
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.features.NotFoundException
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

fun Application.login(accountService: AccountService) {
    routing {
        route("/api/login") {
            post {
                try {
                    val loginData = call.receive<LoginRequest>()
                    call.respond(accountService.logIn(loginData))
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
        route("/api/signup") {
            post {
                try {
                    val signUpData = call.receive<SignUpRequest>()
                    call.respond(accountService.signUp(SignUpRequest(
                        signUpData.name.trim().toLowerCase(),
                        signUpData.email.trim().toLowerCase(),
                        signUpData.password.trim(),
                        signUpData.country.trim().toUpperCase(),
                        false
                    )))
                } catch (e: UserAlreadyExistsException) {
                    call.respond(HttpStatusCode.BadRequest.description(e.message ?: ""))
                }
            }
        }
        route("/auth/google") {
            post {
                call.respondText("Oauth")
            }
        }
        route("api/logout") {
            post {
                try {
                    val authHeader = call.request.header("Authorization") ?: ""
                    val user = authorizeUser(authHeader)
                    accountService.logOut(LogOutRequest(getToken(authHeader)))
                    call.respond(HttpStatusCode.OK)
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}