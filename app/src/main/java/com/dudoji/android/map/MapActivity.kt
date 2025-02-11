package com.dudoji.android.map

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.MainActivity
import com.dudoji.android.R
import com.dudoji.android.util.MapUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.bottomnavigation.BottomNavigationView

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var bottomNav: BottomNavigationView

    var googleMap: GoogleMap? = null
    var mapUtil: MapUtil = MapUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapUtil.setupLocationServices()
        mapUtil.requestLocationPermission()
        mapUtil.prepareMap()

        bottomNav = findViewById(R.id.navigationView)
        setupBottomNavigation()

        bottomNav.selectedItemId = R.id.mapFragment
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
        mapUtil.setGoogleMap(p0)
    }

    //맵에서 메인 엑티비티 가기
    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mapFragment -> {
                    // 맵 화면 유지
                    true
                }
                R.id.homeFragment -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }


}