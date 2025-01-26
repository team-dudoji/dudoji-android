package com.dudoji.android.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R
import com.dudoji.android.util.MapUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class MapActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {


    var googleMap: GoogleMap? = null
    var mapUtil: MapUtil = MapUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapUtil.requestLocationPermission()
        mapUtil.prepareMap()
        mapUtil.setupLocationServices()
    }




    override fun onConnected(p01: Bundle?){
        mapUtil.moveMapToCurrentLocation(googleMap)
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
    }
}