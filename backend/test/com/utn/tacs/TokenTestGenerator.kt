package com.utn.tacs;

import com.utn.tacs.auth.JwtConfig
import io.ktor.server.testing.TestApplicationRequest

object TokenTestGenerator {

  fun TestApplicationRequest.addJwtHeader(user: User) = addHeader("Authorization", "Bearer ${getToken(user)}")

  private fun getToken(user: User) = JwtConfig.makeToken(user)
}
