package com.dudoji.android.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dudoji.android.MainActivity
import com.dudoji.android.NavigatableActivity
import com.dudoji.android.R
import com.dudoji.android.map.MapActivity
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.LinkedList
import java.util.Queue

class LocationActivity : NavigatableActivity() {

    override val defaultSelectedItemId = R.id.locationFragment

    override val navigationItems = mapOf(
        R.id.homeFragment to MainActivity::class.java,
        R.id.mapFragment to MapActivity::class.java,
        R.id.locationFragment to null
    )

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val locationQueue: Queue<String> = LinkedList()
    private lateinit var locationTextView: TextView
    private lateinit var bottomNav: BottomNavigationView
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 3000L // 3초마다 찍기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // 서비스 시작
        Intent(this, LocationService::class.java).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
            } else {
                startService(it)
            }
        }

        // 라이브데이터 관찰 설정
        LocationRepository.getLiveLocations().observe(this) { logs ->
            locationTextView.text = logs
        }

        locationTextView = findViewById(R.id.locationTextView)
        bottomNav = findViewById(R.id.navigationView)

        setupBottomNavigation(bottomNav)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            requestLocationPermission()
            return
        }

        val locationRequest = LocationRequest.create().apply {
            interval = updateInterval
            fastestInterval = updateInterval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { updateLocationLog(it) }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            showPermissionDeniedToast()
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (hasLocationPermission()) {
                    try {
                        fusedLocationClient.lastLocation?.addOnSuccessListener { location ->
                            location?.let { updateLocationLog(it) }
                        }
                    } catch (e: SecurityException) {
                        showPermissionDeniedToast()
                    }
                } else {
                    requestLocationPermission()
                }
                handler.postDelayed(this, updateInterval)
            }
        }, updateInterval)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun showPermissionDeniedToast() {
        Toast.makeText(
            this,
            "Location permission denied",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                showPermissionDeniedToast()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateLocationLog(location: Location) {
        val log = "Lat: ${location.latitude}, Lng: ${location.longitude}"

        if (locationQueue.size >= 20) {
            locationQueue.poll() // 가장 오래된 놈 제거
        }

        locationQueue.add(log)

        // 화면 업뎃
        locationTextView.text = locationQueue.joinToString("\n")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            stopService(Intent(this, LocationService::class.java))
        }
    }

    // 위치 권한 확인
    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
