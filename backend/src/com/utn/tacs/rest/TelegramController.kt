package com.utn.tacs.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.telegram.TelegramRepository
import com.utn.tacs.TelegramUser
import com.utn.tacs.user.UsersRepository
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.*

fun Application.telegram(usersRepository: UsersRepository, telegramRepository: TelegramRepository) {
    routing {
        route("/api/telegram") {
            get {
                val telegramUser = TelegramUser(call.parameters["id"]!!, null, null)

                call.respond(telegramRepository.getTelegramSession(telegramUser) ?: HttpStatusCode.NotFound)
            }
            post {
                val telegramUser: TelegramUser = jacksonObjectMapper().readValue(call.receiveText())
                if(telegramUser.username == null || telegramUser.password == null) {
                    call.respond(HttpStatusCode(400, "username and password can't be null"))
                }else{
                    val user = usersRepository.getUserByEmailAndPass(telegramUser.username, telegramUser.password)

                    when {
                        user == null -> call.respond(HttpStatusCode(401, "User not found"))
                        telegramRepository.getTelegramSession(telegramUser) != null -> call.respond(HttpStatusCode(402, "Telegram User already logged on"))
                        else -> call.respond(telegramRepository.createNewTelegramSession(user, telegramUser) ?: HttpStatusCode.Conflict)
                    }
                }
            }
            delete {
                val telegramUser: TelegramUser = jacksonObjectMapper().readValue(call.receiveText())

                when {
                    telegramRepository.getTelegramSession(telegramUser) == null -> call.respond(HttpStatusCode(400, "Session not found"))
                    else -> call.respond(telegramRepository.deleteTelegramSession(telegramUser))
                }
            }
        }
    }
}