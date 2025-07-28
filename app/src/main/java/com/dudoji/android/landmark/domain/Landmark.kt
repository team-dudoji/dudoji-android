package com.dudoji.android.landmark.domain

class Landmark {
    val landmarkId: Long
    val lat: Double
    val lng: Double
    val placeName: String
    val address: String
    val content: String
    val imageUrl: String
    val radius: Double
    var isDetected: Boolean

    constructor(
        landmarkId: Long,
        lat: Double,
        lng: Double,
        placeName: String,
        address: String,
        content: String,
        imageUrl: String,
        radius: Double,
        isDetected: Boolean
    ) {
        this.landmarkId = landmarkId
        this.lat = lat
        this.lng = lng
        this.placeName = placeName
        this.address = address
        this.content = content
        this.imageUrl = imageUrl
        this.radius = radius
        this.isDetected = isDetected
    }
}