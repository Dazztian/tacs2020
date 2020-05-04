package com.utn.tacs.rest

import io.ktor.application.call
import io.ktor.routing.*
import io.ktor.response.*
import com.utn.tacs.*
import com.utn.tacs.countries.*

fun Route.countriesRoutes() {
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