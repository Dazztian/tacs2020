package com.utn.tacs.rest

import com.utn.tacs.countries.CountriesService
import com.utn.tacs.utils.getLogger
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import java.lang.NumberFormatException


fun Application.countriesRoutes(countriesService: CountriesService) {
    val logger = getLogger()

    routing {
        route("/api/countries") {
            get {
                try {
                    val name = call.request.queryParameters["name"]
                    val lat = call.request.queryParameters["lat"]?.toDouble()
                    val lon = call.request.queryParameters["lon"]?.toDouble()
                    when {
                        lat != null && lon != null -> call.respond(countriesService.getNearestCountries(lat, lon))
                        name != null -> call.respond(countriesService.getCountryLatestByName(name))
                        else -> call.respond(countriesService.getAllCountries())
                    }
                } catch (e: NumberFormatException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (e: Exception) {
                    logger.error("Parameters where not correct...", e)
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            get("/names") {
                try {
                    call.respond(countriesService.getAllCountries().map { x -> x.countryregion }.sorted())
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/{iso2}") {
                try {
                    val iso2: String = call.parameters["iso2"].toString()
                    call.respond(countriesService.getCountryLatestByIsoCode(iso2.toUpperCase()))
                } catch (e: Exception) {
                    logger.error("iso2 code was not correct...", e)
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            get("/{iso2}/timeseries") {
                try {
                    val iso2: String = call.parameters["iso2"].toString()
                    val fromDay: Int? = call.request.queryParameters["fromDay"]?.toInt()
                    val toDay: Int? = call.request.queryParameters["toDay"]?.toInt()
                    val fromDate: String? = call.request.queryParameters["fromDate"]
                    val toDate: String? = call.request.queryParameters["toDate"]
                    call.respond(countriesService.getCountryTimesSeries(iso2.toUpperCase(), fromDay, toDay, fromDate, toDate))
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                } catch (e: NumberFormatException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (e: BadRequestException) {
                    call.respond(HttpStatusCode.BadRequest.description("malformed timeseries days or dates ranges"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}