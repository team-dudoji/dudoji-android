package com.dudoji.android.presentation.map

import android.content.ClipData
import android.os.Build
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.GoogleMap

@RequiresApi(Build.VERSION_CODES.O)
class PinSetterController(
    val pinSetter: ImageView,
    val pinDropZone: ConstraintLayout,
    val googleMap: GoogleMap,
    val onPinDrop: (lat: Double, lng: Double) -> Unit
) {

    init {
        setDragAndDropListener()
    }

    fun setDragAndDropListener() {
        pinSetter.setOnLongClickListener {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(pinSetter)
            pinSetter.startDragAndDrop(data, shadowBuilder, pinSetter, 0)
        }

        pinDropZone.setOnDragListener{ v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true
                DragEvent.ACTION_DROP -> {
                    val x = event.x
                    val y = event.y
                    val (lat, lng) = getPinSetterPosition(x, y)
                    onPinDrop(lat, lng)
                    true
                }
                else -> false
            }
        }

    }

    fun getPinSetterPosition(x: Float, y: Float): Pair<Double, Double> {
        val visibleRegion = googleMap.projection.visibleRegion
        val latLngBounds = visibleRegion.latLngBounds
        val southwest = latLngBounds.southwest
        val northeast = latLngBounds.northeast
        val latRange = northeast.latitude - southwest.latitude
        val lngRange = northeast.longitude - southwest.longitude
        val lat = northeast.latitude - (y / pinDropZone.height) * latRange
        val lng = southwest.longitude + (x / pinDropZone.width) * lngRange
        return Pair(lat, lng)
    }
}