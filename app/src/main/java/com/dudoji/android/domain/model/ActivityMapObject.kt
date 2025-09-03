package com.dudoji.android.domain.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class ActivityMapObject(
        val latLng: LatLng,
        val bitmap: Bitmap,
        val offsetX: Float = 0f,
        val offsetY: Float = 0f,
        val width: Int? = null,
        val height: Int? = null
    )