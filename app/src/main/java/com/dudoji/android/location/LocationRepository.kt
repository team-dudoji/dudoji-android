package com.dudoji.android.location

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

object LocationRepository {
    private const val MAX_LOG_SIZE = 20 //20개로 설정했으니 여기도 해줌
    private val locationQueue: Queue<String> = LinkedList()
    private val liveLocationData = MutableLiveData<String>()

    fun addLocation(location: Location) {
        val log = "Lat: ${location.latitude}, Lng: ${location.longitude}"
        synchronized(this) {
            if (locationQueue.size >= MAX_LOG_SIZE) {
                locationQueue.poll()
            }
            locationQueue.add(log)
            liveLocationData.postValue(locationQueue.joinToString("\n"))
        }
    }

    fun getLiveLocations(): LiveData<String> = liveLocationData
}
