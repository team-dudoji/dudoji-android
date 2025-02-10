package com.dudoji.android.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R
import com.dudoji.android.util.MapUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class MapActivity : AppCompatActivity(), OnMapReadyCallback {


    var googleMap: GoogleMap? = null
    var mapUtil: MapUtil = MapUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapUtil.setupLocationServices()
        mapUtil.requestLocationPermission()
        mapUtil.prepareMap()
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        mapUtil.setGoogleMap(p0)
    }
}