package com.dudoji.android.util.location

import android.location.Location

object LocationCallbackFilter {

    const val LOCATION_UPDATE_THRESHOLD = 5 // meter
    val lastLocation: Location = Location("")

    fun isSameLocation(location: Location): Boolean {
        val isSameLocation = lastLocation.distanceTo(location) < LOCATION_UPDATE_THRESHOLD

        lastLocation.latitude = location.latitude
        lastLocation.longitude = location.longitude

        return isSameLocation
    }
}