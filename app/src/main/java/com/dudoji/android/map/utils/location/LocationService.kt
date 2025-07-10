package com.dudoji.android.map.utils.location

import android.location.Location
import com.google.android.gms.location.LocationResult

interface LocationService {
    companion object{
        const val LOCATION_CALLBACK_INTERVAL = 1000L
        var lastLocation: Location? = null //제일 최근 위치 저장

        // method to check if the location is close to the last known location
        fun isCloseToLastLocation(location: Location, distance: Float): Boolean {
            return (lastLocation?.distanceTo(location) ?: Float.MAX_VALUE) < distance
        }

        fun getLastLatLng(): Pair<Double, Double> {
            //마지막 위치 정보(위도, 경도)를 반환하며 없으면 0,0 리턴함
            return lastLocation?.let {Pair(it.latitude, it.longitude)} ?: Pair(0.0, 0.0)
        }
    }
    fun setLocationCallback(callback: (LocationResult?)->Unit)
}