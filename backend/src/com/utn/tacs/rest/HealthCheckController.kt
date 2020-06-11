package com.utn.tacs.rest

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.healthCheckRoutes() {
    routing {
        route("/configuration") {
            get {
                call.respondText("Application running")
            }
        }
    }
}