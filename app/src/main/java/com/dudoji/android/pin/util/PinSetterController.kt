package com.dudoji.android.pin.util

import android.content.ClipData
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dudoji.android.config.REVEAL_CIRCLE_RADIUS_BY_WALK
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.map.utils.location.LocationService
import com.dudoji.android.pin.api.dto.PinRequestDto
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.repository.PinRepository
import com.dudoji.android.pin.util.PinModal.openPinDataModal
import com.dudoji.android.util.UriConverter
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PinSetterController{
    val pinSetter: ImageView
    val pinDropZone: FrameLayout
    val googleMap: GoogleMap
    val pinApplier: PinApplier
    val activity: AppCompatActivity

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(pinSetter: ImageView, pinDropZone: FrameLayout, pinApplier: PinApplier, googleMap: GoogleMap, activity: AppCompatActivity, clusterManager: ClusterManager<Pin>) {
        this.pinSetter = pinSetter
        this.pinDropZone = pinDropZone
        this.googleMap = googleMap
        this.pinApplier = pinApplier
        this.activity = activity

        setDragAndDropListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                            openPinDataModal(activity as MapActivity, lat, lng) {
                                val image = UriConverter.uriToMultipartBodyPart(
                                    activity,
                                    it.imageUri
                                )

                                activity.lifecycleScope.launch {
                                    val imageResponse = RetrofitClient.pinApiService.uploadImage(image)
                                    if (!imageResponse.isSuccessful) {
                                        Log.e("PinRepository", "Failed to upload image: ${imageResponse.errorBody()?.string()}")
                                        Toast.makeText(
                                            activity,
                                            "핀 추가에 실패했습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@launch
                                    }
                                    Log.d("PinRepository", "Image uploaded successfully: ${imageResponse.body()}")
                                    val requestDto =
                                        PinRequestDto(
                                            content = it.content,
                                            createdDate = LocalDateTime.of(it.date, LocalDateTime.now().toLocalTime()),
                                            imageUrl = imageResponse.body()!!,
                                            lat = lat,
                                            lng = lng,
                                            address = it.address,
                                            placeName = it.placeName,
                                            pinSkinId = it.pinSkinId,
                                            hashtags = it.hashtags
                                        )
                                    if (PinRepository.addPin(
                                            requestDto)) {
                                        pinApplier.markForReload()
                                        Toast.makeText(
                                            activity,
                                            "핀 추가에 성공했습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else {
                                        Toast.makeText(
                                            activity,
                                            "핀 추가에 실패했습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
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