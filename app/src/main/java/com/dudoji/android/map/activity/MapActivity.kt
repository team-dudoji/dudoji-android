package com.dudoji.android.map.activity

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.dudoji.android.R
import com.dudoji.android.config.MAX_ZOOM
import com.dudoji.android.config.MIN_ZOOM
import com.dudoji.android.config.TILE_OVERLAY_LOADING_TIME
import com.dudoji.android.follow.repository.FollowRepository
import com.dudoji.android.landmark.activity.LandmarkSearchActivity
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.landmark.util.LandmarkApplier
import com.dudoji.android.map.manager.DatabaseMapSectionManager
import com.dudoji.android.map.manager.MapSectionManager
import com.dudoji.android.map.repository.RevealCircleRepository
import com.dudoji.android.map.utils.MapCameraPositionController
import com.dudoji.android.map.utils.MapDirectionController
import com.dudoji.android.map.utils.MapUtil
import com.dudoji.android.map.utils.location.GPSLocationService
import com.dudoji.android.map.utils.location.LocationCallbackFilter
import com.dudoji.android.map.utils.location.LocationService
import com.dudoji.android.map.utils.tile.MaskTileProvider
import com.dudoji.android.map.utils.tile.mask.IMaskTileMaker
import com.dudoji.android.map.utils.tile.mask.MapSectionMaskTileMaker
import com.dudoji.android.map.utils.ui.LandmarkBottomSheet
import com.dudoji.android.mypage.activity.FollowListActivity
import com.dudoji.android.mypage.activity.MyPageActivity
import com.dudoji.android.pin.activity.MyPinActivity
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.util.PinApplier
import com.dudoji.android.pin.util.PinFilter
import com.dudoji.android.pin.util.PinRenderer
import com.dudoji.android.pin.util.PinSetterController
import com.dudoji.android.shop.activity.ShopActivity
import com.dudoji.android.ui.AnimatedNavButtonHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import kotlinx.coroutines.launch

class MapActivity :  AppCompatActivity(), OnMapReadyCallback {

    private lateinit var myLocationButton : Button;
    private lateinit var locationService: LocationService //로케이션 서비스 변수 추가

    private lateinit var pinSetter: ImageView
    lateinit var pinSetterController: PinSetterController
    private lateinit var pinDropZone: FrameLayout
    private lateinit var pinApplier: PinApplier
    private lateinit var LandmarkApplier: LandmarkApplier

    private lateinit var googleMap: GoogleMap
    private var mapUtil: MapUtil = MapUtil(this)
    private lateinit var mapCameraPositionController : MapCameraPositionController

    private val numOfTileOverlay = 2
    private var indexOfTileOverlay = 0
    private val tileOverlays: MutableList<TileOverlay> = mutableListOf()

    private lateinit var maskTileMaker: IMaskTileMaker
    private lateinit var mapSectionManager: MapSectionManager

    lateinit var directionController: MapDirectionController

    private lateinit var clusterManager: ClusterManager<Pin>
    private val normalMarkerCollection: MarkerManager.Collection by lazy {
        clusterManager.markerManager.newCollection()
    }

    private lateinit var pinFilter: PinFilter // 핀 필터 변수
    private lateinit var landmarkBottomSheet: LandmarkBottomSheet


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

        locationService = GPSLocationService(this)

        setupMyLocationButton()
        setupLocationUpdates() // Setup location updates Callback

        lifecycleScope.launch{
            FollowRepository.loadFollowings() // Load followings
        }

        setupAnimatedNavButtons()

        setupFilterBarToggle()

        landmarkBottomSheet = LandmarkBottomSheet(findViewById(R.id.landmark_bottom_sheet), this)

        setupSearchLandmark()
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

    override fun onPause() {
        super.onPause()
        if (::directionController.isInitialized) {
            directionController.stop()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setPinSetterController() {
        pinDropZone = findViewById(R.id.outer_drop_zone)
        pinSetter = findViewById(R.id.pinSetter)
        pinSetterController = PinSetterController(pinSetter, pinDropZone ,pinApplier, googleMap, this, clusterManager)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        mapUtil.setGoogleMap(googleMap)
        this.googleMap.setMinZoomPreference(MIN_ZOOM)
        this.googleMap.setMaxZoomPreference(MAX_ZOOM)
        mapCameraPositionController = MapCameraPositionController(this.googleMap, myLocationButton)

        lifecycleScope.launch {
            mapSectionManager = DatabaseMapSectionManager(this@MapActivity)
            setTileMaskTileMaker(
                MapSectionMaskTileMaker(mapSectionManager)
            )
            startLocationUpdates()

            clusterManager = ClusterManager(this@MapActivity, googleMap)

            //맵 액티비티에 스킨 씌우기
            clusterManager.renderer = PinRenderer(
                this@MapActivity,
                googleMap,
                clusterManager
            )

            pinFilter = PinFilter(this@MapActivity)
            pinApplier = PinApplier(clusterManager, googleMap, this@MapActivity, pinFilter)
            LandmarkApplier = LandmarkApplier(normalMarkerCollection, googleMap, this@MapActivity)

            googleMap.setOnCameraIdleListener {
                clusterManager.onCameraIdle()
                pinApplier.onCameraIdle()
                LandmarkApplier.onCameraIdle()
            }

            pinFilter.setupFilterButtons()

            normalMarkerCollection.setOnMarkerClickListener { marker ->
                Log.d("MapActivity", "Marker clicked: ${marker.id}, ${marker.title}")

                val tag = marker.tag
                if (tag is Landmark) {
                    lifecycleScope.launch {
                        landmarkBottomSheet.open(tag)
                    }
                    true
                }
                false
            }
        }

        directionController = MapDirectionController(this@MapActivity, mapCameraPositionController)
        directionController.start()

        setPinSetterController()
    }

    private fun setupAnimatedNavButtons() {
        AnimatedNavButtonHelper.setup(
            activity = this,
            onStoreClick = {
                startActivity(Intent(this, ShopActivity::class.java))
            },
            onMyPinClick = {
                startActivity(Intent(this, MyPinActivity::class.java))
            },
            onSocialClick = {
                val intent = Intent(this, FollowListActivity::class.java)
                intent.putExtra(FollowListActivity.EXTRA_TYPE, FollowListActivity.TYPE_FOLLOWER) // 기본으로 팔로워 페이지 이동
                startActivity(intent)
            },
            onProfileClick = {
                startActivity(Intent(this, MyPageActivity::class.java))
            }
        )
    }

    private fun setupFilterBarToggle() {
        val btnFilter = findViewById<ImageButton>(R.id.btnFilter)
        val filterBarWrapper = findViewById<FrameLayout>(R.id.filterBarWrapper)
        val filterBarAnim = findViewById<LottieAnimationView>(R.id.filterBarAnim)

        var isFilterBarVisible = false

        btnFilter.setOnClickListener {
            isFilterBarVisible = !isFilterBarVisible
            if (isFilterBarVisible) {
                filterBarWrapper.visibility = View.VISIBLE
                filterBarAnim.progress = 0f
                filterBarAnim.playAnimation()
            } else {
                filterBarWrapper.visibility = View.GONE
            }
        }
    }

    private fun setupSearchLandmark() {
        val editText = findViewById<EditText>(R.id.searchEditText)
        val container = findViewById<LinearLayout>(R.id.searchBarContainer)

        fun goToSearch() {
            val intent = Intent(this, LandmarkSearchActivity::class.java)
            intent.putExtra("query", editText.text.toString())
            startActivity(intent)
        }

        container.setOnClickListener { goToSearch() }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                goToSearch()
                true
            } else false
        }
    }
}