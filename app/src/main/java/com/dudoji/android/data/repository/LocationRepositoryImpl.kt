package com.dudoji.android.data.repository

import android.location.Location
import android.util.Log
import com.dudoji.android.data.datasource.location.BearingDataSource
import com.dudoji.android.data.datasource.location.LocationService
import com.dudoji.android.domain.repository.LocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    locationService: LocationService,
    bearingService: BearingDataSource
): LocationRepository {

    private val _locationFlow = MutableStateFlow(Location("initial").apply {
        latitude = 37.5665
        longitude = 126.9780
    })
    val locationFlow: StateFlow<Location> = _locationFlow
    val bearingFlow: StateFlow<Float> = bearingService.bearing

    init {
        locationService.setLocationCallback {
            location ->
            Log.d("LocationRepositoryImpl", "New location: $location")
            CoroutineScope(Dispatchers.Default).launch {
                _locationFlow.emit(location)
            }
        }
    }

    override fun getLocationUpdates(): StateFlow<Location> {
        return locationFlow
    }

    override fun getBearing(): StateFlow<Float> {
        return bearingFlow
    }
}