package com.dudoji.android.map

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.dudoji.android.MainActivity
import com.dudoji.android.NavigatableActivity
import com.dudoji.android.R
import com.dudoji.android.model.MapSectionManager
import com.dudoji.android.repository.RevealCircleRepository
import com.dudoji.android.util.MapUtil
import com.dudoji.android.util.location.LocationCallbackFilter
import com.dudoji.android.util.location.LocationService
import com.dudoji.android.util.tile.MaskTileProvider
import com.dudoji.android.util.tile.mask.IMaskTileMaker
import com.dudoji.android.util.tile.mask.MapSectionMaskTileMaker
import com.dudoji.android.util.tile.mask.PositionsMaskTileMaker
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

const val MIN_ZOOM = 10f
const val MAX_ZOOM = 20f
const val TILE_OVERLAY_LOADING_TIME = 700L // ms

class MapActivity :  NavigatableActivity(), OnMapReadyCallback {

    override val navigationItems = mapOf(
        R.id.mapFragment to null, // 기본 맵 화면
        R.id.homeFragment to MainActivity::class.java,
    )

    override val defaultSelectedItemId = R.id.mapFragment

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var locationService: LocationService //로케이션 서비스 변수 추가

    var googleMap: GoogleMap? = null
    var mapUtil: MapUtil = MapUtil(this)
    var marker: Marker? = null

    val numOfTileOverlay = 2
    var indexOfTileOverlay = 0
    val tileOverlays: MutableList<TileOverlay> = mutableListOf()

    fun setTileMaskTileMaker(maskTileMaker: IMaskTileMaker) {
        val tileOverlayOptions = TileOverlayOptions().tileProvider(MaskTileProvider(maskTileMaker))
        for (i in 0 until numOfTileOverlay) {
            tileOverlays.add(googleMap?.addTileOverlay(tileOverlayOptions)!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapUtil.setupLocationServices()
        mapUtil.requestLocationPermission()
        mapUtil.prepareMap()

        bottomNav = findViewById(R.id.navigationView)

        bottomNav.selectedItemId = R.id.mapFragment

        setupBottomNavigation(findViewById(R.id.navigationView))

        locationService = LocationService(this)

        setupLocationUpdates() // Setup location updates Callback
    }


    private fun setupLocationUpdates(){
        locationService.setLocationCallback(object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?){
                locationResult?.lastLocation?.let{
                    Log.d("MapActivity", "location callback: Location Updated: ${it.latitude}, ${it.longitude}")
                    if (!LocationCallbackFilter.isSameLocation(it)) {
                        Log.d("MapActivity", "location callback: Location is Saved")
                        RevealCircleRepository.addLocation(it)
                        updateLocationOnMap(it)
                    }
                }
            }
        })
    }

    // Update location on map
    private fun updateLocationOnMap(location: Location){
        val lat = location.latitude
        val lng = location.longitude

        if (lat != 0.0 && lng != 0.0) {
            val latLng = LatLng(lat, lng) // LatLng 객체로 변환
            if (marker == null) {
                marker = googleMap?.addMarker(MarkerOptions().position(latLng).title("User"))
            }

            marker?.position = latLng
            updateMap()
            googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    fun updateMap(){
        tileOverlays[indexOfTileOverlay].clearTileCache()
        indexOfTileOverlay = (indexOfTileOverlay + 1) % numOfTileOverlay

        Handler().postDelayed({
            tileOverlays[indexOfTileOverlay].clearTileCache()
            indexOfTileOverlay = (indexOfTileOverlay + 1) % numOfTileOverlay
        }, TILE_OVERLAY_LOADING_TIME)
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
        mapUtil.setGoogleMap(p0)
        p0?.setMinZoomPreference(MIN_ZOOM)  // set zoom level bounds
        p0?.setMaxZoomPreference(MAX_ZOOM)
        
        // apply tile overlay to google map
        setTileMaskTileMaker(PositionsMaskTileMaker(
            MapSectionMaskTileMaker(
                MapSectionManager(
                    listOf()
                )
            )
        ))
    }
}