package com.utn.tacs

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.time.Instant
import java.time.LocalDate

data class User(
        val name: String,
        val email: String,
        val password: String,
        @ContextualSerialization
        val _id: Id<User> = newId(),
        @ContextualSerialization
        val creationDate: Instant? = null,
        val country: String?
) {
        constructor(name: String) : this(name, "", "", newId(), null, null)
        constructor(name: String, email: String, password: String, country: String) : this(name, email, password, newId(), null, country)
        constructor(name: String, email: String, password: String, _id: Id<User>) : this(name, email, password, _id, null, null)
        constructor(_id: Id<User>, name: String) : this(name, "", "", _id, null, null)
        constructor(_id: Id<User>, name: String, email:String, password:String) : this(name, email, password, _id, null, null)
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
        val recovered: Int,
        var timeseries: List<TimeSerie>? = listOf()
)

data class TimeSerie(
        val number: Int,
        val confirmed: Int,
        val deaths: Int,
        val recovered: Int,
        val date: String
)

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

data class LoginRequest(
        val email: String,
        val password: String
)

data class SignUpRequest(
        val name: String,
        val email: String,
        val password: String,
        val country: String
)

data class LoginResponse(
        val user: User,
        val token: String
)

data class LogOutRequest(
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

data class TelegramUser(
        val telegramId: String,
        val username: String?,
        val password: String?
)

data class TelegramSession(
        @ContextualSerialization
        val _id: Id<TelegramSession> = newId(),
        @ContextualSerialization
        val userId: Id<User>,
        val telegramId: String
){
    constructor(userId: Id<User>, telegramId: String): this(newId(), userId, telegramId)
}