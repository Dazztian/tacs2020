package com.utn.tacs

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.time.Instant
import java.time.LocalDate

data class User(
        val name: String,
        val email: String?,
        val password: String?,
        @ContextualSerialization
        val _id: Id<User> = newId(),
        @ContextualSerialization
        val creationDate: Instant? = null
) {
    constructor(name: String) : this(name, null, null, newId())
    constructor(name: String, email: String, password: String) : this(name, email, password, newId())
    constructor(_id: Id<User>, name: String) : this(name, null, null, _id)
    constructor(_id: Id<User>, name: String, email:String, password:String) : this(name, email, password, _id)

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
        val countries: MutableSet<String>,
        @ContextualSerialization
        val creationDate: String
) {
    constructor(userId: Id<User>, name: String, countries: MutableSet<String>, creationDate: LocalDate) : this(newId(), userId, name, countries, creationDate.toString())
    constructor(userId: Id<User>, name: String, countries: MutableSet<String>) : this(newId(), userId, name, countries, LocalDate.now().toString())
    constructor(userId: Id<User>, name: String) : this(newId(), userId, name, mutableSetOf(), LocalDate.now().toString())

}

data class UserCountriesListModificationRequest(
        val name: String?,
        val countries: MutableSet<String>?
)

data class UserData(
        val user: User,
        val listsQuantity: Int,
        val countriesTotal: Int
)

data class UserListComparision(
        val userCountryList1: UserCountriesList,
        val userCountryList2: UserCountriesList,
        val sharedCountries: Set<String>
)
