package com.dudoji.android.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.dudoji.android.MainActivity
import com.dudoji.android.NavigatableActivity
import com.dudoji.android.R
import com.dudoji.android.map.MapActivity
import com.dudoji.android.repository.MAX_LOG_SIZE
import com.dudoji.android.repository.RevealCircleRepository
import com.dudoji.android.util.NetWorkUtil
import com.dudoji.android.util.PermissionUtil
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
    private val updateInterval = 3000L // 3초마다 갱신

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        initViews()
        setupLocationComponents()
        setLocationTestButton()
    }

    private fun initViews() { // 뷰를 초기화 한다.
        locationTextView = findViewById(R.id.locationTextView)
        bottomNav = findViewById(R.id.navigationView)
        setupBottomNavigation(bottomNav)
    }


    private fun setupLocationComponents() { // location을 받아오는 client를 초기화한다.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        // check permission
        if (!PermissionUtil.hasLocationPermission(this)) {
            PermissionUtil.requestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // location request create
        val locationRequest = LocationRequest.create().apply {
            interval = updateInterval
            fastestInterval = updateInterval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // location callback 정의
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    updateLocationLog(it)
                    RevealCircleRepository.addLocation(it)
                }
            }
        }

        try { // callback 등록
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            showPermissionDeniedToast()
        }

        // last location을 업데이트ㅍ
        handler.postDelayed({ checkLastLocation() }, updateInterval)
    }

    private fun checkLastLocation() {
        // permission check
        if (!PermissionUtil.hasLocationPermission(this)) {
            PermissionUtil.requestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { updateLocationLog(it) }
            }.addOnFailureListener {
                Toast.makeText(this, "위치 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            showPermissionDeniedToast()
        }

        handler.postDelayed({ checkLastLocation() }, updateInterval)
    }

    private fun setLocationTestButton(){
        val button = findViewById<Button>(R.id.sendButton)
        button.setOnClickListener({
            val jsonObject = NetWorkUtil().createRevealCirclesRequestJson()
            val path = "api/user/reveal_circles/save"
            NetWorkUtil().sendJsonToServer(path, jsonObject)
        })
    }


    private fun showPermissionDeniedToast() {
        Toast.makeText(this, "위치 불러오기 거부", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtil.handlePermissionResult(
            requestCode, LOCATION_PERMISSION_REQUEST_CODE, grantResults,
            { startLocationUpdates() }, { showPermissionDeniedToast() }
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateLocationLog(location: Location) {
        val log = "Lat: ${location.latitude}, Lng: ${location.longitude}"
        if (locationQueue.size >= MAX_LOG_SIZE) locationQueue.poll()
        locationQueue.add(log)
        locationTextView.text = locationQueue.joinToString("\n")
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
