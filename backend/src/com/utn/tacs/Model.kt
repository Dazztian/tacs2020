package com.utn.tacs

import com.utn.tacs.utils.countriesNamesMap
import io.ktor.auth.Principal
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.time.LocalDate

data class User(
        val name: String,
        val email: String,
        val password: String?,
        @ContextualSerialization
        val _id: Id<User> = newId(),
        @ContextualSerialization
        val creationDate: String? = null,
        val country: String?,
        val isAdmin: Boolean = false,
        var lastConnection: String? = null
) : Principal {
    constructor(name: String, email: String, password: String, country: String, isAdmin: Boolean) : this(name, email, password, newId(), null, country, isAdmin)
    constructor(name: String, email: String, password: String, _id: Id<User>) : this(name, email, password, _id, null, null)
    constructor(_id: Id<User>, name: String) : this(name, "", null, _id, null, null)
    constructor(_id: Id<User>, name: String, email: String, password: String) : this(name, email, password, _id, null, null)
    constructor(_id: Id<User>, name: String, email: String, password: String?, isAdmin:Boolean) : this(name, email, password, _id, null, null, isAdmin)

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
        var timeseries: List<TimeSeries>? = listOf()
)

@Serializable
data class CountryResponse (
        val countryregion: String,
        val lastupdate: String,
        val location: Location,
        val countrycode: CountryCode?,
        val confirmed: Int,
        val deaths: Int,
        val recovered: Int,
        var timeseries: List<TimeSeries>? = listOf(),
        var timeSeriesTotal: TimeSeriesTotal? = null
){
        constructor(c: Country):
        this(c.countryregion, c.lastupdate, c.location, c.countrycode, c.confirmed, c.deaths, c.recovered, c.timeseries ?: listOf())
        constructor(c: Country, timeSeriesTotal: TimeSeriesTotal):
        this(c.countryregion, c.lastupdate, c.location, c.countrycode, c.confirmed, c.deaths, c.recovered, c.timeseries ?: listOf(), timeSeriesTotal)
}

@Serializable
data class TimeSeriesTotal(
        val confirmed: Int,
        val deaths: Int,
        val recovered: Int
)

@Serializable
data class TimeSeries(
        var number: Int,
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
        val creationDate: LocalDate
) {
    constructor(userId: Id<User>, name: String, countries: MutableSet<String>, creationDate: LocalDate) : this(newId(), userId, name, countries, creationDate)
    constructor(userId: Id<User>, name: String, countries: MutableSet<String>) : this(newId(), userId, name, countries, LocalDate.now())

}

class UserCountriesListWrapper(
        val _id: Id<UserCountriesList>,
        val name: String,
        val countries: Set<String>){
    constructor(id :Id<UserCountriesList>, name :String) : this(id, name, emptySet())
}

data class UserCountriesListModificationRequest(
        val name: String,
        val countries: MutableSet<String>
)

data class LoginRequest(
        val email: String,
        val password: String
)

@Serializable
data class SignUpRequest(
        val name: String,
        val email: String,
        val password: String,
        val country: String,
        var isAdmin: Boolean? = false
)

data class UserData(
        val user: User,
        val listsQuantity: Int,
        val countriesTotal: Int
)

data class UserListComparision(
        val userCountryList1: UserCountriesListResponse,
        val userCountryList2: UserCountriesListResponse,
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
) {
    constructor(userId: Id<User>, telegramId: String) : this(newId(), userId, telegramId)
}

data class UserResponse(
        val id: String,
        val name: String,
        val email: String,
        val creationDate: String,
        val country: String,
        val isAdmin: Boolean,
        val lists: List<UserCountriesListResponse>
) {
        constructor(u: User, lists: List<UserCountriesListResponse>):
                this(u._id.toString(), u.name, u.email, u.creationDate ?: "", u.country ?: "", u.isAdmin, lists)
}

data class LoginResponse(
        val user: UserResponse,
        val token: String
) {
        constructor(u: User, lists: List<UserCountriesListResponse>, token: String):
                this(UserResponse(u._id.toString(), u.name, u.email, u.creationDate ?: "", u.country ?: "", u.isAdmin, lists), token)
}

data class UserCountriesListResponse(
        val id: String,
        val name: String,
        val countries: MutableSet<CountriesNamesResponse>,
        val creationDate: String
){
        constructor(u: UserCountriesList):
        this(u._id.toString(), u.name, u.countries.map{ CountriesNamesResponse(it) }.toMutableSet(), u.creationDate.toString())
}

@Serializable
data class CountriesNamesResponse(
        val name: String,
        val iso2: String
) {
        constructor(iso2: String):
                this(countriesNamesMap.get(iso2) ?: iso2, iso2)
}

data class UserBasicData(
        val id: String,
        val email: String,
        val name: String
)

data class CountryListsDataResponse(
        val totalUsers: Int,
        val users: Set<String>
)