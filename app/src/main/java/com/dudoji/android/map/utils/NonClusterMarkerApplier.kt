package com.dudoji.android.map.utils

import RetrofitClient
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.dudoji.android.map.domain.NonClusterMarker
import com.dudoji.android.network.Coil
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.collections.MarkerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
abstract class NonClusterMarkerApplier<T : NonClusterMarker> (
    val normalMarkerCollection: MarkerManager.Collection,
    val context: Context
) {

    protected var isIncludedBaseUrl = false

    companion object {
        const val TAG = "NonClusterMarkerApplier"
    }

    fun applyMarker(markerBases: List<T>) {
        markerBases.forEach { markerBase ->
            val marker = normalMarkerCollection.addMarker(markerBase.toMarkerOptions())
            marker?.tag = markerBase
            applyMarkerIcon(marker, markerBase)
            onMarkerLoaded(marker)
        }
    }

    open fun onMarkerLoaded(marker: Marker?) {}

    fun applyMarkerIcon(marker: Marker?, markerBase: T) {
        if (marker == null) return

        CoroutineScope(Dispatchers.IO).launch {
            val loader = Coil.imageLoader
            val request = ImageRequest.Builder(context)
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

    fun clear() {
        normalMarkerCollection.clear()
    }

    fun add(markers: List<T>) {

    }
}