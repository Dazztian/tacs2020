package com.utn.tacs.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.typesafe.config.ConfigFactory
import com.utn.tacs.User
import java.util.*

object JwtConfig {

    private val secret = ConfigFactory.load().getString("jwt.secret")
    private val issuer = ConfigFactory.load().getString("jwt.issuer")
    private val validityInMs = ConfigFactory.load().getInt("jwt.validity")

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
            .require(algorithm)
            .withIssuer(issuer)
            .build()


    /**
     * Produce a token for this User
     */
    fun makeToken(user: User): String = JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("id", user._id.toString())
            .withExpiresAt(getExpiration())
            .sign(algorithm)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

}