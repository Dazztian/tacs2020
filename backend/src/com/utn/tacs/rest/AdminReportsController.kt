package com.utn.tacs.rest

import com.utn.tacs.exception.UnauthorizedException
import com.utn.tacs.reports.AdminReportsService
import com.utn.tacs.user
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import java.time.LocalDate

/**
 * This for now is the only difference between a normal user and admins
 * Based on that and that creating a different authenticate {} based on roles would add
 * lots of code, and dirt on the other controllers, we decide to check based on call.user if the user is an admin.
 *  */
fun Application.adminReports(adminReportsService: AdminReportsService) {
    routing {
        authenticate {
            route("/api/admin/report/{userId}") {
                get {
                    try {
                        call.user?.isAdmin ?: throw UnauthorizedException("User is not admin")
                        val userId = call.parameters["userId"]!!.toString()
                        call.respond(adminReportsService.getUserData(userId))
                    } catch (e: NotFoundException) {
                        call.respond(HttpStatusCode.NotFound)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
            route("/api/admin/report/lists/compare") {
                get {
                    try {
                        call.user?.isAdmin ?: throw UnauthorizedException("User is not admin")
                        val userCountriesListId1 = call.request.queryParameters["list1"]!!.toString()
                        val userCountriesListId2 = call.request.queryParameters["list2"]!!.toString()
                        call.respond(adminReportsService.getListComparison(userCountriesListId1, userCountriesListId2))
                    } catch (e: NotFoundException) {
                        logger.error("Parameters where not correct...", e)
                        call.respond(HttpStatusCode.NotFound)
                    } catch (e: UnauthorizedException) {
                        call.respond(HttpStatusCode.Unauthorized)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
            route("/api/admin/report/{country}/list") {
                get {
                    try {
                        call.user?.isAdmin ?: throw UnauthorizedException("User is not admin")
                        val country: String = call.parameters["country"]!!.toString()
                        call.respond(adminReportsService.getUsersByCountry(country))
                    } catch (e: UnauthorizedException) {
                        call.respond(HttpStatusCode.Unauthorized)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
            route("/api/admin/report/lists/total") {
                get {
                    try {
                        call.user?.isAdmin ?: throw UnauthorizedException("User is not admin")
                        call.respond(adminReportsService.getListsQuantity())
                    } catch (e: UnauthorizedException) {
                        call.respond(HttpStatusCode.Unauthorized)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
            route("/api/admin/report/lists") {
                get {
                    try {
                        call.user?.isAdmin ?: throw UnauthorizedException("User is not admin")
                        val startDate: String = call.request.queryParameters["startDate"]!!.toString()
                        val endDate: String = call.request.queryParameters["endDate"]!!.toString()
                        call.respond(adminReportsService.getRegisteredUserListsBetween(LocalDate.parse(startDate), LocalDate.parse(endDate)))
                    } catch (e: UnauthorizedException) {
                        call.respond(HttpStatusCode.Unauthorized)
                    } catch (e: Exception) {
                        logger.error("Parameters where not correct...", e)
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
        }
    }
}
