package com.utn.tacs.lists

import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.user.UsersRepository
import com.utn.tacs.utils.getLogger
import io.ktor.features.NotFoundException
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId
import java.time.LocalDate


class UserListsRepository(private val database: MongoDatabase, private val usersRepository: UsersRepository) {

    private val logger = getLogger()

    /**
     * Get user lists of countries by user id
     *
     * @param userId String
     * @return List<UserCountriesList>
     */
    fun getUserLists(userId: String): List<UserCountriesList> {
        return database.getCollection<UserCountriesList>("userCountriesList").find(UserCountriesList::userId eq ObjectId(userId).toId()).toList()
    }

    /**
     * Get one user list of countries by list id
     *
     * @param userListId String
     * @return UserCountriesList?
     */
    fun getUserList(userListId: String): UserCountriesList? {
        val listId: Id<UserCountriesList> = ObjectId(userListId).toId()
        return database.getCollection<UserCountriesList>("userCountriesList").findOneById(listId)
    }

    /**
     * Gets all lists of countries between dates bundle
     *
     * @param startDate LocalDate
     * @param endDate LocalDate
     * @return List<UserCountriesList>
     */
    fun getUserListsByCreationDate(startDate: LocalDate, endDate: LocalDate): List<UserCountriesList> {
        return database.getCollection<UserCountriesList>("userCountriesList").find(UserCountriesList::creationDate gte startDate, UserCountriesList::creationDate lte endDate).toList()
    }

    /**
     * Returns the amount of lists of countries in database
     *
     * @return Long
     */
    fun getCount(): Long {
        return database.getCollection<UserCountriesList>("userCountriesList").countDocuments()
    }

    /**
     * Get all lists of countries that contains the Country name
     *
     * @param country String
     * @return List<UserCountriesList>
     */
    fun getAllThatContains(country: String): List<UserCountriesList> {
        return database.getCollection<UserCountriesList>("userCountriesList").find(UserCountriesList::countries contains country).toList()
    }

    /**
     * This creates a list when the user does not have one with that name.
     *  We return the userList _id because if it was correctly added the id will be the same.
     *
     * @param userList UserCountriesList
     * @return String?
     *
     * @throws NotFoundException
     */
    fun createUserList(userList: UserCountriesList): String? {
        return try {
            usersRepository.getUserById(userList._id.toString()) ?: throw NotFoundException("User does not exists")
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
     *
     * @param listId String
     * @return Boolean
     *          True when delete is correct
     *          False when delete is not correct
     *          null when object was not found
     * @throws NotFoundException
     */
    fun delete(listId: String): Boolean {
        getUserList(listId) ?: throw NotFoundException("User list does not exists")
        return getUserList(listId)?.let {
            logger.info("User found, trying to delete...")
            val deleted = database.getCollection<UserCountriesList>().deleteOneById(it._id)
            logger.info("Delete status: $deleted")
            deleted.wasAcknowledged()
        } ?: false
    }

    /**
     * Updates one user's list of countries. updates the list name or the countries in the list
     *
     * @param listId String
     * @param newName String
     * @param countriesToAdd MutableSet<String>
     * @return String?
     */
    fun doUpdate(listId: String, newName: String, countriesToAdd: MutableSet<String>): String? {
        val a = getUserList(listId)
        return getUserList(listId)?.let {
            countriesToAdd.addAll(it.countries)
            database.getCollection<UserCountriesList>().findOneAndUpdate(UserCountriesList::_id eq it._id, set(UserCountriesList::name setTo newName, UserCountriesList::countries setTo countriesToAdd))?._id.toString()
        }
    }
}