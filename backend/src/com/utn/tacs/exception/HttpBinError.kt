package com.utn.tacs.exception

import io.ktor.http.HttpStatusCode

data class HttpBinError(
        val request: String,
        val message: String,
        val code: HttpStatusCode
)