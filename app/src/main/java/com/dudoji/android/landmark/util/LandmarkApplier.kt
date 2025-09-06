package com.dudoji.android.landmark.util

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.presentation.map.NonClusterMarkerApplier
import com.google.maps.android.collections.MarkerManager

@RequiresApi(Build.VERSION_CODES.O)
class LandmarkApplier(normalMarkerCollection: MarkerManager.Collection, context: Context)
    : NonClusterMarkerApplier<Landmark>(normalMarkerCollection, context) {

    init {
//        isIncludedBaseUrl = true
    }

    override fun onMarkerLoaded(marker: com.google.android.gms.maps.model.Marker?) {
        // No additional actions needed when a landmark marker is loaded
    }
}