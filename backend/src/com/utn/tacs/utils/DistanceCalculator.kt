package com.utn.tacs.utils

import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

//Calculates the distance between two positions on earth. Math formulas are obtained from here https://www.geodatasource.com/developers/java

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

fun isDistanceLowerThan(lat1: Double, lon1: Double, lat2: Double, lon2: Double, maxDistance: Double): Boolean {
    return getDistanceBetween(lat1, lon1, lat2, lon2) <= maxDistance
}
