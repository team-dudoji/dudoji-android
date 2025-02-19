package com.dudoji.android.repository

import android.location.Location
import java.util.*

const val LOCATION_SYSTEM_CHANGE_WARNING_TEXT = "Changed To Different Location System"

data class RevealCircle(val lat: Double, val lng: Double, val radius: Double)

const val MAX_LOG_SIZE = 20 //20개로 설정했으니 여기도 해줌

object RevealCircleRepository {

    val revealCircleQueue: Queue<RevealCircle> = LinkedList()

    fun addLocation(location: Location) {
        val revealCircle = RevealCircle(location.latitude, location.longitude, 100.0)
        synchronized(this) {
//            if (locationQueue.size >= MAX_LOG_SIZE) {
//                locationQueue.poll()
//            }
            revealCircleQueue.add(revealCircle)
        }
    }

    fun getLocations() : List<RevealCircle>{
        return revealCircleQueue.toList()
    }
}
