package com.dudoji.android.map.utils.location

import android.location.Location
import com.dudoji.android.config.LOCATION_UPDATE_THRESHOLD

object LocationCallbackFilter {

    val lastLocation: Location = Location("")

    fun isSameLocation(location: Location): Boolean {
        val isSameLocation = lastLocation.distanceTo(location) < LOCATION_UPDATE_THRESHOLD

        lastLocation.latitude = location.latitude
        lastLocation.longitude = location.longitude

        return isSameLocation
    }
}