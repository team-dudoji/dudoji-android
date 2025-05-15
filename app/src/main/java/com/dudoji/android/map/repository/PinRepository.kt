package com.dudoji.android.map.repository

import com.dudoji.android.map.domain.Pin
import com.dudoji.android.map.utils.pin.PinApplier
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

object PinRepository {
    private val pinList = mutableListOf<Pin>()
    private lateinit var googleMap: GoogleMap

    suspend fun getPins(latLng: LatLng, radius: Double): List<Pin> {
        
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

    suspend fun addPin(pin: Pin) {
        pinList.add(pin)
        RetrofitClient.pinApiService.createPin(pin.toPinDto())
    }

    fun updateFilter(pinApplier: PinApplier) {
//        pinApplier.clearPins()
//        val visibleFriendId: HashSet<Long> = FollowRepository.getFollowings()
//            .filter { it.isVisible }
//            .map { it.user.id }
//            .toHashSet()
//        Log.d("PinRepository", "Visible Friend IDs: $visibleFriendId")
//        pinList.forEach { pin ->
//            if (visibleFriendId.contains(pin.userId)) {
//                pinApplier.applyPin(pin)
//            }
//        }
    }

    fun getPins(): List<Pin> {
        return pinList
    }

    fun clearPins() {
        pinList.clear()
    }
}