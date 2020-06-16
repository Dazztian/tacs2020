package com.utn.tacs.exception

import com.fasterxml.jackson.core.JsonParseException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.BadRequestException
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

fun Application.exceptionHandler() {
    install(io.ktor.features.StatusPages) {
        exception<JsonParseException> { cause ->
            log.error("Exception $cause")
            val error = HttpBinError(code = HttpStatusCode.BadRequest, request = call.request.local.uri, message = cause.toString())
            call.respond(HttpStatusCode.BadRequest, error)
        }
        exception<UserAlreadyExistsException> { cause ->
            log.error("Exception $cause")
            val error = HttpBinError(code = HttpStatusCode.BadRequest, request = call.request.local.uri, message = cause.toString())
            call.respond(HttpStatusCode.BadRequest, error)
        }
        exception<UnauthorizedException> { cause ->
            log.error("Exception $cause")
            val error = HttpBinError(code = HttpStatusCode.Unauthorized, request = call.request.local.uri, message = cause.toString())
            call.respond(HttpStatusCode.Unauthorized, error)
        }
        exception<BadRequestException> { cause ->
            log.error("Exception $cause")
            val error = HttpBinError(code = HttpStatusCode.BadRequest, request = call.request.local.uri, message = cause.toString())
            call.respond(HttpStatusCode.BadRequest, error)
        }
        exception<IllegalArgumentException> { cause ->
            log.error("Exception $cause")
            val error = HttpBinError(code = HttpStatusCode.BadRequest, request = call.request.local.uri, message = cause.toString())
            call.respond(HttpStatusCode.BadRequest, error)
        }
        exception<NotFoundException> { cause ->
            log.error("Exception $cause")
            val error = HttpBinError(code = HttpStatusCode.NotFound, request = call.request.local.uri, message = cause.toString())
            call.respond(HttpStatusCode.NotFound, error)
        }
        exception<Throwable> { cause ->
            log.error("Exception $cause")
            val error = HttpBinError(code = HttpStatusCode.InternalServerError, request = call.request.local.uri, message = cause.toString())
            call.respond(HttpStatusCode.InternalServerError, error)
        }
    }
}