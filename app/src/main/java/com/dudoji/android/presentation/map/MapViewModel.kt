package com.dudoji.android.presentation.map

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dudoji.android.config.LANDMARK_PIN_RADIUS
import com.dudoji.android.config.REVEAL_CIRCLE_RADIUS_BY_WALK
import com.dudoji.android.domain.model.PinSkin
import com.dudoji.android.domain.usecase.MapUseCase
import com.dudoji.android.landmark.datasource.LandmarkDataSource
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.map.domain.Npc
import com.dudoji.android.map.repository.NpcDataSource
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.domain.Who
import com.dudoji.android.pin.repository.PinRepository
import com.dudoji.android.pin.util.PinMakeData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class MapViewModel @Inject constructor(
    val mapUseCase: MapUseCase
) : ViewModel(), GoogleMap.OnCameraIdleListener {

    private val _mapUiState = MutableStateFlow(MapUiState())
    val mapUiState = _mapUiState

    private val _visibilityMap: MutableStateFlow<MutableMap<Who, Boolean>> = MutableStateFlow(mutableMapOf(
        Who.MINE to true,
        Who.FOLLOWING to true,
        Who.UNKNOWN to true
    ))
    val visibilityMap: StateFlow<MutableMap<Who, Boolean>> = _visibilityMap

    val pinsToShow: Flow<List<Pin>> = mapUiState.map { it.pins }
    val npcsToShow: Flow<List<Npc>> = mapUiState.map { it.npcs }
    val landmarksToShow: Flow<List<Landmark>> = mapUiState.map { it.landmarks }

    private val _landmarkToShow = MutableStateFlow<Landmark?>(null)
    val landmarkToShow: StateFlow<Landmark?> = _landmarkToShow
    val pinToShow: MutableStateFlow<Pin?> = MutableStateFlow(null) // Not implemented yet
    val pinClusterToShow: MutableStateFlow<List<Pin>> = MutableStateFlow(emptyList()) // Not implemented yet

    val locationFlow: StateFlow<Location> = mapUseCase.getLocationUpdates()
    val bearingFlow: StateFlow<Float> = mapUseCase.getBearing()
    val selectedPinSkin: MutableStateFlow<PinSkin?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            locationFlow.collect {
                    location ->
                Log.d("MapViewModel", "New location: $location")
                if (_mapUiState.value.isAttached) {

                }
            }
        }
    }



    fun setSelectedPinSkin(pinSkin: PinSkin?) {
        selectedPinSkin.value = pinSkin
    }

    fun setPinClusterToShow(pins: List<Pin>) {
        pinClusterToShow.value = pins
    }

    fun setPinToShow(pin: Pin?) {
        pinToShow.value = pin
    }

    fun setLandmarkToShow(landmark: Landmark?) {
        _landmarkToShow.value = landmark
    }

    fun toggleVisibility(who: Who) {
        val current = _visibilityMap.value[who] ?: true
        _visibilityMap.value = _visibilityMap.value.toMutableMap().apply {
            this[who] = !current
        }
    }

    private val _centerLocation = MutableStateFlow<LatLng>(LatLng(0.0, 0.0))
    val centerLocation: StateFlow<LatLng> = _centerLocation

    fun createPin(pinMakeData: PinMakeData) {
        viewModelScope.launch {
            mapUseCase.createPin(pinMakeData)
        }
    }

    fun updateCenterLocation(latLng: LatLng) {
        _centerLocation.value = latLng

        if (abs(locationFlow.value.latitude - _centerLocation.value.latitude) < 0.0001 &&
            abs(locationFlow.value.longitude - _centerLocation.value.longitude) < 0.0001) {
            setAttach(true)
        } else {
            setAttach(false)
        }
    }

    fun canCreatePin(lat: Double, lng: Double): Boolean {
//        val distance = locationFlow.value.distanceTo(
//            Location("manual").apply {
//                latitude = lat
//                longitude = lng }
//        )
//        return distance <= REVEAL_CIRCLE_RADIUS_BY_WALK.toFloat()
        return true
    }

    fun setAttach(isAttached: Boolean) {
        Log.d("MapViewModel", "setAttach: $isAttached")
        _mapUiState.value = _mapUiState.value.copy(
            isAttached = isAttached
        )
    }

    fun loadPin() {
        viewModelScope.launch {
            PinRepository.load(
                LatLng(centerLocation.value.latitude, centerLocation.value.longitude),
                LANDMARK_PIN_RADIUS.toDouble())
            _mapUiState.value = _mapUiState.value.copy(
                pins = PinRepository.getPins().filter { pin ->
                    _visibilityMap.value[pin.master] == true
                }
            )
        }
    }

    fun loadLandmark() {
        viewModelScope.launch {
            LandmarkDataSource.load(
                LatLng(centerLocation.value.latitude, centerLocation.value.longitude),
                LANDMARK_PIN_RADIUS.toDouble())
            _mapUiState.value = _mapUiState.value.copy(
                landmarks = LandmarkDataSource.getLandmarks()
            )
        }
    }

    fun loadNpc() {
        viewModelScope.launch {
            NpcDataSource.load(
                LatLng(centerLocation.value.latitude, centerLocation.value.longitude),
                LANDMARK_PIN_RADIUS.toDouble())
            _mapUiState.value = _mapUiState.value.copy(
                npcs = NpcDataSource.getNpcs()
            )
        }
    }

    override fun onCameraIdle() {
        loadPin()
        loadLandmark()
        loadNpc()
    }
}

data class MapUiState (
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAttached: Boolean = true,
    val pins: List<Pin> = emptyList(),
    val landmarks: List<Landmark> = emptyList(),
    val npcs: List<Npc> = emptyList()
)