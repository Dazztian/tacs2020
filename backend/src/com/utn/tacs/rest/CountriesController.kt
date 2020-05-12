package com.utn.tacs.rest

import com.utn.tacs.countries.getCountriesFromDatabase
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



fun Application.countriesRoutes() {
    val logger = getLogger()

    routing {
        route("/api/countries") {
            get {
               try{ val lat = call.request.queryParameters["lat"]?.toDouble()
                val lon = call.request.queryParameters["lon"]?.toDouble()
                if (lat != null && lon != null) {
                    call.respond(getNearestCountries(lat, lon))
                } else {
                    call.respond(getAllCountries())
                } } catch (e: Exception){
                   logger.error("Parameters where not correct...", e)
                   call.respond(HttpStatusCode.BadRequest)
               }
            }
            get("/tree") {
                call.respond(getAllCountries())
            }
            get("/{iso2}") {
                val iso2: String = call.parameters["iso2"].toString()
                var values = getCountryLatestByIsoCode(iso2.toUpperCase())
                call.respond(values)
            }
        }
    }
}