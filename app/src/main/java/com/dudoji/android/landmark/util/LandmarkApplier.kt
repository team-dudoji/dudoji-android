package com.dudoji.android.landmark.util

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.dudoji.android.landmark.datasource.LandmarkDataSource
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.network.Coil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.collections.MarkerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LandmarkApplier(val normalMarkerCollection: MarkerManager.Collection, val googleMap: GoogleMap, val activity: MapActivity)
    : OnCameraIdleListener{

    private var hasToReload = true

    companion object {
        private val appliedLandmarks: HashSet<Landmark> = HashSet()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun applyLandmarks(landmarks: List<Landmark>) {
        landmarks.forEach { landmark ->
            if (!appliedLandmarks.contains(landmark)) {
                val marker = normalMarkerCollection.addMarker(landmark.toMarkerOptions())
                marker?.tag = landmark
                appliedLandmarks.add(landmark)
                applyMarkerIcon(marker, landmark)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun applyMarkerIcon(marker: Marker?, landmark: Landmark) {
        if (marker == null) return

        CoroutineScope(Dispatchers.IO).launch {
            val loader = Coil.imageLoader
            val request = ImageRequest.Builder(activity)
                .data("${RetrofitClient.BASE_URL}/${landmark.mapImageUrl}")
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            val resizedBitmap = Bitmap.createScaledBitmap(result.toBitmap(), 150, 150, true)
            withContext(Dispatchers.Main) {
                marker.setIcon(
                    BitmapDescriptorFactory.fromBitmap(resizedBitmap)
                )
            }
        }
    }

    fun markForReload() {
        hasToReload = true
    }

    fun clearLandmarks() {
        appliedLandmarks.clear()
        normalMarkerCollection.clear()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCameraIdle() {
        activity.lifecycleScope.launch {
            Log.d("LandmarkApplier", "onCameraIdle called")
            if (hasToReload || LandmarkDataSource.load(
                    googleMap.projection.visibleRegion.latLngBounds.center,
                    100.0)) {
                val landmarks = LandmarkDataSource.getLandmarks()
                Log.d("LandmarkApplier", "Loaded landmarks: ${landmarks.size}")
                applyLandmarks(landmarks)
            }
            hasToReload = false
        }
    }
}