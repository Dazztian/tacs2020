package com.utn.tacs.rest

import com.utn.tacs.exception.HttpBinError
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
            route("/api/admin/report") {
                get {
                    try {
                        call.user?.isAdmin ?: throw UnauthorizedException("User is not admin")
                        call.respond(adminReportsService.getAllUsers())
                    } catch (e: NotFoundException) {
                        call.respond(HttpStatusCode.NotFound)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
            route("/api/admin/report/{userId}") {
                get {
                    call.user?.isAdmin ?: throw UnauthorizedException("User is not admin")
                    val userId = call.parameters["userId"]!!.toString()
                    call.respond(adminReportsService.getUserData(userId))
                }
            }
            route("/api/admin/report/lists/compare") {
                get {
                        call.user?.isAdmin ?: throw UnauthorizedException("User is not admin")
                        val userCountriesListId1 = call.request.queryParameters["list1"]!!.toString()
                        val userCountriesListId2 = call.request.queryParameters["list2"]!!.toString()
                        val response = adminReportsService.getListComparison(userCountriesListId1, userCountriesListId2)
                        call.respond(response)
                }
            }
            route("/api/admin/report/{country}/list") {
                get {
                        call.user?.isAdmin ?: throw UnauthorizedException("User is not admin")
                        val country: String = call.parameters["country"]!!.toString()
                        call.respond(adminReportsService.getUsersByCountry(country))
                }
            }
            route("/api/admin/report/lists/total") {
                get {
                    call.user?.isAdmin ?: throw UnauthorizedException("User is not admin")
                    call.respond(adminReportsService.getListsQuantity())
                }
            }
            route("/api/admin/report/lists") {
                get {
                    call.user?.isAdmin ?: throw UnauthorizedException("User is not admin")
                    val startDateParam = call.request.queryParameters["startDate"]!!.toString().split("/")
                    val endDateParam = call.request.queryParameters["endDate"]!!.toString().split("/")
                    val startDate = LocalDate.of(startDateParam.get(2).toInt(),startDateParam.get(0).toInt(),startDateParam.get(1).toInt())
                    val endDate = LocalDate.of(endDateParam.get(2).toInt(),endDateParam.get(0).toInt(),endDateParam.get(1).toInt())
                    call.respond(adminReportsService.getListsQuantityBetween(startDate, endDate))
                }
            }
        }
    }
}
