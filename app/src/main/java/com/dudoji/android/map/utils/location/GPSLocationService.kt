package com.dudoji.android.map.utils.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.dudoji.android.map.utils.location.LocationService.Companion.LOCATION_CALLBACK_INTERVAL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

// Location을 지속적으로 받아오는 Class
class GPSLocationService: LocationService { // 위치 서비스 초기화 때문에 context를 private으로 저장

    private var context: Context //외부에서 전달받은 Context를 저장하는 변수
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 위치 정보 제공
    private lateinit var locationListener: LocationListener // 위치 업데이트 콜백
    private val handler: Handler // 메인 스레드에서 작업을 예약하기 위한 Handler 객체


    constructor(context: Context) { // 생성자 // 초기화 context를 받기 위해 추가
        this.context = context//conext를 인자로 받아 객체를 생성하는 생성자
        handler = Handler(Looper.getMainLooper())
        setupLocationComponents()
    }

    // location callback을 설정하는 메서드
    override fun setLocationCallback(callback: (Location)->Unit) {
        locationListener =  object: LocationListener {
            override fun onLocationChanged(location: Location) {
                callback(location)
            }
        }

        val locationRequest = LocationRequest.Builder(1000L)
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