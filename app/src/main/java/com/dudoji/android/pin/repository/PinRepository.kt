package com.dudoji.android.pin.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.config.LANDMARK_PIN_RADIUS
import com.dudoji.android.config.PIN_UPDATE_THRESHOLD
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.map.utils.MapUtil
import com.dudoji.android.pin.api.dto.PinRequestDto
import com.dudoji.android.pin.api.dto.PinResponseDto
import com.dudoji.android.pin.api.dto.PinSkinUpdateRequestDto
import com.dudoji.android.pin.domain.Pin
import com.google.android.gms.maps.model.LatLng

object PinRepository {
    val pinList = mutableListOf<Pin>()

    private var lastPinUpdatedLatLng: LatLng? = null

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getLandmarkPins(landmark: Landmark): List<Pin> {
        val response = RetrofitClient.pinApiService.getPins(
            LANDMARK_PIN_RADIUS,
            landmark.lat,
            landmark.lng
        )
        return if (response.isSuccessful) {
            response.body()?.map(PinResponseDto::toDomain) ?: emptyList()
        } else {
            Log.e("PinRepository", "Failed to fetch pins for landmark: ${response.errorBody()?.string()}")
            emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadPins(latLng: LatLng, radius: Double): Boolean{
        if (lastPinUpdatedLatLng == null ||
            MapUtil.distanceBetween(lastPinUpdatedLatLng!!, latLng) > PIN_UPDATE_THRESHOLD) {
            val response = RetrofitClient.pinApiService.getPins(
                radius.toInt(),
                latLng.latitude,
                latLng.longitude)
            if (response.isSuccessful) {
                val pins = response.body()

                pinList.clear()
                pinList.addAll(pins?.map { pinDto ->
                    pinDto.toDomain()
                } ?: emptyList()
                )
                lastPinUpdatedLatLng = latLng
                return true
            } else {
                Log.e("PinRepository", "Failed to fetch pins: ${response.message()}, error: ${response.errorBody()?.string()}")
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addPin(pin: PinRequestDto): Boolean {
        val response = RetrofitClient.pinApiService.createPin(pin)
        if (response.isSuccessful) {
            pinList.add(response.body()?.toDomain()!!)
            return true
        }
        Log.e("PinRepository", "Failed to add pin: ${response.errorBody()?.string()}")
        return false
    }

    fun getPins(): List<Pin> {
        return pinList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMyPins(): List<Pin> {
        val response = RetrofitClient.pinApiService.getMyPins()
        return if (response.isSuccessful) {
            response.body()?.map { it.toDomain() } ?: emptyList()
        } else {
            emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updatePinSkin(pinId: Long, newSkin: Long): Boolean {
        val response = RetrofitClient.pinApiService.updatePinSkin(
            pinId,
            PinSkinUpdateRequestDto(newSkin)
        )
        return response.isSuccessful
    }
}