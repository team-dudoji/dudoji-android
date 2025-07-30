package com.dudoji.android.landmark.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.Serializable

class Landmark : Serializable {
    val landmarkId: Long
    val lat: Double
    val lng: Double
    val placeName: String
    val address: String
    val content: String
    val mapImageUrl: String
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
    ) {
        this.landmarkId = landmarkId
        this.lat = lat
        this.lng = lng
        this.placeName = placeName
        this.address = address
        this.content = content
        this.mapImageUrl = mapImageUrl
        this.detailImageUrl = detailImageUrl
        this.radius = radius
        this.isDetected = isDetected
        this.hashtags = hashtags
    }

    fun toMarkerOptions(): MarkerOptions {
        return MarkerOptions()
            .position(LatLng(lat, lng))
            .zIndex(10f)
    }
}