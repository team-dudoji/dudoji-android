package com.dudoji.android.map.utils.pin

import android.content.ClipData
import android.location.Location
import android.os.Build
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dudoji.android.R
import com.dudoji.android.config.REVEAL_CIRCLE_RADIUS_BY_WALK
import com.dudoji.android.map.domain.Pin
import com.dudoji.android.map.repository.PinRepository
import com.dudoji.android.map.utils.location.LocationService
import com.dudoji.android.util.modal.Modal
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

class PinSetterController{
    val pinSetter: ImageView
    val pinDropZone: FrameLayout
    val googleMap: GoogleMap
    val pinApplier: PinApplier
    val activity: AppCompatActivity

    constructor(pinSetter: ImageView, pinDropZone: FrameLayout, googleMap: GoogleMap, activity: AppCompatActivity, clusterManager: ClusterManager<Pin>) {
        this.pinSetter = pinSetter
        this.pinDropZone = pinDropZone
        this.googleMap = googleMap
        this.pinApplier = PinApplier(clusterManager, activity)
        this.activity = activity

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
                        if (LocationService.isCloseToLastLocation(
                            Location("manual").apply {
                                latitude = lat
                                longitude = lng
                            },
                                REVEAL_CIRCLE_RADIUS_BY_WALK.toFloat()
                        )) {
                            getPinMemoData {
                                val pin =
                                    Pin(
                                        lat,
                                        lng,
                                        0L,
                                        0L,
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        LocalDateTime.now(ZoneId.systemDefault())
                                        } else {
                                            LocalDateTime.now(ZoneId.of("UTC"))
                                        },
                                        it.first,
                                        it.second
                                    )

                                activity.lifecycleScope.launch {
                                    PinRepository.addPin(pin)
                                    pinApplier.applyPin(pin)
                                }

                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "핀을 드롭할 수 있는 위치가 아닙니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        true
                    }
                    else -> false
                }
            }
        )
    }

    fun getPinMemoData(onComplete: (Pair<String, String>) -> Unit) {
        Modal.showCustomModal(activity, R.layout.modal_pin_memo) { view ->
            val pinTitle = view.findViewById<EditText>(R.id.memo_title_input)
            val pinContent = view.findViewById<EditText>(R.id.memo_content_input)
            val saveButton = view.findViewById<Button>(R.id.memo_save_button)

            saveButton.setOnClickListener {
                onComplete(
                    Pair(
                        pinTitle.text.toString(),
                        pinContent.text.toString()
                    )
                )

                // Close the modal
                (view.parent.parent.parent as? ViewGroup)?.removeView(view.parent.parent as View?)
                true
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