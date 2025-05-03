package com.dudoji.android.map.utils.pin

import android.content.ClipData
import android.view.DragEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.dudoji.android.map.domain.Pin
import com.dudoji.android.map.repository.PinRepository
import com.google.android.gms.maps.GoogleMap
import java.util.Date

class PinSetterController{
    val pinSetter: ImageView
    val pinDropZone: FrameLayout
    val googleMap: GoogleMap
    val pinApplier: PinApplier

    constructor(pinSetter: ImageView, pinDropZone: FrameLayout, googleMap: GoogleMap) {
        this.pinSetter = pinSetter
        this.pinDropZone = pinDropZone
        this.googleMap = googleMap
        this.pinApplier = PinApplier(googleMap)

        setDragAndDropListener()
    }

    fun setDragAndDropListener() {
        pinSetter.setOnLongClickListener {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(pinSetter)
            pinSetter.startDragAndDrop(data, shadowBuilder, pinSetter, 0)
        }

        pinDropZone.setOnDragListener(
            View.OnDragListener { v, event ->
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        true
                    }
                    DragEvent.ACTION_DROP -> {
                        val x = event.x
                        val y = event.y
                        val (lat, lng) = getPinSetterPosition(x, y)
                        val pin = Pin(
                            lat = lat,
                            lng = lng,
                            pinId = 0L,
                            userId = 0L,
                            createdDate = Date(),
                            title = "New Pin",
                            content = "Pin Content"
                        )

                        PinRepository.addPin(pin)
                        pinApplier.applyPin(pin)
                        true
                    }
                    else -> false
                }
            }
        )
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