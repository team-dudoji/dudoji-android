package com.dudoji.android.pin.util

import android.content.Context
import com.dudoji.android.R
import com.dudoji.android.pin.domain.Pin
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class PinRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<Pin>
) : DefaultClusterRenderer<Pin>(context, map, clusterManager) {

    //지도에 렌더링 되기 전에 호출
    override fun onBeforeClusterItemRendered(item: Pin, markerOptions: MarkerOptions) {
        val iconResId = when (item.pinSkin) {
            "pin_red" -> R.drawable.pin_red
            "pin_orange" -> R.drawable.pin_orange
            "pin_blue" -> R.drawable.pin_blue
            else -> R.drawable.pin_orange
        }
        val bitmapDescriptor = BitmapDescriptorFactory.fromResource(iconResId)
        //마커 아이콘에 적용
        markerOptions.icon(bitmapDescriptor)
    }
}
