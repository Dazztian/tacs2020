package com.utn.tacs.lists

import com.mongodb.DuplicateKeyException
import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId


class UserListsRepository(private val database: MongoDatabase) {

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
     * */
    //TODO when creating the database, we should add an index to name + userId
    // db.usercountriesList.createIndex( { "userId": 1, "name": 1 } )
    fun createUserList(userList: UserCountriesList): Id<UserCountriesList>? {
        return try {
            (database.getCollection<UserCountriesList>().insertOne(userList).insertedId as ObjectId?)?.toId()
        } catch (e: DuplicateKeyException) {
            null
        } catch (e: MongoException) {
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
    fun delete(userId: String, name: String): Boolean? {
        return getUserList(userId, name)?.let { database.getCollection<UserCountriesList>().deleteOneById(it._id).wasAcknowledged() }
    }


    fun doUpdate(userId: String, name: String, newName: String, countriesToAdd: MutableSet<String>): Id<UserCountriesList>? {
        return getUserList(userId, name)?.let {
            countriesToAdd.addAll(it.countries)
            (database.getCollection<UserCountriesList>().updateOneById(it._id, set(UserCountriesList::name setTo newName,
                    UserCountriesList::countries setTo countriesToAdd)).upsertedId as ObjectId?)?.toId()
        }

    }

    fun doUpdate(userId: String, name: String, countriesToAdd: MutableSet<String>): Id<UserCountriesList>? {
        return getUserList(userId, name)?.let {
            (database.getCollection<UserCountriesList>().updateOneById(it._id, UserCountriesList::countries addToSet countriesToAdd)
                    .upsertedId as ObjectId?)?.toId()
        }
    }

    fun doUpdate(userId: String, name: String, newName: String): Id<UserCountriesList>? {
        return getUserList(userId, name)?.let {
            (database.getCollection<UserCountriesList>()
                    .updateOneById(it._id, set(UserCountriesList::name setTo newName)).upsertedId as ObjectId?)?.toId()
        }

    }

    fun update(userId: String, name: String, newName: String?, countriesToAdd: MutableSet<String>?): Id<UserCountriesList>? {
        return if (newName != null && countriesToAdd != null) {
            doUpdate(userId, name, newName, countriesToAdd)
        } else if (newName != null) {
            doUpdate(userId, name, newName)
        } else if (countriesToAdd != null) {
            doUpdate(userId, name, countriesToAdd)
        } else {
            null
        }
    }


}