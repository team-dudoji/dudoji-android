package com.dudoji.android.map.utils

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.dudoji.android.presentation.map.MapActivity
import com.dudoji.android.map.domain.NonClusterMarker
import com.dudoji.android.network.Coil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.collections.MarkerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.forEach

abstract class NonClusterMarkerApplier<T : NonClusterMarker> (
    val normalMarkerCollection: MarkerManager.Collection,
    val googleMap: GoogleMap,
    val activity: MapActivity): OnCameraIdleListener {
    private var hasToReload = true
    private val appliedMarkerBases: HashSet<T> = HashSet()

    protected var isIncludedBaseUrl = false

    companion object {
        const val TAG = "NonClusterMarkerApplier"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun applyMarker(markerBases: List<T>) {
        markerBases.forEach { markerBase ->
            if (!appliedMarkerBases.contains(markerBase)) {
                val marker = normalMarkerCollection.addMarker(markerBase.toMarkerOptions())
                marker?.tag = markerBase
                appliedMarkerBases.add(markerBase)
                applyMarkerIcon(marker, markerBase)
                onMarkerLoaded(marker)
            }
        }
    }

    open fun onMarkerLoaded(marker: Marker?) {}

    @RequiresApi(Build.VERSION_CODES.O)
    fun applyMarkerIcon(marker: Marker?, markerBase: T) {
        if (marker == null) return

        CoroutineScope(Dispatchers.IO).launch {
            val loader = Coil.imageLoader
            val request = ImageRequest.Builder(activity)
                .data(if (isIncludedBaseUrl) {markerBase.iconUrl} else {"${RetrofitClient.BASE_URL}/${markerBase.iconUrl}"})
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                val resizedBitmap = Bitmap.createScaledBitmap(result.drawable.toBitmap(), 150, 150, true)
                withContext(Dispatchers.Main) {
                    marker.setIcon(
                        BitmapDescriptorFactory.fromBitmap(resizedBitmap)
                    )
                }
            }
        }
    }

    fun markForReload() {
        hasToReload = true
    }

    fun clearLandmarks() {
        appliedMarkerBases.clear()
        normalMarkerCollection.clear()
    }

    abstract suspend fun load(latLng: LatLng, radius: Double): Boolean
    abstract fun getMarkerBases(): List<T>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCameraIdle() {
        activity.lifecycleScope.launch {
            if (hasToReload || load(
                    googleMap.projection.visibleRegion.latLngBounds.center,
                    100.0)) {
                val markerBases = getMarkerBases()
                Log.d(TAG, "Loaded landmarks: ${markerBases.size}")
                applyMarker(markerBases)
            }
            hasToReload = false
        }
    }
}