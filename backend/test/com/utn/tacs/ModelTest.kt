package com.utn.tacs

import org.junit.Test
import org.litote.kmongo.newId


class ModelTest {

    @Test
    fun testUserCountriesListWrapper() {
        val userCountriesListWrapper = UserCountriesListWrapper(
            newId(), "name", setOf("AR")
        )

        val userCountriesListWrapper2 = UserCountriesListWrapper(
            newId(), "name"
        )
    }
}

