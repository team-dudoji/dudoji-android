package com.dudoji.android.landmark.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.landmark.datasource.LandmarkDataSource
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.presentation.map.MapActivity
import com.dudoji.android.map.utils.NonClusterMarkerApplier
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.collections.MarkerManager

class LandmarkApplier(normalMarkerCollection: MarkerManager.Collection, googleMap: GoogleMap, activity: MapActivity)
    :  NonClusterMarkerApplier<Landmark>(normalMarkerCollection, googleMap, activity), OnCameraIdleListener{

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun load(
        latLng: LatLng,
        radius: Double
    ): Boolean {
        return LandmarkDataSource.load(latLng, radius)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getMarkerBases(): List<Landmark> {
        return LandmarkDataSource.getLandmarks()
    }
}