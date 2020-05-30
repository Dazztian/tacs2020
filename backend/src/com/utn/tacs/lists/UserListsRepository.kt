package com.utn.tacs.lists

import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.utils.getLogger
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId
import java.time.LocalDate


class UserListsRepository(private val database: MongoDatabase) {

    private val logger = getLogger()

    fun getUserLists(userId: String): List<UserCountriesList> {
        return database.getCollection<UserCountriesList>("userCountriesList").find(UserCountriesList::userId eq ObjectId(userId).toId()).toList()
    }

    fun getUserList(userListId: String): UserCountriesList? {
        val listId: Id<UserCountriesList> = ObjectId(userListId).toId()
        return database.getCollection<UserCountriesList>("userCountriesList").findOneById(listId)
    }

    fun getUserListsByCreationDate(startDate: LocalDate, endDate: LocalDate): List<UserCountriesList> {
        return database.getCollection<UserCountriesList>("userCountriesList").find(UserCountriesList::creationDate gte startDate.toString(), UserCountriesList::creationDate lte endDate.toString()).toList()
    }

    fun getCount(): Long {
        return database.getCollection<UserCountriesList>("userCountriesList").countDocuments()
    }

    fun getAllThatContains(country: String): List<UserCountriesList> {
        return database.getCollection<UserCountriesList>("userCountriesList").find(UserCountriesList::countries contains country).toList()
    }

    /**
     * This creates a list when the user does not have one with that name.
     * We return the userList _id because if it was correctly added the id will be the same.
     * */
    fun createUserList(userList: UserCountriesList): String? {
        return try {
            database.getCollection<UserCountriesList>().insertOne(userList)
            logger.info("User country list ${userList.name} creation completed.")
            return userList._id.toString()
        } catch (e: MongoException) {
            logger.error("Creation failed with exception:", e)
            null
        }
    }

    /**
     * Deletes a UserCountriesList.
     * @return
     *      True when delete is correct
     *      False when delete is not correct
     *      null when object was not found.
     */
    fun delete(listId: String): Boolean {
        return getUserList(listId)?.let {
            logger.info("User found, trying to delete...")
            val deleted = database.getCollection<UserCountriesList>().deleteOneById(it._id)
            logger.info("Delete status: $deleted")
            deleted.wasAcknowledged()
        } ?: false
    }

    fun doUpdate(listId: String, newName: String, countriesToAdd: MutableSet<String>): String? {
        val a = getUserList(listId)
        return getUserList(listId)?.let {
            countriesToAdd.addAll(it.countries)
            database.getCollection<UserCountriesList>().findOneAndUpdate(UserCountriesList::_id eq it._id, set(UserCountriesList::name setTo newName, UserCountriesList::countries setTo countriesToAdd))?._id.toString()
        }
    }
}