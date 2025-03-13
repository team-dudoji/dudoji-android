package com.dudoji.android.util.map

import android.location.Location
import android.widget.Button
import com.dudoji.android.config.DEFAULT_ZOOM_LEVEL
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay

// controller for map camera position, location marker, and location button
class MapCameraPositionController(private val googleMap: GoogleMap, private val locationButton: Button) {

    private var marker: Marker? = null

    var isAttached = true
    var lastLocation: Location = Location("")
    var location: Location = Location("")

    // Update interval is 100ms
    suspend fun update() {
        delay(100)

        if (!isAttached) return // if not attached, do nothing

        if (Math.abs(lastLocation.latitude - googleMap.cameraPosition.target.latitude) < 0.0001 &&
            Math.abs(lastLocation.longitude - googleMap.cameraPosition.target.longitude) < 0.0001) {
            setAttach(true)
            lastLocation = location
        } else {
            lastLocation = location
            setAttach(false)
            return
        }

        moveCameraPosition(location.latitude, location.longitude, DEFAULT_ZOOM_LEVEL)
    }

    fun setAttach(isAttached: Boolean){
        if (isAttached == this.isAttached) return

        if (isAttached) {
            lastLocation = location
            moveCameraPosition(location.latitude, location.longitude, DEFAULT_ZOOM_LEVEL)
        }
        this.isAttached = isAttached

        locationButton.visibility = if (isAttached) Button.GONE else Button.VISIBLE
    }

    fun updateLocation(location: Location) {
        this.location = location

        // update location too
        val latLng = LatLng(location.latitude, location.longitude)
        if (marker == null) {
            marker = googleMap.addMarker(MarkerOptions().position(latLng).title("User"))
        }
        marker?.position = latLng
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


