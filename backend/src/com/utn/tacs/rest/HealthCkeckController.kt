package com.utn.tacs.rest

import io.ktor.application.call
import io.ktor.routing.*
import io.ktor.response.*

fun Route.healthCheckRoutes() {
    route("/") {
        get {
            call.respondText("Application running")
        }
    }
}