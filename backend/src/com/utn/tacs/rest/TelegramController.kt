package com.utn.tacs.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.telegram.TelegramRepository
import com.utn.tacs.TelegramUser
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.UserCountriesListModificationRequest
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.user.UsersService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.*
import org.litote.kmongo.Id

fun Application.telegram(usersRepository: UsersRepository, userListsRepository: UserListsRepository, telegramRepository: TelegramRepository, usersService: UsersService) {
    routing {
        route("/api/telegram") {
            get {
                val telegramUser = TelegramUser(call.parameters["telegramId"]!!, null, null)

                call.respond(telegramRepository.getTelegramSession(telegramUser) ?: HttpStatusCode.NotFound)
            }

            post("/login") {
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
            post("/logout") {
                val telegramUser: TelegramUser = jacksonObjectMapper().readValue(call.receiveText())

                when (telegramRepository.getTelegramSession(telegramUser)) {
                    null -> call.respond(HttpStatusCode(404, "Session not found"))
                    else -> call.respond(telegramRepository.deleteTelegramSession(telegramUser))
                }
            }

            get("/user") {
                when(val session = telegramRepository.getUserId(call.parameters["id"]!!)){
                    null -> call.respond(HttpStatusCode(400, "Id not found"))
                    else -> call.respond(usersRepository.getUserById(session.userId) ?: HttpStatusCode.NotFound)
                }
            }

            route("/countryList"){
                get {
                    when(val session = telegramRepository.getUserId(call.parameters["telegramId"]!!)){
                        null -> call.respond(HttpStatusCode(400, "Id not found"))
                        else -> {
                            val userCountryLists = userListsRepository.getUserLists(session.userId.toString())
                            //TODO: ARREGLAR ESTO
                            call.respond(userCountryLists.map { UserCountriesListMiniFix(it._id, it.name) })
                        }
                    }
                }
                get("/{listId}") {
                    when(val countryList = userListsRepository.getUserList(call.parameters["listId"].toString())){
                        null -> call.respond(HttpStatusCode(400, "Id not found"))
                        else -> call.respond(UserCountriesListMiniFix(countryList))
                    }
                }
                post {
                    when(val sessionId = telegramRepository.getUserId(call.parameters["sessionId"]!!)){
                        null -> call.respond(HttpStatusCode(400, "Id not found"))
                        else -> {
                            val requestJson = call.receive<UserCountriesListModificationRequest>()
                            try{
                                call.respond(usersService.createUserList(sessionId.userId.toString(), requestJson.name!!, requestJson.countries!!))
                            }catch (exc :Exception){
                                call.respond(HttpStatusCode(500, "Exception"), exc.message.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}


//TODO: ARREGLAR ESTO
class UserCountriesListMiniFix(
        val _id: Id<UserCountriesList>,
        val name: String,
        val countries: Set<String>){
    constructor(userCountriesList: UserCountriesList) : this(userCountriesList._id, userCountriesList.name, userCountriesList.countries.toSet())
    constructor(id :Id<UserCountriesList>, name :String) : this(id, name, emptySet())
}