package com.dudoji.android.landmark.api.dto

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.network.api.dto.BaseDto

data class LandmarkResponseDto(
    val landmarkId: Long,
    val lat: Double,
    val lng: Double,
    val placeName: String,
    val address: String,
    val content: String,
    val mapImageUrl: String,
    val detailImageUrl: String,
    val radius: Double,
    val isDetected: Boolean,
    val hashtags: List<String>?
): BaseDto<Landmark> {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun toDomain(): Landmark {
        Log.d("LandmarkResponseDto", "Converting to domain model: $landmarkId, $lat, $lng, $placeName, $address, $content, $mapImageUrl, $detailImageUrl, $radius, $isDetected, $hashtags")
        return Landmark(
            landmarkId = landmarkId,
            lat = lat,
            lng = lng,
            placeName = placeName,
            address = address,
            content = content,
            mapImageUrl = mapImageUrl,
            detailImageUrl = detailImageUrl,
            radius = radius,
            isDetected = isDetected,
            hashtags ?: emptyList()
        )
    }
}