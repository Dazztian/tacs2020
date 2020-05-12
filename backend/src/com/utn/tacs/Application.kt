package com.utn.tacs

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature

import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.rest.*
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.utils.MongoClientGenerator
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import org.litote.kmongo.id.jackson.IdJacksonModule


//Changed the package to work with intellij.
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {


    install(DefaultHeaders)
    install(CORS) {
        method(HttpMethod.Post)
        method(HttpMethod.Get)
        header(HttpHeaders.Accept)
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }

    contentNegotiator()

    install(StatusPages) {
        exception<Throwable> { cause ->
            val error = HttpBinError(code = HttpStatusCode.InternalServerError, request = call.request.local.uri, message = cause.toString(), cause = cause)
            call.respond(error)
        }
    }


    routes()
}

fun Application.contentNegotiator(){
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter())
            registerModule(IdJacksonModule())

        }
    }
}
fun Application.routes() {
    healthCheckRoutes()
    countriesRoutes()
    userCountriesListRoutes(UserListsRepository(MongoClientGenerator.getDataBase()))
    users(UsersRepository(MongoClientGenerator.getDataBase()))
    login(UsersRepository(MongoClientGenerator.getDataBase()))
}