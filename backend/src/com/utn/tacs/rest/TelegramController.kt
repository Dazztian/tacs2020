package com.utn.tacs.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.utn.tacs.TelegramUser
import com.utn.tacs.UserCountriesListModificationRequest
import com.utn.tacs.UserCountriesListWrapper
import com.utn.tacs.countries.CountriesService
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.telegram.TelegramRepository
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.user.UsersService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.telegram(usersRepository: UsersRepository, userListsRepository: UserListsRepository,
                         telegramRepository: TelegramRepository, usersService: UsersService,
                         countriesService: CountriesService) {
    routing {
        route("/api/telegram") {
            //Gets the telegramSession of an telegramUser
            get {
                val telegramId = call.parameters["telegramId"] ?: return@get call.respond(HttpStatusCode.BadRequest)

                call.respond(telegramRepository.getTelegramSession(telegramId) ?: HttpStatusCode.NotFound)
            }

            post("/login") {
                val telegramUser: TelegramUser = jacksonObjectMapper().readValue(call.receiveText())
                if (telegramUser.username.isNullOrBlank() || telegramUser.password.isNullOrBlank()) {
                    call.respond(HttpStatusCode(400, "username and password can't be null"))
                } else {
                    when(telegramRepository.getTelegramSession(telegramUser.telegramId)) {
                        null -> {
                            val user = usersRepository.getUserByEmailAndPass(telegramUser.username, telegramUser.password)
                            call.respond(telegramRepository.createNewTelegramSession(user, telegramUser) ?: HttpStatusCode.Conflict)
                        }
                        else -> call.respond(HttpStatusCode(402, "Telegram User already logged on"))
                    }
                }
            }
            post("/logout") {
                val telegramUser: TelegramUser = jacksonObjectMapper().readValue(call.receiveText())

                when (telegramRepository.getTelegramSession(telegramUser.telegramId)) {
                    null -> call.respond(HttpStatusCode(404, "Session not found"))
                    else -> call.respond(telegramRepository.deleteTelegramSession(telegramUser.telegramId))
                }
            }

            route("/countryList") {
                //Gets all country lists from associated to the telegramId
                get {
                    when (val session = telegramRepository.getTelegramSession(call.parameters["telegramId"]
                            ?: return@get call.respond(HttpStatusCode.BadRequest))) {
                        null -> call.respond(HttpStatusCode(400, "Id not found"))
                        else -> {
                            val userCountryLists = userListsRepository.getUserLists(session.userId.toString())
                            call.respond(userCountryLists.map { UserCountriesListWrapper(it._id, it.name) })
                        }
                    }
                }
                route("/{listId}") {
                    //Gets the countries from a UserCountriesList
                    get {
                        val telegramId = call.parameters["telegramId"]
                                ?: return@get call.respond(HttpStatusCode.BadRequest)
                        val listId = call.parameters["listId"] ?: return@get call.respond(HttpStatusCode.BadRequest)

                        if (!telegramRepository.authenticated(telegramId, listId))
                            call.respond(HttpStatusCode(400, "Id not found"))
                        else {
                            val countryList = userListsRepository.getUserList(listId)
                            if (countryList!!.countries.isEmpty())
                                call.respond(emptyList<String>())
                            else
                                call.respond(countriesService.getCountriesByName(countryList.countries.toList()))
                        }
                    }
                    //Adds countries to a countriesList
                    post("/add") {
                        val telegramId = call.parameters["telegramId"]
                                ?: return@post call.respond(HttpStatusCode.BadRequest)
                        val listId = call.parameters["listId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                        if (!telegramRepository.authenticated(telegramId, listId)) {
                            call.respond(HttpStatusCode(401, "Telegram id not logged in or not associated to that list"))
                            return@post
                        }

                        when (val countryList = userListsRepository.getUserList(listId)) {
                            null -> call.respond(HttpStatusCode(400, "List not found"))
                            else -> {
                                val request = call.receive<UserCountriesListModificationRequest>()
                                val countryNames = countriesService.getAllCountries().map { x -> x.countryregion }
                                if (!countryNames.containsAll(request.countries)) {
                                    call.respond(HttpStatusCode(405, "invalid countries"),
                                            request.countries.fold("Invalid countries:", { acc, country ->
                                                if (!countryNames.contains(country))
                                                    "$acc\n$country"
                                                else
                                                    acc
                                            }))
                                    return@post
                                }

                                val newList = UserCountriesListModificationRequest(countryList.name, countryList.countries)
                                newList.countries.addAll(request.countries)
                                usersService.updateUserList(countryList.userId.toString(), countryList._id.toString(), newList)

                                call.respond(HttpStatusCode.OK, "Saved")
                            }
                        }
                    }

                    //Gets timeseries of a list between dates
                    get("/timeseries") {
                        val listId = call.parameters["listId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                        val iso2Countries = countriesService.getIsoByName(userListsRepository.getUserList(listId)!!.countries.toList())
                        val fromDate: String? = call.request.queryParameters["fromDate"]
                        val toDate: String? = call.request.queryParameters["toDate"]

                        call.respond(countriesService.getCountryTimesSeries(iso2Countries.toList().sorted(), null, null, fromDate, toDate))
                    }
                }
                //Creates a new userCountriesList
                post {
                    val telegramId = call.parameters["telegramId"]
                            ?: return@post call.respond(HttpStatusCode.BadRequest)
                    when (val session = telegramRepository.getTelegramSession(telegramId)) {
                        null -> call.respond(HttpStatusCode(400, "Id not found"))
                        else -> {
                            val request = call.receive<UserCountriesListModificationRequest>()

                            if (request.countries.isNotEmpty()) {
                                val countryNames = countriesService.getAllCountries().map { x -> x.countryregion }
                                if (!countryNames.containsAll(request.countries)) {
                                    call.respond(HttpStatusCode(405, "invalid countries"),
                                            request.countries.fold("Invalid countries:", { acc, country ->
                                                if (!countryNames.contains(country))
                                                    "$acc\n$country"
                                                else
                                                    acc
                                            }))
                                    return@post
                                }
                            }
                            call.respond(usersService.createUserList(session.userId.toString(), request.name, request.countries))
                        }
                    }
                }
            }
        }
    }
}