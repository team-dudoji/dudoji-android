package com.dudoji.android.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R
import com.dudoji.android.util.MapUtil
import com.dudoji.android.util.tile.MaskTileProvider
import com.dudoji.android.util.tile.mask.OpaqueMaskTileMaker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.TileOverlayOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    var googleMap: GoogleMap? = null
    var mapUtil: MapUtil = MapUtil(this)
    private var tileOverlayOptions : TileOverlayOptions =  TileOverlayOptions().tileProvider(MaskTileProvider(
        OpaqueMaskTileMaker()))

    fun setTileMaskTileMaker(maskTileMaker: OpaqueMaskTileMaker) {
        this.tileOverlayOptions = TileOverlayOptions().tileProvider(MaskTileProvider(maskTileMaker))
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

        // apply tile overlay to google map
        googleMap?.addTileOverlay(tileOverlayOptions)
    }
}