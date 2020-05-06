package com.utn.tacs.rest

import com.utn.tacs.countries.getCountriesFromDatabase
import com.utn.tacs.getAllCountries
import com.utn.tacs.getCountryLatestByIsoCode
import com.utn.tacs.getNearestCountries
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.countriesRoutes() {
    routing {
        route("/api/countries") {
            get {
                val lat = call.request.queryParameters["lat"]?.toDouble()
                val lon = call.request.queryParameters["lon"]?.toDouble()
                if (lat != null && lon != null) {
                    call.respond(getNearestCountries(lat, lon))
                } else {
                    call.respond(getAllCountries())
                }
            }
            get("/tree") {
                call.respond(getCountriesFromDatabase())
            }
            get("/{iso2}") {
                val iso2: String = call.parameters["iso2"].toString()
                call.respond(getCountryLatestByIsoCode(iso2.toUpperCase()))
            }
        }
    }
}