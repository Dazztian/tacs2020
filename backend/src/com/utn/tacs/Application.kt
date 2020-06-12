package com.utn.tacs

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.utn.tacs.auth.AuthorizationService
import com.utn.tacs.auth.JwtConfig
import com.utn.tacs.countries.CountriesRepository
import com.utn.tacs.countries.CountriesService
import com.utn.tacs.exception.HttpBinError
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
import io.ktor.auth.Authentication
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Routing
import org.litote.kmongo.id.jackson.IdJacksonModule

val usersRepository = UsersRepository(MongoClientGenerator.getDataBase())
val userListsRepository = UserListsRepository(MongoClientGenerator.getDataBase(), usersRepository)
val usersService = UsersService(usersRepository, userListsRepository)
val accountService = AuthorizationService(usersRepository, usersService)
val countriesService = CountriesService(CountriesRepository(MongoClientGenerator.getDataBase(), CovidExternalClient))

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
    install(CallLogging)
    authentication(usersRepository)
    contentNegotiator()

    install(StatusPages) {
        exception<Throwable> { cause ->
            val error = HttpBinError(code = HttpStatusCode.InternalServerError, request = call.request.local.uri, message = cause.toString(), cause = cause)
            call.respond(error)
        }
    }

    routes()
}

/**
 * This is separated in a different method to be able to call it when testing controllers.
 * */
fun Application.authentication(usersRepository: UsersRepository) {
    install(Authentication) {
        /**
         * Setup the JWT authentication to be used in [Routing].
         * If the token is valid, the corresponding [User] is fetched from the database.
         * The [User] can then be accessed in each [ApplicationCall].
         */
        jwt {
            verifier(JwtConfig.verifier)
            realm = "tacs"
            validate {
                it.payload.getClaim("id").asString()?.let(usersRepository::getUserOrFail)
            }
        }
    }
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
    countriesRoutes(countriesService)
    userCountriesListRoutes(usersService)
    users(usersService)
    login(accountService)
    adminReports(AdminReportsService(usersRepository, userListsRepository))
    telegram(usersRepository, userListsRepository, TelegramRepository(MongoClientGenerator.getDataBase()), usersService, countriesService)
}

//Define a call for when using authorization
val ApplicationCall.user get() = authentication.principal<User>()
