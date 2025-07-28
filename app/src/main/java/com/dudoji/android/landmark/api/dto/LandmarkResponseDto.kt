package com.dudoji.android.landmark.api.dto

import com.dudoji.android.landmark.domain.Landmark

data class LandmarkResponseDto(
    val landmarkId: Long,
    val lat: Double,
    val lng: Double,
    val placeName: String,
    val address: String,
    val content: String,
    val imageUrl: String,
    val radius: Double,
    val isDetected: Boolean
) {
    fun toDomain(): Landmark {
        return Landmark(
            landmarkId = landmarkId,
            lat = lat,
            lng = lng,
            placeName = placeName,
            address = address,
            content = content,
            imageUrl = imageUrl,
            radius = radius,
            isDetected = isDetected
        )
    }
}