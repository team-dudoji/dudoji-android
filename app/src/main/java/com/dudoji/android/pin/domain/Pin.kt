package com.dudoji.android.pin.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.pin.api.dto.PinRequestDto
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Pin (
    val lat: Double,
    val lng: Double,
    val pinId: Long,
    val userId: Long,
    var likeCount: Int,
    var isLiked: Boolean,
    val imageUrl: String,
    val createdDate: LocalDate,
    val content: String,
    val master: Who,
    val placeName: String,
    val address: String,
    var pinSkinId: Long,
    val hashtags: List<String>
) : ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(lat, lng)
    }

    override fun getTitle(): String? {
        return "pin"
    }

    override fun getSnippet(): String? {
        return content
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toPinRequestDto(): PinRequestDto {
        return PinRequestDto(
            lat = lat,
            lng = lng,
            createdDate = LocalDateTime.of(createdDate, LocalTime.MIDNIGHT),
            content = content,
            imageUrl = imageUrl,
            placeName = placeName,
            address = address,
            pinSkinId = pinSkinId,
            hashtags = hashtags
        )
    }
}