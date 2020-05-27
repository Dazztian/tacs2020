package com.utn.tacs.rest

import com.utn.tacs.countries.CountriesService
import com.utn.tacs.*
import com.utn.tacs.countries.*
import com.utn.tacs.utils.getLogger
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.util.pipeline.intercept


fun Application.countriesRoutes(countriesService: CountriesService) {
    val logger = getLogger()

    routing {
        route("/api/countries") {
            get {
                try{
                    val lat = call.request.queryParameters["lat"]?.toDouble()
                    val lon = call.request.queryParameters["lon"]?.toDouble()
                    if (lat != null && lon != null) {
                        call.respond(countriesService.getNearestCountries(lat, lon))
                    } else {
                        call.respond(countriesService.getAllCountries())
                    }
                } catch (e: Exception){
                    logger.error("Parameters where not correct...", e)
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/{iso2}") {
                val iso2: String = call.parameters["iso2"].toString()
                call.respond(countriesService.getCountryLatestByIsoCode(iso2.toUpperCase()))
            }
            get("/{iso2}/timeseries") {
                val iso2: String = call.parameters["iso2"].toString()
                call.respond(countriesService.getCountryTimesSeries(iso2.toUpperCase()))
            }
        }
    }
}