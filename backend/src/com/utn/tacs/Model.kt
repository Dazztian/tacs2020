package com.utn.tacs

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.time.Instant

@Serializable
data class User(
        val name: String,
        val email: String?,
        val password: String?,
        @ContextualSerialization
        val _id: Id<User> = newId(),
        @ContextualSerialization
        val creationDate: Instant? = null
) {
    override fun toString(): String {
        return "{\"_id\":$_id, \"name\":\"$name\", \"email\":\"$email\", \"password\":\"$password\", \"creationDate\": $creationDate}"
    }

    constructor(name: String) : this(name, null, null, newId())
    constructor(name: String, email: String, password: String) : this(name, email, password, newId())
    constructor(_id: Id<User>, name: String) : this(name, null, null, _id)
}

@Serializable
data class Location(
        val name: String,
        val lat: Double,
        val lng: Double
) {
    override fun toString(): String {
        return "{ \"name\":\"$name\", \"lat\": $lat, \"lng\": $lng}"
    }
}

@Serializable
data class CountryCode(
        val iso2: String,
        val iso3: String
) {
    override fun toString(): String {
        return "{ \"iso2\": \"$iso2\", \"iso3\": \"$iso3\"}"
    }
}

@Serializable
data class CountryData(
        val countryregion: String,
        val lastupdate: String,
        val location: Location,
        val countrycode: CountryCode,
        val confirmed: Int,
        val deaths: Int,
        val recovered: Int
) {
    override fun toString(): String {
        return "{\"countryregion\": \"$countryregion\", \"lastupdate\" : \"$lastupdate \", \"location\" : $location, \"countrycode\":$countrycode, \"confirmed\": $confirmed, \"deaths\":$deaths, \"recovered\": $recovered}"
    }
}

@Serializable
data class UserCountriesList(
        @ContextualSerialization
        val _id: Id<UserCountriesList> = newId(),
        @ContextualSerialization
        val userId: Id<User>,
        val name: String,
        val countries: MutableSet<String>
) {
    override fun toString(): String {
        return "{\"userId\": \"$userId\", \"name\": \"$name\", \"countries\": \"$countries\"}"
    }

    constructor(userId: Id<User>, name: String, countries: MutableSet<String>) : this(newId(), userId, name, countries)
    constructor(userId: Id<User>, name: String) : this(newId(), userId, name, mutableSetOf())
}

@Serializable
data class UserCountriesListModificationRequest(
        val name: String?,
        val countries: MutableSet<String>?
)