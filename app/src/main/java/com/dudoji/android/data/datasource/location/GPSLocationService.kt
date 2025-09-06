package com.dudoji.android.data.datasource.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.dudoji.android.data.datasource.location.LocationService.Companion.LOCATION_CALLBACK_INTERVAL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GPSLocationService @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationService {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationListener: LocationListener

    init {
        setupLocationComponents()
    }

    // location callback을 설정하는 메서드
    override fun setLocationCallback(callback: (Location)->Unit) {
        locationListener =  object: LocationListener {
            override fun onLocationChanged(location: Location) {
                callback(location)
            }
        }

        val locationRequest = LocationRequest.Builder(LOCATION_CALLBACK_INTERVAL)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(LOCATION_CALLBACK_INTERVAL / 2)
            .setMaxUpdateDelayMillis(LOCATION_CALLBACK_INTERVAL)
            .build()

        //위치 권한 부여 여부 확인
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            throw SecurityException("Location permission ACCESS_FINE_LOCATION is not granted")// 권한 없으면 메세지 띄워줌
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationListener,
            Looper.getMainLooper()
        )
    }

    private fun setupLocationComponents() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context) // LocationServices를 사용해 context를 기반으로 초기화
    }
}