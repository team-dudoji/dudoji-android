package com.dudoji.android.map.utils

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dudoji.android.R
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

class MapUtil(val activity: AppCompatActivity) {

    lateinit var providerClient: FusedLocationProviderClient
    lateinit var apiClient: GoogleApiClient
    private var googleMap: GoogleMap? = null

    private val mapView by lazy {
        activity.findViewById<MapView>(R.id.mapView)
    }

    companion object {
        fun distanceBetween(p1: LatLng, p2: LatLng): Double {
            val results = FloatArray(1)
            Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
            return results[0].toDouble()
        }
    }

    // Permission Request for Location
    // Initialize Google Map Api
    fun requestLocationPermission() {
        val requestPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (it.all { permission -> permission.value == true }) {
                apiClient.connect()
            } else {
                Toast.makeText(activity, "권한 거부", Toast.LENGTH_SHORT).show()
            }
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            !== PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            )
        } else {
            apiClient.connect()
        }
    }

    // Prepare Map
    // Initialize Google Map Api
    fun prepareMap(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(
            activity as OnMapReadyCallback
        )
    }

    // Setup Location Services
    // Initialize Google Map Api
    fun setupLocationServices() {
        providerClient = LocationServices.getFusedLocationProviderClient(activity)
        apiClient = GoogleApiClient.Builder(activity)
            .addApi(LocationServices.API)
            .build()
    }

    // Set Google Map by MapActivity
    fun setGoogleMap(map: GoogleMap?) {
        this.googleMap = map
    }
}