package com.utn.tacs.rest

import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.exception.UnAuthorizedException
import com.utn.tacs.reports.AdminReportsService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import org.litote.kmongo.Id
import org.litote.kmongo.toId
import java.time.LocalDate
import io.ktor.request.header

fun Application.adminReports(adminReportsService: AdminReportsService) {
    routing {
        route("/api/admin/report/{userId}") {
            get {
                try {
                    authorizeUserAdmin(call.request.header("Authorization") ?: "")
                    val userId = call.parameters["userId"]!!.toString()
                    call.respond(adminReportsService.getUserData(userId))
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
        route("/api/admin/report/lists/compare") {
            get {
                try {
                    authorizeUserAdmin(call.request.header("Authorization") ?: "")
                    val userCountriesListId1 = call.request.queryParameters["list1"]!!.toString()
                    val userCountriesListId2 = call.request.queryParameters["list2"]!!.toString()
                    call.respond(adminReportsService.getListComparison(userCountriesListId1, userCountriesListId2))
                } catch (e: NotFoundException) {
                    logger.error("Parameters where not correct...", e)
                    call.respond(HttpStatusCode.NotFound)
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
        route("/api/admin/report/{country}/list") {
            get {
                try {
                    authorizeUserAdmin(call.request.header("Authorization") ?: "")
                    val country: String = call.parameters["country"]!!.toString()
                    call.respond(adminReportsService.getUsersByCountry(country))
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
        route("/api/admin/report/lists/total") {
            get {
                try {
                    authorizeUserAdmin(call.request.header("Authorization") ?: "")
                    call.respond(adminReportsService.getListsQuantity())
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
        route("/api/admin/report/lists") {
            get {
                try {
                    authorizeUserAdmin(call.request.header("Authorization") ?: "")
                    val startDate: String = call.request.queryParameters["startDate"]!!.toString()
                    val endDate: String = call.request.queryParameters["endDate"]!!.toString()
                    call.respond(adminReportsService.getRegisteredUserListsBetween(LocalDate.parse(startDate), LocalDate.parse(endDate)))
                } catch (e: UnAuthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized)
                } catch (e: Exception) {
                    logger.error("Parameters where not correct...", e)
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}
