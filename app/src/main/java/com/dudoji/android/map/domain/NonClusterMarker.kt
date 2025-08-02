package com.dudoji.android.map.domain

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

abstract class NonClusterMarker(
    val lat: Double,
    val lng: Double,
    val iconUrl: String,
) {
    fun toMarkerOptions(): MarkerOptions {
        return MarkerOptions()
            .position(LatLng(lat, lng))
            .zIndex(10f)
    }
}