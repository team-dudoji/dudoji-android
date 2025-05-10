package com.dudoji.android.map.repository

import android.util.Log
import com.dudoji.android.friend.repository.FriendRepository
import com.dudoji.android.map.domain.Pin
import com.dudoji.android.map.utils.pin.PinApplier
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
        suspend {
            RetrofitClient.pinApiService.createPin(pin.toPinDto())
        }
    }

    fun updateFilter(pinApplier: PinApplier) {
        pinApplier.clearPins()
        val visibleFriendId: HashSet<Long> = FriendRepository.getFriends()
            .filter { it.isVisible }
            .map { it.user.id }
            .toHashSet()
        Log.d("PinRepository", "Visible Friend IDs: $visibleFriendId")
        pinList.forEach { pin ->
            if (visibleFriendId.contains(pin.userId)) {
                pinApplier.applyPin(pin)
            }
        }
    }

    fun getPins(): List<Pin> {
        return pinList
    }

    fun clearPins() {
        pinList.clear()
    }


}