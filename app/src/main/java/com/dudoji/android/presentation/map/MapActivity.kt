package com.dudoji.android.presentation.map

import RetrofitClient
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
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
import com.dudoji.android.domain.repository.PinSkinRepository
import com.dudoji.android.landmark.activity.LandmarkSearchActivity
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.landmark.util.LandmarkApplier
import com.dudoji.android.map.domain.Npc
import com.dudoji.android.map.fragment.QuestFragment
import com.dudoji.android.map.utils.npc.NpcApplier
import com.dudoji.android.mypage.activity.MyPageActivity
import com.dudoji.android.pin.activity.MyPinActivity
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.presentation.follow.FollowListActivity
import com.dudoji.android.presentation.map.PinModal.openPinDataModal
import com.dudoji.android.presentation.shop.activity.ShopActivity
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
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class MapActivity :  AppCompatActivity(), OnMapReadyCallback {

    private val mapViewModel: MapViewModel by viewModels()

    @Inject
    lateinit var pinSkinRepository: PinSkinRepository

    private lateinit var binding: ActivityMapBinding

    private val landmarkApplier: LandmarkApplier by lazy {
        LandmarkApplier(normalMarkerCollection, this)
    }
    private val npcApplier: NpcApplier by lazy {
        NpcApplier(normalMarkerCollection, this, {
                npc ->
            if (npc.hasQuest) {
                val initialBitmap = BitmapFactory.decodeResource(resources, R.mipmap.quest_bubble)
                val activityMapObject =
                    ActivityMapObject(LatLng(npc.lat, npc.lng), initialBitmap, 70f, -150f, 170, -100)
                npc.activityMapObject = activityMapObject
                activityObjects.add(activityMapObject)
            }
        }) {
            activityObjects.clear()
        }
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
        PinFilter(binding.mapOverlayUiLayout, mapViewModel)
    }
    private val pinSkinSelectBar: PinSelectBar by lazy {
        PinSelectBar(binding.mapOverlayUiLayout.pinSelectBar, pinSkinRepository) {
                selectedPinSkin ->
            mapViewModel.setSelectedPinSkin(selectedPinSkin)
        }
    }

    private val profileButtonManager: ProfileButtonManager by lazy {
        ProfileButtonManager(
            context = this,
            profileButton = binding.profileButton,
            profileSelectorBar = binding.profileSelectorBar,
            lottieView = binding.profileBarLottie,
            option1 = binding.profileOption1,
            option2 = binding.profileOption2,
            option3 = binding.profileOption3,
            option4 = binding.profileOption4,
            viewModel = mapViewModel,
            scope = lifecycleScope
        )
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

        profileButtonManager.init()

        landmarkBottomSheet = LandmarkBottomSheet(binding.landmarkBottomSheet, this)

        setupSearchLandmark()

        lifecycleScope.launch {
            pinSkinSelectBar.init()
            mapViewModel.selectedPinSkin.collect {
                    pinSkin ->
                if (pinSkin == null) return@collect
                pinSkinSelectBar.setSelectedPinSkin(pinSkin)

            }
        }
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
        lifecycleScope.launch {
            mapViewModel.visibilityMap.collect { visibilityMap ->
                for ((who, isVisible) in visibilityMap) {
                    pinFilter.updateFilterButton(who, isVisible)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapViewModel.landmarksToShow.collect { landmarks ->
                    if (!::googleMap.isInitialized) return@collect
                    Log.d("MapActivity", "Received landmarks: ${landmarks.size}")
                    landmarkApplier.add(landmarks)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapViewModel.npcsToShow.collect { npcs ->
                    if (!::googleMap.isInitialized) return@collect
                    npcApplier.add(npcs)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapViewModel.pinsToShow.collect { pins ->
                    if (!::googleMap.isInitialized) return@collect
                    clusterManager.clearItems()
                    clusterManager.addItems(pins)
                    clusterManager.cluster()
                }
            }
        }
        lifecycleScope.launch {
            mapViewModel.pinToShow.collect { pin ->
                if (pin == null) return@collect
                PinModal.openPinMemoModal(
                    this@MapActivity,
                    pin,
                )
            }
        }
        lifecycleScope.launch {
            mapViewModel.pinClusterToShow.collect { pins ->
                if (pins.isEmpty()) return@collect
                PinModal.openPinMemosModal(
                    this@MapActivity,
                    pins
                )
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

            clusterManager.renderer = PinRenderer(
                this@MapActivity,
                googleMap,
                clusterManager,
                pinSkinRepository
            )

            googleMap.setOnCameraIdleListener {
                updateLocationToViewModel()
                mapViewModel.onCameraIdle()
                clusterManager.onCameraIdle()
            }

            googleMap.setOnCameraMoveListener {
                updateLocationToViewModel()
                updateMapObjects()
            }

            clusterManager.setOnClusterClickListener {
                    cluster ->
                mapViewModel.setPinClusterToShow(cluster.items.toList())
                true
            }

            clusterManager.setOnClusterItemClickListener { pin ->
                mapViewModel.setPinToShow(pin)
                true
            }

            normalMarkerCollection.setOnMarkerClickListener { marker ->
                Log.d("MapActivity", "Marker clicked: ${marker.id}, ${marker.title}")

                val tag = marker.tag
                when (tag) {
                    is Landmark -> {
                        lifecycleScope.launch {
                            mapViewModel.setLandmarkToShow(tag)
                        }
                        true
                    }
                    is Npc -> {
                        if (tag.hasQuest) {
                            lifecycleScope.launch {
                                val response = RetrofitClient.npcQuestApiService.getNpcQuest(tag.npcId)
                                if (response.isSuccessful) {
                                    val npcQuestDto = response.body() ?: throw IllegalStateException("NPC Quest data is null")
                                    if (npcQuestDto.quests.isEmpty()) {
                                        Toast.makeText(
                                            this@MapActivity,
                                            "진행 가능한 퀘스트가 없습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@launch
                                    }
                                    val quest = npcQuestDto.quests[0]
                                    mapViewModel.acceptQuest(quest.id) {
                                        isSuccess ->
                                        if (isSuccess) {
                                            Toast.makeText(
                                                this@MapActivity,
                                                "퀘스트를 수락했습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            tag.hasQuest = false
                                            npcApplier.reload()
                                            lifecycleScope.launch {
                                                binding.dialogLayout.root.visibility = View.VISIBLE
                                                delay(5000)
                                                binding.dialogLayout.root.visibility = View.GONE
                                            }
                                        } else {
                                            Toast.makeText(
                                                this@MapActivity,
                                                "퀘스트 수락에 실패했습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                } else {
                                    Toast.makeText(
                                        this@MapActivity,
                                        "퀘스트 정보를 불러오지 못했습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }
                            }
                            return@setOnMarkerClickListener true
                        }
                        Modal.showCustomModal(
                            this@MapActivity,
                            QuestFragment(tag.npcId),
                            R.layout.template_quest_modal
                        )
                        true
                    }
                    else -> false
                }
            }
        }

        mapOverlayUI = MapOverlayUI(
            binding.mapOverlayUiLayout,
            this,
            assets,
            googleMap
        ) {
                lat, lng ->
            if (!::googleMap.isInitialized) return@MapOverlayUI

            if (!mapViewModel.canCreatePin(lat, lng)) {
                Toast.makeText(
                    this@MapActivity,
                    "핀을 드롭할 수 있는 위치가 아닙니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@MapOverlayUI
            }

            openPinDataModal(this, lat, lng, mapViewModel.selectedPinSkin.value) {
                    pinMakeData ->
                mapViewModel.createPin(pinMakeData)
            }
        }
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
            onStateChanged = { isExpanded ->
                profileButtonManager.updateVisibility(isExpanded)
            },
            onStoreClick = {
                startActivity(Intent(this, ShopActivity::class.java))
            },
            onMyPinClick = {
                startActivity(Intent(this, MyPinActivity::class.java))
            },
            onSocialClick = {
                val intent = Intent(this, FollowListActivity::class.java)
                intent.putExtra(FollowListActivity.EXTRA_TYPE, UserType.FOLLOWER.toString())
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