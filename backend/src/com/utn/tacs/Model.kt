package com.utn.tacs

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.time.Instant

data class User(
        val name: String,
        val email: String?,
        val password: String?,
        @ContextualSerialization
        val _id: Id<User> = newId(),
        @ContextualSerialization
        val creationDate: Instant? = null,
        val country: String
) {
        constructor(name: String) : this(name, null, null, newId(), null, "")
        constructor(name: String, email: String, password: String, country: String) : this(name, email, password, newId(), null, country)
        constructor(_id: Id<User>, name: String) : this(name, null, null, _id, null, "")
}

@Serializable
data class Location(
        val lat: Double,
        val lng: Double
)

@Serializable
data class CountryCode(
        val iso2: String,
        val iso3: String
)

data class Country(
        @ContextualSerialization
        val _id: Id<Country>?,
        val countryregion: String,
        val lastupdate: String,
        val location: Location,
        val countrycode: CountryCode?,
        val confirmed: Int,
        val deaths: Int,
        val recovered: Int
) {
    constructor(countryregion: String, lastupdate: String, location: Location, countrycode: CountryCode?, confirmed: Int, deaths: Int, recovered: Int) :
            this(newId(), countryregion, lastupdate, location, countrycode, confirmed, deaths, recovered)
}

data class UserCountriesList(
        @ContextualSerialization
        val _id: Id<UserCountriesList>,
        @ContextualSerialization
        val userId: Id<User>,
        val name: String,
        val countries: MutableSet<String>
) {
    constructor(userId: Id<User>, name: String, countries: MutableSet<String>) : this(newId(), userId, name, countries)
    constructor(userId: Id<User>, name: String) : this(newId(), userId, name, mutableSetOf())
}

data class UserCountriesListModificationRequest(
        val name: String?,
        val countries: MutableSet<String>?
)

data class LoginRequest(
        val user: String,
        val password: String
)

data class SignUpRequest(
        val name: String,
        val email: String,
        val password: String,
        val country: String
)

data class SignUpResponse(
        val user: User,
        val token: String
)

data class UserAccount(
        @ContextualSerialization
        val _id: Id<UserAccount> = newId(),
        @ContextualSerialization
        val userId: Id<User>,
        val token: String
) {
        constructor(userId: Id<User>, token: String): this(newId(), userId, token)
}