package com.utn.tacs.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.Test

class DistanceCalculatorKtTest {

    @Test
    fun testGetDistanceBetween() {
        assertEquals(8214.603166418585, DistanceCalculator.getDistanceBetween(-65.345, 23.33, 53.55, 22.4))
    }

    @Test
    fun testDistanceIsLowerThan() {
        assertTrue(DistanceCalculator.isDistanceLowerThan(-65.345, 23.33, 53.55, 22.4, 8300.00))
    }
}