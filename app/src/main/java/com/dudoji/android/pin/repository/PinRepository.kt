package com.dudoji.android.pin.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.config.PIN_UPDATE_THRESHOLD
import com.dudoji.android.map.utils.MapUtil
import com.dudoji.android.pin.domain.Pin
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import okhttp3.MultipartBody

object PinRepository {
    private val pinList = mutableListOf<Pin>()
    private lateinit var googleMap: GoogleMap

    private var lastPinUpdatedLatLng: LatLng? = null

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
                Log.e("PinRepository", "Failed to fetch pins: ${response.errorBody()?.string()}")
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addPin(pin: Pin, multipartBody: MultipartBody.Part): Boolean {
        val imageResponse = RetrofitClient.pinApiService.uploadImage(multipartBody)
        if (!imageResponse.isSuccessful) {
            Log.e("PinRepository", "Failed to upload image: ${imageResponse.errorBody()?.string()}")
            return false
        }

        val response = RetrofitClient.pinApiService.createPin(pin.toPinRequestDto(imageResponse.body().toString()))
        if (response.isSuccessful) {
            pinList.add(pin)
            return true
        }
        Log.e("PinRepository", "Failed to add pin: ${response.errorBody()?.string()}")
        return false
    }

    fun getPins(): List<Pin> {
        return pinList
    }

}