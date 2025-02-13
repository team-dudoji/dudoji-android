package com.dudoji.android.map

import android.os.Bundle
import com.dudoji.android.MainActivity
import com.dudoji.android.R
import com.dudoji.android.model.mapsection.MapSectionManager
import com.dudoji.android.util.MapUtil
import com.dudoji.android.util.mapsection.MapSectionParser
import com.dudoji.android.util.tile.MaskTileProvider
import com.dudoji.android.util.tile.mask.IMaskTileMaker
import com.dudoji.android.util.tile.mask.MapSectionMaskTileMaker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.gms.maps.model.TileOverlayOptions
import com.dudoji.android.NavigatableActivity

const val MIN_ZOOM = 10f
const val MAX_ZOOM = 20f

class MapActivity :  NavigatableActivity(), OnMapReadyCallback {

    override val navigationItems = mapOf(
        R.id.mapFragment to null, // 기본 맵 화면
        R.id.homeFragment to MainActivity::class.java
    )

    override val defaultSelectedItemId = R.id.mapFragment

    private lateinit var bottomNav: BottomNavigationView

    var googleMap: GoogleMap? = null
    var mapUtil: MapUtil = MapUtil(this)

    fun setTileMaskTileMaker(maskTileMaker: IMaskTileMaker) {
        val tileOverlayOptions = TileOverlayOptions().tileProvider(MaskTileProvider(maskTileMaker))
        googleMap?.addTileOverlay(tileOverlayOptions)
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
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
        mapUtil.setGoogleMap(p0)
        p0?.setMinZoomPreference(MIN_ZOOM)  // set zoom level bounds
        p0?.setMaxZoomPreference(MAX_ZOOM)
        // apply tile overlay to google map
        setTileMaskTileMaker(
            MapSectionMaskTileMaker(MapSectionManager(MapSectionParser().testParseMapSections(resources)))
        )
    }

    override fun onResume() {
        super.onResume()
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.navigationView)
        bottomNav.selectedItemId = defaultSelectedItemId
    }


}