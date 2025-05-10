package com.dudoji.android.map.domain

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.util.Date

data class Pin (
    val lat: Double,
    val lng: Double,
    val pinId: Long,
    val userId: Long,
    val createdDate: Date,
    @get:JvmName("getPinTitle")
    val title: String,
    val content: String
) : ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(lat, lng)
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        return content
    }
}