package com.dudoji.android.landmark.util

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.map.utils.NonClusterMarkerApplier
import com.google.maps.android.collections.MarkerManager

@RequiresApi(Build.VERSION_CODES.O)
class LandmarkApplier(normalMarkerCollection: MarkerManager.Collection, context: Context)
    : NonClusterMarkerApplier<Landmark>(normalMarkerCollection, context) {

}