package com.dudoji.android.map.activity

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.dudoji.android.NavigatableActivity
import com.dudoji.android.R
import com.dudoji.android.config.MAX_ZOOM
import com.dudoji.android.config.MIN_ZOOM
import com.dudoji.android.config.TILE_OVERLAY_LOADING_TIME
import com.dudoji.android.map.manager.MapSectionManager
import com.dudoji.android.map.domain.MarkerTag
import com.dudoji.android.map.domain.MarkerType
import com.dudoji.android.map.domain.Pin
import com.dudoji.android.map.manager.MapSectionManager
import com.dudoji.android.map.repository.MapSectionRepository
import com.dudoji.android.map.repository.RevealCircleRepository
import com.dudoji.android.map.utils.MapCameraPositionController
import com.dudoji.android.map.utils.MapDirectionController
import com.dudoji.android.map.utils.MapUtil
import com.dudoji.android.map.utils.location.LocationCallbackFilter
import com.dudoji.android.map.utils.location.LocationService
import com.dudoji.android.map.utils.pin.PinSetterController
import com.dudoji.android.map.utils.tile.MaskTileProvider
import com.dudoji.android.map.utils.tile.mask.IMaskTileMaker
import com.dudoji.android.map.utils.tile.mask.MapSectionMaskTileMaker
import com.dudoji.android.map.utils.tile.mask.PositionsMaskTileMaker
import com.dudoji.android.mypage.activity.MypageActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.dudoji.android.util.modal.Modal
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MapActivity : NavigatableActivity(), OnMapReadyCallback {

    override val navigationItems = mapOf(
        R.id.mapFragment to null, // 기본 맵 화면
        R.id.mypageFragment to MypageActivity::class.java
    )

    override val defaultSelectedItemId = R.id.mapFragment

    private lateinit var myLocationButton : Button;
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var locationService: LocationService //로케이션 서비스 변수 추가

    private lateinit var pinSetter: ImageView
    private lateinit var pinSetterController: PinSetterController
    private lateinit var pinDropZone: FrameLayout

    private var googleMap: GoogleMap? = null
    private var mapUtil: MapUtil = MapUtil(this)
    private lateinit var mapCameraPositionController : MapCameraPositionController

    private val numOfTileOverlay = 2
    private var indexOfTileOverlay = 0
    private val tileOverlays: MutableList<TileOverlay> = mutableListOf()

    private lateinit var maskTileMaker: IMaskTileMaker
    private lateinit var mapSectionManager: MapSectionManager

    lateinit var directionController: MapDirectionController

    fun setTileMaskTileMaker(maskTileMaker: IMaskTileMaker) {
        this.maskTileMaker = maskTileMaker
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

        setupMyLocationButton()
        setupLocationUpdates() // Setup location updates Callback
    }


    private fun setupLocationUpdates(){
        locationService.setLocationCallback { locationResult ->
            locationResult?.lastLocation?.let {
                Log.d(
                    "MapActivity",
                    "location callback: Location Updated: ${it.latitude}, ${it.longitude}"
                )
                if (!LocationCallbackFilter.isSameLocation(it)) {
                    Log.d("MapActivity", "location callback: Location is Saved")
                    RevealCircleRepository.addLocation(it)
                    updateLocationOnMap(it)
                }
                mapCameraPositionController.updateLocation(it)
            }
        }
    }

    // Update location on map
    private fun updateLocationOnMap(location: Location){
        if (tileOverlays.size == 0)
            return
        val lat = location.latitude
        val lng = location.longitude

        if (lat != 0.0 && lng != 0.0) {
            updateMap()
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

        mapCameraPositionController = MapCameraPositionController(p0!!, myLocationButton)

        lifecycleScope.launch {
            // apply tile overlay to google map
            mapSectionManager = MapSectionRepository.getMapSectionManager(this@MapActivity)
            setTileMaskTileMaker(PositionsMaskTileMaker(
                MapSectionMaskTileMaker(
                    mapSectionManager
                )
            ))
            startLocationUpdates()
        }

        //방향 스껄~
        directionController = MapDirectionController(
            this,
            mapCameraPositionController
        )
        directionController.start()
        setupOnMapPinOnClickListener()
        setPinSetterController()
    }

    fun setupMyLocationButton() {
        myLocationButton = findViewById(R.id.myLocationButton)
        myLocationButton.setOnClickListener {
            mapCameraPositionController.setAttach(true)
            myLocationButton.visibility = View.GONE
        }
    }

    // location updating routine
    private fun startLocationUpdates() {
        lifecycleScope.launch {
            while (true)
                mapCameraPositionController.update()
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            RevealCircleRepository.saveRevealCirclesToDatabase(this@MapActivity, mapSectionManager, maskTileMaker)
        }
    }

    override fun onPause() {
        super.onPause()
        directionController.stop()
    }

    fun setPinSetterController() {
        pinDropZone = findViewById(R.id.outer_drop_zone)
        pinSetter = findViewById(R.id.pinSetter)
        pinSetterController = PinSetterController(pinSetter, pinDropZone, googleMap!!, this)
    }

    fun setupOnMapPinOnClickListener() {
        googleMap?.setOnMarkerClickListener{
            marker ->
            val tag: MarkerTag<*> = marker.tag as MarkerTag<*>
            if (tag.tag == MarkerType.PIN) {
                val pin = tag.data as Pin
                Modal.showCustomModal(this, R.layout.modal_pin_memo_show) { view ->
                    val pinTitle = view.findViewById<TextView>(R.id.memo_title_output)
                    val pinContent = view.findViewById<TextView>(R.id.memo_content_output)
                    val pinDate = view.findViewById<TextView>(R.id.memo_date_output)
                    pinTitle.text = pin.title
                    pinContent.text = pin.content
                    pinDate.text = pin.createdDate.toString()
                }
            }
            true
        }
    }
}