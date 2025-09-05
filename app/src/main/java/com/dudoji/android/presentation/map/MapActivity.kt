package com.dudoji.android.presentation.map

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.dudoji.android.R
import com.dudoji.android.config.DEFAULT_ZOOM_LEVEL
import com.dudoji.android.config.MAX_ZOOM
import com.dudoji.android.config.MIN_ZOOM
import com.dudoji.android.databinding.ActivityMapBinding
import com.dudoji.android.domain.model.ActivityMapObject
import com.dudoji.android.domain.model.UserType
import com.dudoji.android.landmark.activity.LandmarkSearchActivity
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.landmark.util.LandmarkApplier
import com.dudoji.android.map.domain.Npc
import com.dudoji.android.map.fragment.QuestFragment
import com.dudoji.android.map.utils.MapObject
import com.dudoji.android.map.utils.MarkerIconToggler
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class MapActivity :  AppCompatActivity(), OnMapReadyCallback {

    private val mapViewModel: MapViewModel by viewModels()

    private lateinit var binding: ActivityMapBinding

    private val pinApplier: PinApplier by lazy {
        PinApplier(clusterManager, googleMap, this@MapActivity, pinFilter)
    }
    private val landmarkApplier: LandmarkApplier by lazy {
        LandmarkApplier(normalMarkerCollection, googleMap, this@MapActivity)
    }
    private val npcApplier: NpcApplier by lazy {
        NpcApplier(normalMarkerCollection, googleMap, this@MapActivity)
    }

    var mapOverlayUI: MapOverlayUI? = null

    private lateinit var googleMap: GoogleMap

    private var dudojiMarker: Marker? = null
    private var markerIconToggler: MarkerIconToggler? = null

    private val clusterManager: ClusterManager<Pin> by lazy {
        ClusterManager(this@MapActivity, googleMap)
    }

    private val normalMarkerCollection: MarkerManager.Collection by lazy {
        clusterManager.markerManager.newCollection()
    }

    private val pinFilter: PinFilter by lazy {
        PinFilter(binding, this@MapActivity)
    }

    private lateinit var landmarkBottomSheet: LandmarkBottomSheet

    val activityObjects = mutableListOf<ActivityMapObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        setupMyLocationButton()
        setupAnimatedNavButtons()

        landmarkBottomSheet = LandmarkBottomSheet(findViewById(R.id.landmark_bottom_sheet), this)

        setupSearchLandmark()

        lifecycleScope.launch {
            mapViewModel.landmarkToShow.collect { landmark ->
                if (landmark == null) return@collect
                landmarkBottomSheet.open(landmark)
            }
        }
        lifecycleScope.launch {
            mapViewModel.bearingFlow.collect { bearing ->
                dudojiMarker?.rotation = bearing
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapViewModel.locationFlow.collect { location ->
                    Log.d("MapActivity", "Received new location: $location")
                    dudojiMarker?.position = LatLng(location.latitude, location.longitude)
                    markerIconToggler?.setSpeed(location.speed)
                    if (::googleMap.isInitialized && mapViewModel.mapUiState.value.isAttached) {
                        moveTo(location.latitude, location.longitude)
                    }
                }
            }
        }
        lifecycleScope.launch {
            mapViewModel.mapUiState.collect { mapUiState ->
                Log.d("MapActivity", "Map UI State changed: isAttached=${mapUiState.isAttached}")
                binding.myLocationButton.visibility =
                    if (mapUiState.isAttached) View.GONE else View.VISIBLE
            }
        }
    }

    fun setupMyLocationButton() {
        try {
            val myLocationBg = assets.open("map/my_location_button.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            binding.myLocationButton.background = myLocationBg
        } catch (e: IOException) {
            e.printStackTrace()
        }

        binding.myLocationButton.setOnClickListener {
            mapViewModel.setAttach(true)
        }
    }

    // location updating routine
    private fun startLocationUpdates() {
        lifecycleScope.launch {
            while (true) {
                if (::googleMap.isInitialized && mapViewModel.mapUiState.value.isAttached) {
                    moveTo(mapViewModel.locationFlow.value.latitude, mapViewModel.locationFlow.value.longitude)
                }
                delay(100)
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    fun setupDudojiMarker() {
        if (dudojiMarker == null) {
            val markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_main)
            dudojiMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(0.0, 0.0))
                    .icon(markerIcon)
                    .anchor(0.5f, 0.5f)
                    .flat(true)
            )

            markerIconToggler = MarkerIconToggler(dudojiMarker!!)
        }
    }

    fun updateLocationToViewModel() {
        Log.d("MapActivity", "Updating location to ViewModel")
        val centerLatLng = googleMap.cameraPosition.target
        mapViewModel.updateCenterLocation(centerLatLng)
    }

    fun moveTo(lat: Double, lng: Double) {
        if (!::googleMap.isInitialized) return
        val latLng = LatLng(lat, lng)
        val position: CameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(DEFAULT_ZOOM_LEVEL)
            .build()
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setMinZoomPreference(MIN_ZOOM)
        googleMap.setMaxZoomPreference(MAX_ZOOM)

        setupDudojiMarker()

        lifecycleScope.launch {
            startLocationUpdates()

            //맵 액티비티에 스킨 씌우기
            clusterManager.renderer = PinRenderer(
                this@MapActivity,
                googleMap,
                clusterManager
            )

            googleMap.setOnCameraIdleListener {
                updateLocationToViewModel()
                mapViewModel.onCameraIdle()
                clusterManager.onCameraIdle()
                pinApplier.onCameraIdle()
                landmarkApplier.onCameraIdle()
                npcApplier.onCameraIdle()
            }

            googleMap.setOnCameraMoveListener {
                updateLocationToViewModel()
                updateMapObjects()
            }

            pinFilter.setupFilterButtons()

            normalMarkerCollection.setOnMarkerClickListener { marker ->
                Log.d("MapActivity", "Marker clicked: ${marker.id}, ${marker.title}")

                val tag = marker.tag
                if (tag is Landmark) {
                    lifecycleScope.launch {

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

        mapOverlayUI = MapOverlayUI(binding, this, assets, googleMap, pinApplier, clusterManager)
    }

    private fun updateMapObjects() {
       if (!::googleMap.isInitialized) return

        val viewObjects = activityObjects.map { obj ->
            val screenPoint = googleMap.projection.toScreenLocation(obj.latLng)
            MapObject(screenPoint.x.toFloat(), screenPoint.y.toFloat(), obj.offsetX, obj.offsetY, obj.width, obj.height, obj.bitmap)
        }
        binding.mapObjectTextureView.setMapObjects(viewObjects)
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