package com.utn.tacs.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.LoginRequest
import com.utn.tacs.LoginResponse
import com.utn.tacs.SignUpRequest
import com.utn.tacs.auth.AuthorizationService
import com.utn.tacs.auth.JwtConfig
import com.utn.tacs.user.UsersService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.features.NotFoundException
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.login(authorizationService: AuthorizationService, usersService: UsersService) {
    routing {
        route("/api/login") {
            post {
                val loginData = call.receive<LoginRequest>()
                val user = authorizationService.auth(loginData.email, loginData.password)
                call.respond(LoginResponse(user, usersService.getUserLists(user._id.toString()), JwtConfig.makeToken(user)))
            }
        }
        route("/api/signup") {
            post {
                val signUpData = call.receive<SignUpRequest>()
                val user = authorizationService.signUp(SignUpRequest(
                        signUpData.name.trim().toLowerCase(),
                        signUpData.email.trim().toLowerCase(),
                        signUpData.password.trim(),
                        signUpData.country.trim().toUpperCase(),
                        false
                ))
                call.respond(LoginResponse(user, usersService.getUserLists(user._id.toString()), JwtConfig.makeToken(user)))
            }
        }
        authenticate("google-oauth") {
            route("/api/google") {
                handle {
                    val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                            ?: error("No principal")

                    val json = HttpClient().get<String>("https://www.googleapis.com/userinfo/v2/me") {
                        header("Authorization", "Bearer ${principal.accessToken}")
                    }
                    val data = ObjectMapper().readValue<Map<String, Any?>>(json)
                    val email = data["email"] as String?

                    if (email != null) {
                        val user = usersService.getOrCreate(data)
                        call.respond(LoginResponse(user, usersService.getUserLists(user._id.toString()), JwtConfig.makeToken(user)))
                    } else {
                        throw NotFoundException("Could not find mail")
                    }
                }
            }
        }
    }
}