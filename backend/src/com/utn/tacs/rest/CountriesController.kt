package com.utn.tacs.rest
import com.utn.tacs.CountriesNamesResponse
import com.utn.tacs.CountryResponse
import com.utn.tacs.TimeserieResponse
import com.utn.tacs.UserNamesResponse
import com.utn.tacs.countries.CountriesService
import com.utn.tacs.utils.getLogger
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.countriesRoutes(countriesService: CountriesService) {

    routing {
        route("/api/countries") {
            get {
                val name = call.request.queryParameters["name"]
                val lat = call.request.queryParameters["lat"]?.toDouble()
                val lon = call.request.queryParameters["lon"]?.toDouble()
                when {
                    lat != null && lon != null -> call.respond(countriesService.getNearestCountries(lat, lon).map { TimeserieResponse(it) })
                    name != null -> call.respond(countriesService.getCountryLatestByName(name))
                    else -> call.respond(countriesService.getAllCountries())
                }
            }
            get("/names") {
                call.respond(countriesService.getAllCountries()
                    .filter { it.countrycode != null}
                    .map { UserNamesResponse(it.countryregion, it.countrycode!!.iso2) } )
            }
            get("/{iso2}") {
                val iso2: String = call.parameters["iso2"].toString()
                call.respond(countriesService.getCountryLatestByIsoCode(iso2.toUpperCase()))
            }
            get("/timeseries") {
                val iso2Countries = call.request.queryParameters["countries"]!!.split(",")
                val fromDay: Int? = call.request.queryParameters["fromDay"]?.toInt()
                val toDay: Int? = call.request.queryParameters["toDay"]?.toInt()
                val fromDate: String? = call.request.queryParameters["fromDate"]
                val toDate: String? = call.request.queryParameters["toDate"]
                if (null != fromDay && null != toDay && fromDay > toDay) {
                    throw BadRequestException("Invalid days ranges")
                }

                val countries : List<CountryResponse>
                when {
                    (fromDay == null && toDay == null && fromDate == null && toDate == null) -> countries = countriesService.getCountryTimesSeries(iso2Countries)
                    else -> countries = countriesService.getCountryTimesSeries(iso2Countries, fromDay, toDay, fromDate, toDate)
                }
                call.respond(countries.map { TimeserieResponse(it) })
            }
        }
    }
}