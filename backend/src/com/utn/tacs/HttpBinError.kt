package com.utn.tacs

import io.ktor.http.HttpStatusCode

data class HttpBinError(
        val request: String,
        val message: String,
        val code: HttpStatusCode,
        val cause: Throwable? = null
)