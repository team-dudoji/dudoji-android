package com.dudoji.android.presentation.map

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dudoji.android.domain.usecase.MapUseCase
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.map.domain.Npc
import com.dudoji.android.pin.repository.PinRepository
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class MapViewModel @Inject constructor(
    mapUseCase: MapUseCase
) : ViewModel(), GoogleMap.OnCameraIdleListener {

    private val _mapUiState = MutableStateFlow(MapUiState())
    val mapUiState = _mapUiState

    private val _landmarkToShow = MutableStateFlow<Landmark?>(null)
    val landmarkToShow: StateFlow<Landmark?> = _landmarkToShow

    val locationFlow: StateFlow<Location> = mapUseCase.getLocationUpdates()
    val bearingFlow: StateFlow<Float> = mapUseCase.getBearing()

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

    private val _centerLocation = MutableStateFlow<LatLng>(LatLng(0.0, 0.0))
    val centerLocation: StateFlow<LatLng> = _centerLocation

    fun updateCenterLocation(latLng: LatLng) {
        _centerLocation.value = latLng

        if (abs(locationFlow.value.latitude - _centerLocation.value.latitude) < 0.0001 &&
            abs(locationFlow.value.longitude - _centerLocation.value.longitude) < 0.0001) {
            setAttach(true)
        } else {
            setAttach(false)
        }
    }

    fun setAttach(isAttached: Boolean) {
        Log.d("MapViewModel", "setAttach: $isAttached")
        _mapUiState.value = _mapUiState.value.copy(
            isAttached = isAttached
        )
    }

    fun loadPin() {
//        PinRepository.load()
        PinRepository.getPins()
    }

    fun loadLandmark() {

    }

    fun loadNpc() {

    }

    override fun onCameraIdle() {

    }
}

data class MapUiState (
    var isLoading: Boolean = false,
    var errorMessage: String? = null,
    var isAttached: Boolean = true,
    var pins: List<Npc> = emptyList(),
    var landmarks: List<Npc> = emptyList(),
    var npcs: List<Npc> = emptyList()
)