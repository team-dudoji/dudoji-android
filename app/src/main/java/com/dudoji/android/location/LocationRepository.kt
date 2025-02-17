package com.dudoji.android.location

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

data class RevealCircle(val lat: Double, val lng: Double, val radius: Double)

object LocationRepository {
    const val MAX_LOG_SIZE = 20 //20개로 설정했으니 여기도 해줌
    private val locationQueue: Queue<RevealCircle> = LinkedList()
    private val liveLocationData = MutableLiveData<String>()
    //
    fun addLocation(location: Location) {
        val log = "Lat: ${location.latitude}, Lng: ${location.longitude}"
        val revealCircle = RevealCircle(location.latitude, location.longitude, 100.0)
        synchronized(this) {
//            if (locationQueue.size >= MAX_LOG_SIZE) {
//                locationQueue.poll()
//            }
            locationQueue.add(revealCircle)
            liveLocationData.postValue(locationQueue.joinToString("\n"))
        }
    }

    fun getLocations() : List<RevealCircle>{
        return locationQueue.toList()
    }

    fun getLiveLocations(): LiveData<String> = liveLocationData
}
