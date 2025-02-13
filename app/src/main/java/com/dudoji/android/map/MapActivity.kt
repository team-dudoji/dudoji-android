package com.dudoji.android.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R
import com.dudoji.android.model.mapsection.MapSectionManager
import com.dudoji.android.util.MapUtil
import com.dudoji.android.util.mapsection.MapSectionParser
import com.dudoji.android.util.tile.MaskTileProvider
import com.dudoji.android.util.tile.mask.IMaskTileMaker
import com.dudoji.android.util.tile.mask.MapSectionMaskTileMaker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.TileOverlayOptions

const val MIN_ZOOM = 10f
const val MAX_ZOOM = 20f

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

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
}