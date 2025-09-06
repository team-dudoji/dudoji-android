package com.dudoji.android.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {
    fun getLocationUpdates(): StateFlow<Location>
    fun getBearing(): StateFlow<Float>
}