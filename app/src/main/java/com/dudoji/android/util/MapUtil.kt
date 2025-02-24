package com.dudoji.android.util

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dudoji.android.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener

class MapUtil : GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener{
    var activity: AppCompatActivity
    lateinit var providerClient: FusedLocationProviderClient
    lateinit var apiClient: GoogleApiClient
    private var googleMap: GoogleMap? = null

    constructor(activity: AppCompatActivity) {
        this.activity = activity
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

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
            !== PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            )
        } else {
            apiClient.connect()
        }
    }

    // Prepare Map
    // Initialize Google Map Api
    fun prepareMap() {
        (activity.supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment)!!.getMapAsync(
            activity as OnMapReadyCallback?
        )
    }

    // Setup Location Services
    // Initialize Google Map Api
    fun setupLocationServices() {
        providerClient = LocationServices.getFusedLocationProviderClient(activity)
        apiClient = GoogleApiClient.Builder(activity)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
    }

    // Move Map to Current Location
    fun moveMapToCurrentLocation(googleMap: GoogleMap?){
        if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
            === PackageManager.PERMISSION_GRANTED){
            providerClient.lastLocation.addOnSuccessListener(
                activity,
                object : OnSuccessListener<Location>{
                    override fun onSuccess(p0: Location?) {
                        p0?.let{
                            val latitude = p0.latitude
                            val longitude = p0.longitude
                            moveMap(latitude, longitude, googleMap)
                        }
                    }
                }
            )
            apiClient.disconnect()
        }
    }

    // Move Map with latitude and longitude
    fun moveMap(latitude: Double, longitude: Double, googleMap: GoogleMap?){
        val latLng = LatLng(latitude, longitude)
        val position: CameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(16f)
            .build()
        googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    // Set Google Map by MapActivity
    fun setGoogleMap(map: GoogleMap?) {
        this.googleMap = map
    }

    override fun onConnected(p0: Bundle?) {
        moveMapToCurrentLocation(googleMap)
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }
}