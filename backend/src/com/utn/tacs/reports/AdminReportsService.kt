package com.utn.tacs.reports

import com.utn.tacs.User
import com.utn.tacs.UserCountriesList
import com.utn.tacs.UserData
import com.utn.tacs.UserListComparision
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.user.UsersRepository
import org.litote.kmongo.Id
import java.time.LocalDate


class AdminReportsService(private val usersRepository: UsersRepository, private val userListsRepository: UserListsRepository) {

    fun getUserData(userId: Id<User>): UserData? {
        val user = usersRepository.getUserById(userId)
        return if (user != null) {
            val lists = userListsRepository.getUserLists(user._id.toString())
            UserData(user, lists.size, lists.sumBy { l -> l.countries.size })
        } else {
            null
        }
    }

    fun getRegisteredUserListsBetween(startDate: LocalDate, endDate: LocalDate): List<UserCountriesList> {
        return userListsRepository.getUserListsByCreationDate(startDate, endDate)
    }

    fun getListsQuantity(): Long {
        return userListsRepository.getCount()
    }

    fun getUsersByCountry(country: String): Set<Id<User>> {
        val userLists = userListsRepository.getAllThatContains(country)
        return userLists.map { l -> l.userId }.toSet()
    }

    fun getListComparison(userListId1: Id<UserCountriesList>, userListId2: Id<UserCountriesList>): UserListComparision? {

        val userList1 = userListsRepository.getUserList(userListId1.toString())
        val userList2 = userListsRepository.getUserList(userListId2.toString())

        return if (userList1 != null && userList2 != null) {
            UserListComparision(userList1, userList2, userList1.countries.intersect(userList2.countries))
        } else {
            null
        }
    }
}
