package com.dudoji.android.map.activity

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.dudoji.android.NavigatableActivity
import com.dudoji.android.R
import com.dudoji.android.config.MAX_ZOOM
import com.dudoji.android.config.MIN_ZOOM
import com.dudoji.android.config.TILE_OVERLAY_LOADING_TIME
import com.dudoji.android.follow.FriendModal
import com.dudoji.android.follow.repository.FollowRepository
import com.dudoji.android.map.controller.PinFilterController
import com.dudoji.android.map.domain.pin.Pin
import com.dudoji.android.map.manager.MapSectionManager
import com.dudoji.android.map.repository.MapSectionRepository
import com.dudoji.android.map.repository.PinRepository
import com.dudoji.android.map.repository.RevealCircleRepository
import com.dudoji.android.map.utils.MapCameraPositionController
import com.dudoji.android.map.utils.MapDirectionController
import com.dudoji.android.map.utils.MapUtil
import com.dudoji.android.map.utils.location.LocationCallbackFilter
import com.dudoji.android.map.utils.location.LocationService
import com.dudoji.android.map.utils.pin.PinApplier
import com.dudoji.android.map.utils.pin.PinSetterController
import com.dudoji.android.map.utils.tile.MaskTileProvider
import com.dudoji.android.map.utils.tile.mask.IMaskTileMaker
import com.dudoji.android.map.utils.tile.mask.MapSectionMaskTileMaker
import com.dudoji.android.map.utils.tile.mask.PositionsMaskTileMaker
import com.dudoji.android.mypage.activity.MypageActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.maps.android.clustering.ClusterManager
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
    lateinit var pinSetterController: PinSetterController
    private lateinit var pinDropZone: FrameLayout
    private lateinit var pinApplier: PinApplier

    private lateinit var googleMap: GoogleMap
    private var mapUtil: MapUtil = MapUtil(this)
    private lateinit var mapCameraPositionController : MapCameraPositionController

    private val numOfTileOverlay = 2
    private var indexOfTileOverlay = 0
    private val tileOverlays: MutableList<TileOverlay> = mutableListOf()

    private lateinit var maskTileMaker: IMaskTileMaker
    private lateinit var mapSectionManager: MapSectionManager

    private lateinit var friendButton :ImageButton

    lateinit var directionController: MapDirectionController

    private lateinit var clusterManager: ClusterManager<Pin>

    private lateinit var pinFilterController: PinFilterController // 핀 필터 변수
    private var currentUserId: Long = -1L // 유저 id


    fun setTileMaskTileMaker(maskTileMaker: IMaskTileMaker) {
        this.maskTileMaker = maskTileMaker
        val tileOverlayOptions = TileOverlayOptions().tileProvider(MaskTileProvider(maskTileMaker))
        for (i in 0 until numOfTileOverlay) {
            tileOverlays.add(googleMap.addTileOverlay(tileOverlayOptions)!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

        setFriendFilterButton()

        lifecycleScope.launch{
            FollowRepository.loadFollowings() // Load followings
        }

        //currentUserId = UserRepository.getCurrentUserId() 유저 아이디 가져오기
        currentUserId = 1L //값 하드코딩


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

    @RequiresApi(Build.VERSION_CODES.O)
    fun setPinSetterController() {
        pinDropZone = findViewById(R.id.outer_drop_zone)
        pinSetter = findViewById(R.id.pinSetter)
        pinSetterController = PinSetterController(pinSetter, pinDropZone ,pinApplier, googleMap, this, clusterManager)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        mapUtil.setGoogleMap(p0)
        p0.setMinZoomPreference(MIN_ZOOM)  // set zoom level bounds
        p0.setMaxZoomPreference(MAX_ZOOM)

        mapCameraPositionController = MapCameraPositionController(p0, myLocationButton)


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

        // set up map section manager for pin
        clusterManager = ClusterManager<Pin>(this, googleMap)
        pinApplier = PinApplier(clusterManager, googleMap, this, currentUserId) //현재 유저 아이디 추가
        googleMap.setOnCameraIdleListener(
            GoogleMap.OnCameraIdleListener {
                clusterManager.onCameraIdle()
                pinApplier.onCameraIdle()
            }
        )
        googleMap.setOnMarkerClickListener(clusterManager)

        directionController = MapDirectionController(
            this,
            mapCameraPositionController
        )
        directionController.start()

        lifecycleScope.launch {
            FollowRepository.loadFollowings()
            //val friendIds = FollowRepository.getFollowings().map { it.id }.toSet()

            pinFilterController = PinFilterController(this@MapActivity, pinApplier)
            pinFilterController.setupFilterButtons()
        }

        // pinFilterController 초기화 먼저!
        pinFilterController = PinFilterController(this, pinApplier)
        pinFilterController.setupFilterButtons()

        // 테스트용 핀 주입과 필터 적용은 초기화 후 호출
        PinRepository.injectTestPins()
        pinFilterController.applyFilteredPins()

        setPinSetterController()
    }

    fun setFriendFilterButton() {
        friendButton = findViewById(R.id.btnFriend)

        friendButton.setOnClickListener {
            FriendModal.openFriendFilterModal(this)
        }
    }
}