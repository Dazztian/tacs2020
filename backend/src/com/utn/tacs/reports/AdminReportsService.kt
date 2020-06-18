package com.utn.tacs.reports

import com.utn.tacs.*
import com.utn.tacs.lists.UserListsRepository
import com.utn.tacs.user.UsersRepository
import io.ktor.features.NotFoundException
import java.time.LocalDate

class AdminReportsService(private val usersRepository: UsersRepository, private val userListsRepository: UserListsRepository) {

    /**
     * Get user data by id. The size of the list of countries and the total of countries in all his lists
     *
     * @param userId String
     * @return UserData
     *
     * @throws NotFoundException
     */
    fun getUserData(userId: String): UserData {
        val user = usersRepository.getUserById(userId) ?: throw NotFoundException()
        val lists = userListsRepository.getUserLists(userId)
        return UserData(user, lists.size, lists.sumBy { l -> l.countries.size })
    }

    /**
     * Get all users id, email and name
     *
     * @return List<UserData>
     *
     */
    fun getAllUsers(): List<UserBasicData> {
        val usersData = usersRepository.getUsers().map {
            UserBasicData(it._id.toString(), it.email, it.name)
        }
        return usersData
    }

    /**
     * Gets all lists of countries between dates bundle
     *
     * @param startDate LocalDate
     * @param endDate LocalDate
     * @return List<UserCountriesList>
     */
    fun getRegisteredUserListsBetween(startDate: LocalDate, endDate: LocalDate): List<UserCountriesList> {
        return userListsRepository.getUserListsByCreationDate(startDate, endDate)
    }

    /**
     * Returns the amount of lists of countries in database
     *
     * @return Long
     */
    fun getListsQuantity(): Long {
        return userListsRepository.getCount()
    }

    /**
     * Get all lists of countries that contains the Country name
     *
     * @param country String
     * @return Set<Id<User>>
     */
    fun getUsersByCountry(country: String): Set<String> {
        val userLists = userListsRepository.getAllThatContains(country)
        return userLists.map { l -> l.userId.toString() }.toSet()
    }

    /**
     * Get the comparison of two lists of countries
     *
     * @param userListId1 String
     * @param userListId2 String
     * @return UserListComparision
     *
     * @throws NotFoundException
     */
    fun getListComparison(userListId1: String, userListId2: String): UserListComparision {
        val userList1 = userListsRepository.getUserList(userListId1) ?: throw NotFoundException()
        val userList2 = userListsRepository.getUserList(userListId2) ?: throw NotFoundException()
        return UserListComparision(
            UserCountriesListResponse(userList1),
            UserCountriesListResponse(userList2),
            userList1.countries.intersect(userList2.countries)
        )
    }
}
