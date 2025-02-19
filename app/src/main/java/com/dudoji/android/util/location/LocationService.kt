package com.dudoji.android.util.location

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.LinkedList
import java.util.Queue

// Location을 지속적으로 받아오는 Class
class LocationService {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val locationQueue: Queue<String> = LinkedList()
    private lateinit var locationTextView: TextView
    private lateinit var bottomNav: BottomNavigationView
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 3000L // 3초마다 갱신

    constructor() { // 생성자 // 초기화
        // Location을 받아오는 로직을 불러오기 위해서 초기화하는 로직들 다 함수로 등록
        setupLocationComponents()
    }

    fun setLocationCallback(callback: LocationCallback) {
        // Location을 받아오는 callback을 설정한다.
        // startLocationUpdates()를 변형해서 구현
    }

    fun getLastLatLng(): Pair<Double, Double> {
        // 마지막 location을 pair로 리턴한다.
         return Pair(0.0, 0.0)
    }

    private fun setupLocationComponents() {
        // TODO: Location을 받아오는 client를 초기화한다.
    }

}