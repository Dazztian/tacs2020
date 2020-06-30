package com.utn.tacs

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.utn.tacs.auth.AuthorizationService
import com.utn.tacs.auth.JwtConfig
import com.utn.tacs.countries.CountriesRepository
import com.utn.tacs.countries.CountriesService
import com.utn.tacs.exception.exceptionHandler
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.reports.AdminReportsService
import com.utn.tacs.rest.*
import com.utn.tacs.telegram.TelegramRepository
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.user.UsersService
import com.utn.tacs.utils.MongoClientGenerator
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.OAuthServerSettings
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
import io.ktor.auth.oauth
import io.ktor.client.HttpClient
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.litote.kmongo.id.jackson.IdJacksonModule

fun main(args: Array<String>) {

    //Initialize everything with the correct mongo url
    //This is done here to be allowed to change the db on executing the app, and not having it tied up to the config file
    val mongoDb = args[0]
    val mongoUrl = args[1]
    val mongoPort = args[2]

    MongoClientGenerator.setProperties(mongoDb, mongoUrl, mongoPort.toInt())

    embeddedServer(Netty, 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    val usersRepository = UsersRepository(MongoClientGenerator.getDataBase())

    install(DefaultHeaders)
    install(CORS) {
        method(HttpMethod.Post)
        method(HttpMethod.Get)
        method(HttpMethod.Delete)
        method(HttpMethod.Put)
        header(HttpHeaders.Accept)
        header("Authorization")
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
    install(CallLogging)
    authentication(usersRepository)
    contentNegotiator()
    exceptionHandler()
    routes(usersRepository)
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

fun Application.routes(usersRepository: UsersRepository) {
    val userListsRepository = UserListsRepository(MongoClientGenerator.getDataBase(), usersRepository)
    val usersService = UsersService(usersRepository, userListsRepository)
    val authorizationService = AuthorizationService(usersRepository, usersService)
    val countriesService = CountriesService(CountriesRepository(MongoClientGenerator.getDataBase(), CovidExternalClient))


    healthCheckRoutes()
    countriesRoutes(countriesService)
    userCountriesListRoutes(usersService)
    users(usersService)
    login(authorizationService, usersService)
    adminReports(AdminReportsService(usersRepository, userListsRepository))
    telegram(usersRepository, userListsRepository, TelegramRepository(MongoClientGenerator.getDataBase(), userListsRepository), usersService, countriesService)
}

//Define a call for when using authorization
val ApplicationCall.user get() = authentication.principal<User>()
