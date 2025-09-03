package com.dudoji.android.map.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.dudoji.android.R
import com.dudoji.android.config.MAX_ZOOM
import com.dudoji.android.config.MIN_ZOOM
import com.dudoji.android.domain.model.UserType
import com.dudoji.android.landmark.activity.LandmarkSearchActivity
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.landmark.util.LandmarkApplier
import com.dudoji.android.map.domain.Npc
import com.dudoji.android.map.fragment.QuestFragment
import com.dudoji.android.map.manager.DatabaseMapSectionManager
import com.dudoji.android.map.manager.MapSectionManager
import com.dudoji.android.map.repository.RevealCircleRepository
import com.dudoji.android.map.utils.MapCameraPositionController
import com.dudoji.android.map.utils.MapDirectionController
import com.dudoji.android.map.utils.MapObject
import com.dudoji.android.map.utils.MapObjectTextureView
import com.dudoji.android.map.utils.MapUtil
import com.dudoji.android.map.utils.fog.FogTextureView
import com.dudoji.android.map.utils.location.GPSLocationService
import com.dudoji.android.map.utils.location.LocationCallbackFilter
import com.dudoji.android.map.utils.location.LocationService
import com.dudoji.android.map.utils.npc.NpcApplier
import com.dudoji.android.map.utils.ui.LandmarkBottomSheet
import com.dudoji.android.mypage.activity.MyPageActivity
import com.dudoji.android.pin.activity.MyPinActivity
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.util.PinApplier
import com.dudoji.android.pin.util.PinFilter
import com.dudoji.android.pin.util.PinRenderer
import com.dudoji.android.presentation.follow.FollowListActivity
import com.dudoji.android.shop.activity.ShopActivity
import com.dudoji.android.ui.AnimatedNavButtonHelper
import com.dudoji.android.util.modal.Modal
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import kotlinx.coroutines.launch
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
class MapActivity :  AppCompatActivity(), OnMapReadyCallback {
    data class ActivityMapObject(val latLng: LatLng, val bitmap: Bitmap, val offsetX: Float = 0f, val offsetY: Float = 0f, val width: Int? = null, val height: Int? = null)

    private lateinit var locationService: LocationService //로케이션 서비스 변수 추가

    private val pinApplier: PinApplier by lazy {
        PinApplier(clusterManager, googleMap, this@MapActivity, pinFilter)
    }
    private val landmarkApplier: LandmarkApplier by lazy {
        LandmarkApplier(normalMarkerCollection, googleMap, this@MapActivity)
    }
    private val npcApplier: NpcApplier by lazy {
        NpcApplier(normalMarkerCollection, googleMap, this@MapActivity)
    }
    private val myLocationButton by lazy {
        findViewById<Button>(R.id.my_location_button)
    }
    private val objectTextureView by lazy {
        findViewById<MapObjectTextureView>(R.id.map_object_texture_view)
    }
    val fogTextureView: FogTextureView by lazy {
        findViewById<FogTextureView>(R.id.fog_texture_view)
    }
    var mapOverlayUI: MapOverlayUI? = null

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

    val activityObjects = mutableListOf<ActivityMapObject>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        mapUtil.setupLocationServices()
        mapUtil.requestLocationPermission()
        mapUtil.prepareMap(savedInstanceState)

        locationService = GPSLocationService(this)

        setupMyLocationButton()

        setupAnimatedNavButtons()

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

    fun moveTo(lat: Double, lng: Double) {
        if (!::googleMap.isInitialized) return
        mapCameraPositionController.moveCameraPosition(lat, lng, 15f)
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
                landmarkApplier.onCameraIdle()
                npcApplier.onCameraIdle()
                fogTextureView.updateParticles(mapSectionManager as DatabaseMapSectionManager)
            }

            googleMap.setOnCameraMoveListener {
                fogTextureView.onCameraMoved(mapSectionManager as DatabaseMapSectionManager)
                updateMapObjects()
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
                } else if (tag is Npc) {
                    Modal.showCustomModal(
                        this@MapActivity,
                        QuestFragment(tag.npcId),
                        R.layout.template_quest_modal
                    )
                    true
                }

                false
            }
        }

        directionController = MapDirectionController(this@MapActivity, mapCameraPositionController)
        directionController.start()

        mapOverlayUI = MapOverlayUI(assets, this, googleMap, pinApplier, clusterManager)
    }

    private fun updateMapObjects() {
       if (!::googleMap.isInitialized) return

        val viewObjects = activityObjects.map { obj ->
            val screenPoint = googleMap.projection.toScreenLocation(obj.latLng)
            MapObject(screenPoint.x.toFloat(), screenPoint.y.toFloat(), obj.offsetX, obj.offsetY, obj.width, obj.height, obj.bitmap)
        }
        objectTextureView.setMapObjects(viewObjects)
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
                intent.putExtra(FollowListActivity.EXTRA_TYPE, UserType.FOLLOWER.toString()) // 기본으로 팔로워 페이지 이동
                startActivity(intent)
            },
            onProfileClick = {
                startActivity(Intent(this, MyPageActivity::class.java))
            }
        )
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