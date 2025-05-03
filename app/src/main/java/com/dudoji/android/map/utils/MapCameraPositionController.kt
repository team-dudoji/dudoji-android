package com.dudoji.android.map.utils

import android.location.Location
import android.widget.Button
import com.dudoji.android.R
import com.dudoji.android.config.DEFAULT_ZOOM_LEVEL
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay

// controller for map camera position, location marker, and location button
class MapCameraPositionController {

    private var marker: Marker? = null

    private val googleMap: GoogleMap
    private val locationButton: Button

    var isAttached = true
    var lastLocation: Location = Location("")
    var location: Location = Location("")
    private var bearing: Float = 0f
    private var markerIconToggler: MarkerIconToggler? = null

    constructor(googleMap: GoogleMap, locationButton: Button) {
        this.googleMap = googleMap
        this.locationButton = locationButton
    }

    fun setBearing(bearing: Float){
        this.bearing = bearing
        marker?.rotation = bearing
    }

    fun getSpeed(): Float {
        return location.speed
    }

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

    //방향 기능 추가 마커에
    fun updateLocation(location: Location) {
        this.location = location

        val latLng = LatLng(location.latitude, location.longitude)

        //화살표 추가
        if (marker == null) {
            val markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_main)
            marker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(markerIcon)
                    .anchor(0.5f, 0.5f) // 회전 중심을 마커의 중심으로 설정
                    .flat(true)         // 지도 평면을 따라 회전 가능하게 함
            )
            markerIconToggler = MarkerIconToggler(marker!!)
        }

        marker?.position = latLng
        marker?.rotation = bearing

        markerIconToggler?.setSpeed(location.speed)
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


