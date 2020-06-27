package com.utn.tacs.utils

import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

public object DistanceCalculator {
    /**
     * Get distance between two coordinate points by lat and long
     * Calculates the distance between two positions on earth. Math formulas are obtained from here
     *
     * @param lat1 Double
     * @param lon1 Double
     * @param lat2 Double
     * @param lon2 Double
     * @return Double
     * @see https://www.geodatasource.com/developers/java
     */
    fun getDistanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        return if ((lat1 == lat2) && (lon1 == lon2)) {
            0.0
        } else {
            val theta = lon1 - lon2;
            var dist = sin(toRadians(lat1)) * sin(toRadians(lat2)) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * cos(toRadians(theta));
            dist = acos(dist);
            dist = toDegrees(dist);
            dist *= 60 * 1.1515;
            (dist);
        }
    }

    /**
     * Measure the distance between two points and checks if distance is between max distance bundles
     *
     * @param lat1 Double
     * @param lon1 Double
     * @param lat2 Double
     * @param lon2 Double
     * @param maxDistance Double
     * @return Boolean
     */
    fun isDistanceLowerThan(lat1: Double, lon1: Double, lat2: Double, lon2: Double, maxDistance: Double): Boolean {
        return getDistanceBetween(lat1, lon1, lat2, lon2) <= maxDistance
    }
}