package com.dudoji.android.pin.api.dto

import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.domain.Who
import java.time.LocalDateTime

data class PinResponseDto (
    val lat: Double,
    val lng: Double,
    val userId: Long,
    val pinId: Long,
    val likeCount: Int,
    val imageUrl: String?,
    val liked: Boolean,
    val createdDate: LocalDateTime,
    val content: String,
    val master: Who,
    val placeName: String,
    val address: String,
    val pinSkinId: Long,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(): Pin {
        return Pin(
            lat = lat,
            lng = lng,
            pinId = pinId,
            userId = userId,
            likeCount = likeCount,
            isLiked = liked,
            createdDate = createdDate.toLocalDate(),
            imageUrl = imageUrl?:"",
            content = content,
            master = master,
            placeName = placeName,
            address = address,
            pinSkinId = pinSkinId,
            hashTags = emptyList())
        }
    }
