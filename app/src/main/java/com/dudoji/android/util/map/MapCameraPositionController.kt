package com.dudoji.android.util.map

import android.location.Location
import com.dudoji.android.config.DEFAULT_ZOOM_LEVEL
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay

class MapCameraPositionController(private val googleMap: GoogleMap) {

    var isAttached = true
    var location: Location = Location("")

    suspend fun update() {
        if (!isAttached) return // if not attached, do nothing
        moveCameraPosition(location.latitude, location.longitude, DEFAULT_ZOOM_LEVEL)
        delay(100)
    }

    fun setAttach(isAttached: Boolean){
        this.isAttached = isAttached
    }

    fun updateLocation(location: Location) {
//        if (!_isAttached) return // if not attached, do nothing
        this.location = location
    }

    // Move Map with latitude and longitude
    fun moveCameraPosition(latitude: Double, longitude: Double, zoomLevel: Float){
        val latLng = LatLng(latitude, longitude)
        val position: CameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(zoomLevel)
            .build()
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))
    }
}


