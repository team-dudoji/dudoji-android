package com.dudoji.android.map.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import coil.load
import com.airbnb.lottie.LottieAnimationView
import com.dudoji.android.R
import com.dudoji.android.config.MAX_ZOOM
import com.dudoji.android.config.MIN_ZOOM
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
import com.dudoji.android.map.utils.fog.FogTextureView
import com.dudoji.android.map.utils.location.GPSLocationService
import com.dudoji.android.map.utils.location.LocationCallbackFilter
import com.dudoji.android.map.utils.location.LocationService
import com.dudoji.android.map.utils.ui.LandmarkBottomSheet
import com.dudoji.android.follow.activity.FollowListActivity
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
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import kotlinx.coroutines.launch
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
class MapActivity :  AppCompatActivity(), OnMapReadyCallback {
    private lateinit var locationService: LocationService //로케이션 서비스 변수 추가

    private lateinit var pinSetter: ImageView
    lateinit var pinSetterController: PinSetterController
    private lateinit var pinDropZone: FrameLayout


    private val pinApplier: PinApplier by lazy {
        PinApplier(clusterManager, googleMap, this@MapActivity, pinFilter)
    }
    private val LandmarkApplier: LandmarkApplier by lazy {
        LandmarkApplier(normalMarkerCollection, googleMap, this@MapActivity)
    }
    private val searchBarContainer by lazy {
        findViewById<LinearLayout>(R.id.search_bar_container)
    }
    private val myLocationButton by lazy {
        findViewById<Button>(R.id.my_location_button)
    }
    private val navigationLayout by lazy {
        findViewById<ConstraintLayout>(R.id.navigation_layout)
    }
    private val landmarkBottomLayout by lazy {
        findViewById<LinearLayout>(R.id.landmark_bottom_sheet)
    }
//    private val fogParticleOverlayView: FogParticleOverlayView by lazy {
//        findViewById<FogParticleOverlayView>(R.id.particle_overlay)
//    }
    val fogTextureView: FogTextureView by lazy {
        findViewById<FogTextureView>(R.id.fog_texture_view)
    }


    private lateinit var googleMap: GoogleMap
    private var mapUtil: MapUtil = MapUtil(this)
    private val mapCameraPositionController : MapCameraPositionController by lazy {
        MapCameraPositionController(this.googleMap, myLocationButton)
    }

    private lateinit var mapSectionManager: MapSectionManager

    lateinit var directionController: MapDirectionController

    private val clusterManager: ClusterManager<Pin> by lazy {
        ClusterManager(this@MapActivity, googleMap)
    }
    private val normalMarkerCollection: MarkerManager.Collection by lazy {
        clusterManager.markerManager.newCollection()
    }

    private val pinFilter: PinFilter by lazy {
        PinFilter(this@MapActivity, mapSectionManager as DatabaseMapSectionManager?)
    }
    private lateinit var landmarkBottomSheet: LandmarkBottomSheet

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        mapUtil.setupLocationServices()
        mapUtil.requestLocationPermission()
        mapUtil.prepareMap(savedInstanceState)

        locationService = GPSLocationService(this)

        setupMyLocationButton()

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
                if (!LocationCallbackFilter.isSameLocation(it)) {
                    RevealCircleRepository.addLocation(it)
                }
                mapCameraPositionController.updateLocation(it)
            }
        }
    }

    fun bringToFront(view: View) {
        view.bringToFront()
        view.parent.requestLayout()
        (view.parent as View).invalidate()
    }

    fun setupMyLocationButton() {
        try {
            val myLocationBg = assets.open("map/my_location_button.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            myLocationButton.background = myLocationBg
        } catch (e: IOException) {
            e.printStackTrace()
        }

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

        try {
            val pinSetterBg = assets.open("pin/pin_button.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            pinSetter.background = pinSetterBg
        } catch (e: IOException) {
            e.printStackTrace()
        }

        pinSetterController = PinSetterController(pinSetter, pinDropZone ,pinApplier, googleMap, this, clusterManager)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setupLocationUpdates() // Setup location updates Callback
        mapUtil.setGoogleMap(googleMap)
        this.googleMap.setMinZoomPreference(MIN_ZOOM)
        this.googleMap.setMaxZoomPreference(MAX_ZOOM)
        mapCameraPositionController

        fogTextureView.setGoogleMap(googleMap)
        lifecycleScope.launch {
            mapSectionManager = DatabaseMapSectionManager(this@MapActivity)

            startLocationUpdates()

            //맵 액티비티에 스킨 씌우기
            clusterManager.renderer = PinRenderer(
                this@MapActivity,
                googleMap,
                clusterManager
            )

            googleMap.setOnCameraIdleListener {
                clusterManager.onCameraIdle()
                pinApplier.onCameraIdle()
                LandmarkApplier.onCameraIdle()
                fogTextureView.updateParticles(mapSectionManager as DatabaseMapSectionManager)
            }

            googleMap.setOnCameraMoveListener {
                fogTextureView.onCameraMoved(mapSectionManager as DatabaseMapSectionManager)
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
        val searchIcon = findViewById<ImageView>(R.id.searchIcon)
        val editText = findViewById<EditText>(R.id.searchEditText)
        val container = findViewById<LinearLayout>(R.id.search_bar_container)

        searchIcon.load("file:///android_asset/map/ic_search.png")

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