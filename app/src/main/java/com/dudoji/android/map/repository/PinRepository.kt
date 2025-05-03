package com.dudoji.android.map.repository

import com.dudoji.android.map.domain.Pin
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

object PinRepository {
    private val pinList = mutableListOf<Pin>()
    private lateinit var googleMap: GoogleMap

    fun getPins(latLng: LatLng, radius: Double): List<Pin> {
        return pinList.filter { pin ->
            val distance = FloatArray(1)
            android.location.Location.distanceBetween(
                latLng.latitude,
                latLng.longitude,
                pin.lat,
                pin.lng,
                distance
            )
            distance[0] <= radius
        }
    }

    fun addPin(pin: Pin) {
        pinList.add(pin)
    }

    fun getPins(): List<Pin> {
        return pinList
    }

    fun clearPins() {
        pinList.clear()
    }
}