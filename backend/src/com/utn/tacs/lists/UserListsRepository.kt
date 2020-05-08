package com.utn.tacs.lists

import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.utils.getLogger
import org.litote.kmongo.*


class UserListsRepository(private val database: MongoDatabase) {

    private val logger = getLogger()

    fun getUserLists(userId: Id<User>): List<UserCountriesList> {
        return database.getCollection<UserCountriesList>("userCountriesList").find(UserCountriesList::userId eq userId).toList()
    }

    fun getUserLists(userId: String): List<UserCountriesList> {
        return getUserLists(userId.toId())
    }

    fun getUserList(userId: Id<User>, name: String): UserCountriesList? {
        return database.getCollection<UserCountriesList>("userCountriesList").findOne(UserCountriesList::userId eq userId, UserCountriesList::name eq name)
    }


    fun getUserList(userId: String, name: String): UserCountriesList? {
        return getUserList(userId.toId(), name)
    }

    /**
     * This creates a list when the user does not have one with that name.
     * We return the userList _id because if it was correctly added the id will be the same.
     * */
    fun createUserList(userList: UserCountriesList): Id<UserCountriesList>? {
        return try {
            database.getCollection<UserCountriesList>().insertOne(userList)
            logger.info("User country list ${userList.name} creation completed.")
            return userList._id
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
    fun delete(userId: Id<User>, name: String): Boolean? {
        return getUserList(userId, name)?.let {
            logger.info("User found, trying to delete...")
            val deleted = database.getCollection<UserCountriesList>().deleteOneById(it._id)
            logger.info("Delete status: $deleted")
            deleted.wasAcknowledged()
        }
    }


    fun doUpdate(userId: Id<User>, name: String, newName: String, countriesToAdd: MutableSet<String>): Id<UserCountriesList>? {
        return getUserList(userId, name)?.let {
            countriesToAdd.addAll(it.countries)
            database.getCollection<UserCountriesList>().findOneAndUpdate(UserCountriesList::_id eq it._id, set(UserCountriesList::name setTo newName, UserCountriesList::countries setTo countriesToAdd))?._id
        }
    }

    fun doUpdate(userId: Id<User>, name: String, countriesToAdd: MutableSet<String>): Id<UserCountriesList>? {
        return doUpdate(userId, name, name, countriesToAdd)
    }

    fun doUpdate(userId: Id<User>, name: String, newName: String): Id<UserCountriesList>? {
        return doUpdate(userId, name, newName, mutableSetOf())
    }

    fun update(userId: Id<User>, name: String, newName: String?, countriesToAdd: MutableSet<String>?): Id<UserCountriesList>? {
        logger.info("Starting update process...")
        return if (newName != null && countriesToAdd != null) {
            logger.info("Going to update name and countries...")
            doUpdate(userId, name, newName, countriesToAdd)
        } else if (newName != null) {
            logger.info("Going to update name...")
            doUpdate(userId, name, newName)
        } else if (countriesToAdd != null) {
            logger.info("Going to update countries...")
            doUpdate(userId, name, countriesToAdd)
        } else {
            logger.info("Can not update to null values...")
            null
        }
    }


}