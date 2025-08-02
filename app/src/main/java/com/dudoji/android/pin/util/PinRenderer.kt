package com.dudoji.android.pin.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.repository.PinSkinRepository
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PinRenderer(
    val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<Pin>
) : DefaultClusterRenderer<Pin>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: Pin, markerOptions: MarkerOptions) {
        val assetManager = context.assets
        val inputStream = assetManager.open("pin/pin_button.png")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 96, 96, true)
        val descriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap)
        markerOptions.icon(descriptor)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClusterItemRendered(clusterItem: Pin, marker: Marker) {
        super.onClusterItemRendered(clusterItem, marker)

        CoroutineScope(Dispatchers.IO).launch {
            // Load the pin skin asynchronously if needed
            val bitmap = PinSkinRepository.getPinSkinBitmapById(clusterItem.pinSkinId, context) ?: return@launch

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 96, 96, true)

            val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap)

            withContext(Dispatchers.Main) {
                Log.d("PinRenderer", "Setting icon for marker: ${clusterItem.pinId}")
                marker.setIcon(bitmapDescriptor)
            }
        }
    }
}
