package com.dudoji.android.domain.usecase

import android.location.Location
import com.dudoji.android.domain.repository.LocationRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MapUseCase @Inject constructor(
    val locationRepository: LocationRepository,
) {
    fun getLocationUpdates(): StateFlow<Location> {
        return locationRepository.getLocationUpdates()
    }
    fun getBearing(): StateFlow<Float> {
        return locationRepository.getBearing()
    }
}