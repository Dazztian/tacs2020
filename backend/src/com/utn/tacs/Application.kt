package com.utn.tacs

import com.utn.tacs.rest.*
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.routing


//Changed the package to work with intellij.
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(CORS) {
        method(HttpMethod.Post)
        method(HttpMethod.Get)
        header(HttpHeaders.Accept)
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
    install(ContentNegotiation) {
        gson {
        }
    }
    install(StatusPages) {
        exception<Throwable> { cause ->
            val error = HttpBinError(code = HttpStatusCode.InternalServerError, request = call.request.local.uri, message = cause.toString(), cause = cause)
            call.respond(error)
        }
    }

    routes()
}

fun Application.routes() {
    //TODO verificar esto, no se bien donde colocar la base para poder usarla en los tests.
    routing {
        healthCheckRoutes()
        countriesRoutes()
        userCountriesListRoutes()
        users()
        login()
    }
}