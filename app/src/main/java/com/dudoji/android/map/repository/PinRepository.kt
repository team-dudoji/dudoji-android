package com.dudoji.android.map.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.config.PIN_UPDATE_THRESHOLD
import com.dudoji.android.map.domain.Pin
import com.dudoji.android.map.utils.MapUtil
import com.dudoji.android.map.utils.pin.PinApplier
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

object PinRepository {
    private val pinList = mutableListOf<Pin>()
    private lateinit var googleMap: GoogleMap

    private var lastPinUpdatedLatLng: LatLng? = null

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadPins(latLng: LatLng, radius: Double): Boolean{
        if (lastPinUpdatedLatLng == null ||
            MapUtil.distanceBetween(lastPinUpdatedLatLng!!, latLng) > PIN_UPDATE_THRESHOLD) {
            val response = RetrofitClient.pinApiService.getPins(radius.toInt(), latLng.latitude, latLng.longitude)
            if (response.isSuccessful) {
                val pins = response.body()
                pinList.clear()
                pinList.addAll(pins?.map { pinDto -> pinDto.toDomain() } ?: emptyList())
                lastPinUpdatedLatLng = latLng
                return true
            } else {
                Log.e("PinRepository", "Failed to fetch pins: ${response.errorBody()?.string()}")
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addPin(pin: Pin): Boolean {
        val response = RetrofitClient.pinApiService.createPin(pin.toPinDto())
        if (response.isSuccessful) {
            pinList.add(pin)
            return true
        }
        Log.e("PinRepository", "Failed to add pin: ${response.errorBody()?.string()}")
        return false
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
}