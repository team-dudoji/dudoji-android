package com.dudoji.android.map.domain

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

abstract class NonClusterMarker(
    open val lat: Double,
    open val lng: Double,
    val iconUrl: String,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NonClusterMarker) return false
        return lat == other.lat && lng == other.lng
    }

    override fun hashCode(): Int {
        var result = lat.hashCode()
        result = 31 * result + lng.hashCode()
        return result
    }

    fun toMarkerOptions(): MarkerOptions {
        return MarkerOptions()
            .position(LatLng(lat, lng))
            .zIndex(10f)
    }
}