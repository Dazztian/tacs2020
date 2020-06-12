package com.utn.tacs.rest

import com.utn.tacs.LoginRequest
import com.utn.tacs.LoginResponse
import com.utn.tacs.SignUpRequest
import com.utn.tacs.auth.AuthorizationService
import com.utn.tacs.auth.JwtConfig
import com.utn.tacs.exception.UnauthorizedException
import com.utn.tacs.exception.UserAlreadyExistsException
import com.utn.tacs.usersService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.login(authorizationService: AuthorizationService) {
    routing {
        route("/api/login") {
            post {
                try {
                    val loginData = call.receive<LoginRequest>()
                    val user = authorizationService.auth(loginData.email, loginData.password)
                    call.respond(LoginResponse(user, usersService.getUserLists(user._id.toString()), JwtConfig.makeToken(user)))
                } catch (e: UnauthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
        route("/api/signup") {
            post {
                try {
                    val signUpData = call.receive<SignUpRequest>()
                    val user = authorizationService.signUp(SignUpRequest(
                            signUpData.name.trim().toLowerCase(),
                            signUpData.email.trim().toLowerCase(),
                            signUpData.password.trim(),
                            signUpData.country.trim().toUpperCase(),
                            false
                    ))
                    call.respond(LoginResponse(user, usersService.getUserLists(user._id.toString()), JwtConfig.makeToken(user)))
                } catch (e: UserAlreadyExistsException) {
                    call.respond(HttpStatusCode.BadRequest.description(e.message ?: ""))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
        route("/auth/google") {
            post {
                call.respondText("Oauth")
            }
        }
    }
}