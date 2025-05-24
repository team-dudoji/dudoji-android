package com.dudoji.android.pin.api.dto

import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.domain.Who
import java.time.LocalDate

data class PinResponseDto (
    val lat: Double,
    val lng: Double,
    val userId: Long,
    val pinId: Long,
    val likeCount: Int,
    val imageUrl: String,
    val isLiked: Boolean,
    val createdDate: LocalDate,
    val content: String,
    val master: Who
) {
    fun toDomain(): Pin {
        return Pin(
            lat = lat,
            lng = lng,
            pinId = pinId,
            userId = userId,
            likeCount = likeCount,
            isLiked = isLiked,
            createdDate = createdDate,
            imageUrl = imageUrl,
            content = content,
            master = master)
        }
    }
