package com.dudoji.android.landmark.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.map.domain.NonClusterMarker
import java.io.Serializable

class Landmark : NonClusterMarker, Serializable {
    val landmarkId: Long
    val placeName: String
    val address: String
    val content: String
    val detailImageUrl: String
    val radius: Double
    var isDetected: Boolean
    val hashtags: List<String>

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(
        landmarkId: Long,
        lat: Double,
        lng: Double,
        placeName: String,
        address: String,
        content: String,
        mapImageUrl: String,
        detailImageUrl: String,
        radius: Double,
        isDetected: Boolean,
        hashtags: List<String>
    ) : super(lat, lng, mapImageUrl) {
        this.landmarkId = landmarkId
        this.placeName = placeName
        this.address = address
        this.content = content
        this.detailImageUrl = detailImageUrl
        this.radius = radius
        this.isDetected = isDetected
        this.hashtags = hashtags
    }
}