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
import io.ktor.auth.OAuthServerSettings
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
import io.ktor.auth.oauth
import io.ktor.client.HttpClient
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
val authorizationService = AuthorizationService(usersRepository, usersService)
val countriesService = CountriesService(CountriesRepository(MongoClientGenerator.getDataBase(), CovidExternalClient))

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

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
        oauth("google-oauth") {
            client = HttpClient()
            providerLookup = { googleOauthProvider }
            urlProvider = { "http://localhost:8080/api/google" }
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
    login(authorizationService, usersService)
    adminReports(AdminReportsService(usersRepository, userListsRepository))
    telegram(usersRepository, userListsRepository, TelegramRepository(MongoClientGenerator.getDataBase()), usersService, countriesService)
}

//Define a call for when using authorization
val ApplicationCall.user get() = authentication.principal<User>()


val googleOauthProvider = OAuthServerSettings.OAuth2ServerSettings(
        name = "google",
        authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
        accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
        requestMethod = HttpMethod.Post,

        clientId = "850038158644-32c2v3i19hur7v95ttbnlaq5qi49b85e.apps.googleusercontent.com",
        clientSecret = "",
        defaultScopes = listOf("profile", "email")
)
