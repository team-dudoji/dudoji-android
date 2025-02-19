package com.dudoji.android.map

import android.os.Bundle
import com.dudoji.android.MainActivity
import com.dudoji.android.NavigatableActivity
import com.dudoji.android.R
import com.dudoji.android.model.mapsection.MapSectionManager
import com.dudoji.android.util.MapUtil
import com.dudoji.android.util.tile.MaskTileProvider
import com.dudoji.android.util.tile.mask.IMaskTileMaker
import com.dudoji.android.util.tile.mask.MapSectionMaskTileMaker
import com.dudoji.android.util.tile.mask.PositionsMaskTileMaker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

const val MIN_ZOOM = 10f
const val MAX_ZOOM = 20f

class MapActivity :  NavigatableActivity(), OnMapReadyCallback {

    override val navigationItems = mapOf(
        R.id.mapFragment to null, // 기본 맵 화면
        R.id.homeFragment to MainActivity::class.java,
//        R.id.locationFragment to LocationActivity::class.java
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

        // TODO - Location Service를 초기화 변수로 저장해놓음
        // 그리고 CallBack 등록
        // 아래 형식으로 인자에 넣어주면 될듯 //
//        호출해야하는함수(object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                locationResult.lastLocation?.let {
//                    updateLocationLog(it) // TODO 안에 로직도 수정, RevealCircleRepository에 등록
                                            // TODO Google Map에 위치 수정
//                    RevealCircleRepository.addLocation(it)
//                }
//            }
//        })
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