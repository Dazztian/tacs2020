package com.utn.tacs

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.utn.tacs.account.AccountRepository
import com.utn.tacs.account.AccountService
import com.utn.tacs.countries.CountriesRepository
import com.utn.tacs.countries.CountriesService

import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.reports.AdminReportsService
import com.utn.tacs.rest.*
import com.utn.tacs.telegram.TelegramRepository
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.user.UsersService
import com.utn.tacs.utils.MongoClientGenerator
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.util.pipeline.PipelineInterceptor
import io.ktor.util.pipeline.PipelinePhase
import org.litote.kmongo.id.jackson.IdJacksonModule

val usersRepository = UsersRepository(MongoClientGenerator.getDataBase())
val userListsRepository = UserListsRepository(MongoClientGenerator.getDataBase())
val usersService = UsersService(usersRepository, userListsRepository)
val accountService = AccountService(usersRepository, AccountRepository(MongoClientGenerator.getDataBase()), usersService)

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

fun Application.contentNegotiator() {
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
    countriesRoutes(CountriesService(CountriesRepository(MongoClientGenerator.getDataBase())))
    userCountriesListRoutes(usersService)
    users(usersService)
    login(accountService)
    adminReports(AdminReportsService(UsersRepository(MongoClientGenerator.getDataBase()), UserListsRepository(MongoClientGenerator.getDataBase())))
    telegram(usersRepository, userListsRepository, TelegramRepository(MongoClientGenerator.getDataBase()))
}