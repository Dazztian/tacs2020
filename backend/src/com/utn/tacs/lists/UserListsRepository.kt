package com.utn.tacs.lists

import com.google.gson.reflect.TypeToken
import com.mongodb.MongoClient
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.Updates
import com.utn.tacs.UserCountriesList
import com.utn.tacs.gson
import org.bson.Document


data class UserListWrapper(
        val countriesList: List<UserCountriesList>
)

class UserListsRepository(private val mongoClient: MongoClient, private val database: String) {

    private fun getUserListsAsDocument(userId: Int): Document? {
        val db = mongoClient.getDatabase(database)
        return db.getCollection("users").find(eq("id", userId))
                .projection(Document("countriesList", 1).append("_id", 0)).first()
    }

    fun getUserLists(userId: Int): List<UserCountriesList> {
        val userCountryListType = object : TypeToken<UserListWrapper>() {}.type
        val lists = getUserListsAsDocument(userId)
        return if (lists != null) {
            val result: UserListWrapper = gson.fromJson(lists.toJson(), userCountryListType)
            result.countriesList
        } else {
            emptyList()
        }
    }

    fun getUserLists(userId: Int, name: String): List<UserCountriesList> {
        return getUserLists(userId).filter { ucl -> ucl.name == name }
    }

    //Should sent something that confirms the insert, or shows that it failed.
    /**
     * This creates a list when the user does not have one with that name.
     * */
    fun createUserList(userId: Int, name: String, countries: List<String>) {
        val db = mongoClient.getDatabase(database)
        val countriesToAdd = Document().append("name", name).append("countries", countries)
        db.getCollection("users").updateOne(and(eq("id", userId), ne("countriesList.name", name)), Updates.addToSet("countriesList", countriesToAdd))
    }

    /**
     * This method adds countries to the list when they are not in there.
     * */
    fun addCountriesToUserList(userId: Int, name: String, countries: List<String>) {}
}