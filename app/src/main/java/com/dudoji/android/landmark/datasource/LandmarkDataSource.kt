package com.dudoji.android.landmark.datasource

import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.config.PIN_UPDATE_THRESHOLD
import com.dudoji.android.landmark.api.dto.LandmarkResponseDto
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.map.utils.MapUtil
import com.google.android.gms.maps.model.LatLng

object LandmarkDataSource {

    private val landmarkList = mutableListOf<Landmark>()

    private var lastLandmarkUpdatedLatLng: LatLng? = null

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadLandmark(latLng: LatLng, radius: Double): Boolean{

        if (lastLandmarkUpdatedLatLng == null ||
            MapUtil.distanceBetween(lastLandmarkUpdatedLatLng!!, latLng) > PIN_UPDATE_THRESHOLD) {
            val response = RetrofitClient.landmarkApiService.getLandmarks(
                radius.toInt(),
                latLng.latitude,
                latLng.longitude)
            if (response.isSuccessful) {
                val landmarks = response.body()

                landmarkList.clear()
                landmarkList.addAll(landmarks?.map(LandmarkResponseDto::toDomain)
                    ?: emptyList()
                )
                lastLandmarkUpdatedLatLng = latLng
                return true
            }
        }
        return false
    }

    fun getLandmarks(): List<Landmark> {
         return landmarkList
    }
}