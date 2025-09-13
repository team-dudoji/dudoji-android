package com.dudoji.android.landmark.domain

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.dudoji.android.map.domain.NonClusterMarker
import kotlinx.parcelize.Parcelize

@Parcelize
@RequiresApi(Build.VERSION_CODES.O)
data class Landmark(
    val landmarkId: Long,
    override val lat: Double,
    override val lng: Double,
    val placeName: String,
    val address: String,
    val content: String,
    val mapImageUrl: String,
    val detailImageUrl: String,
    val radius: Double,
    var isDetected: Boolean,
    val hashtags: List<String>,
    val isFestival: Boolean
) : NonClusterMarker(lat, lng, mapImageUrl), Parcelable