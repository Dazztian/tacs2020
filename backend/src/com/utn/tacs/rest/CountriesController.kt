package com.utn.tacs.rest

import com.utn.tacs.countries.getCountriesFromDatabase
import com.utn.tacs.*
import com.utn.tacs.countries.*
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
                    call.respond(getNearestCountries(lat, lon).map { it.countryregion })
                } else {
                    call.respond(getAllCountries().map { it.countryregion })
                }
            }
            get("/tree") {
                call.respond(getAllCountries())
            }
            get("/{iso2}") {
                val iso2: String = call.parameters["iso2"].toString()
                call.respond(getCountryLatestByIsoCode(iso2.toUpperCase()))
            }
        }
    }
}