package com.dudoji.android.pin.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.config.LANDMARK_PIN_RADIUS
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.map.repository.RangeSearchDataSource
import com.dudoji.android.pin.api.dto.PinRequestDto
import com.dudoji.android.pin.api.dto.PinResponseDto
import com.dudoji.android.pin.api.dto.PinSkinUpdateRequestDto
import com.dudoji.android.pin.domain.Pin

@RequiresApi(Build.VERSION_CODES.O)
object PinRepository: RangeSearchDataSource<PinResponseDto, Pin>(RetrofitClient.pinApiService) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getLandmarkPins(landmark: Landmark): List<Pin> {
        val response = RetrofitClient.pinApiService.getRangeSearchResults(
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
    suspend fun addPin(pin: PinRequestDto): Boolean {
        val response = RetrofitClient.pinApiService.createPin(pin)
        if (response.isSuccessful) {
            dataList.add(response.body()?.toDomain()!!)
            return true
        }
        Log.e("PinRepository", "Failed to add pin: ${response.errorBody()?.string()}")
        return false
    }

    fun getPins(): List<Pin> {
        return dataList
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