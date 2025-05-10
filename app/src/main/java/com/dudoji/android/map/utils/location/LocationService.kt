package com.dudoji.android.map.utils.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

// Location을 지속적으로 받아오는 Class
class LocationService { // 위치 서비스 초기화 때문에 context를 private으로 저장
    companion object{
        const val LOCATION_CALLBACK_INTERVAL = 1000L
        private var lastLocation: Location? = null //제일 최근 위치 저장

        // method to check if the location is close to the last known location
        fun isCloseToLastLocation(location: Location, distance: Float): Boolean {
            return lastLocation?.distanceTo(location) ?: Float.MAX_VALUE < distance
        }
    }

    private var context: Context //외부에서 전달받은 Context를 저장하는 변수
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 위치 정보 제공
    private lateinit var locationCallback: LocationCallback // 위치 업데이트 콜백
    private val handler: Handler // 메인 스레드에서 작업을 예약하기 위한 Handler 객체


    constructor(context: Context) { // 생성자 // 초기화 context를 받기 위해 추가
        this.context = context//conext를 인자로 받아 객체를 생성하는 생성자
        handler = Handler(Looper.getMainLooper())
        setupLocationComponents()
    }

    // location callback을 설정하는 메서드
    fun setLocationCallback(callback: (LocationResult?)->Unit) {
        locationCallback =  object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult){
                lastLocation = locationResult.lastLocation
                callback(locationResult)
            }
        }

        val locationRequest = LocationRequest.create().apply { //LocationRequest 객체 생성, 설정 블록 적용
            interval = LOCATION_CALLBACK_INTERVAL
            fastestInterval = LOCATION_CALLBACK_INTERVAL / 2 // 업데이트 주기가 너무 빠르지 않도록 갱신 속도의 절반을 최대 속도로 설정
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY// 고정밀 위치 정보 획득
        }

        //위치 권한 부여 여부 확인
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            throw SecurityException("Location permission ACCESS_FINE_LOCATION is not granted")// 권한 없으면 메세지 띄워줌
        }

        fusedLocationClient.requestLocationUpdates(//위치 업데이트 요청
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun getLastLatLng(): Pair<Double, Double> {
        //마지막 위치 정보(위도, 경도)를 반환하며 없으면 0,0 리턴함
         return lastLocation?.let {Pair(it.latitude, it.longitude)} ?: Pair(0.0, 0.0)
    }

    private fun setupLocationComponents() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context) // LocationServices를 사용해 context를 기반으로 초기화
        locationCallback = object : LocationCallback(){ //로케이션 콜백 로직 구현
            override fun onLocationResult(locationResult: LocationResult){ //위치 업데이트가 들어올 때 호출되는 콜백 메서드
                locationResult?.let {//location result가 null이 아닐때 실행
                    lastLocation = it.lastLocation //최신 위치 갱신
                }
            }
        }
    }

}