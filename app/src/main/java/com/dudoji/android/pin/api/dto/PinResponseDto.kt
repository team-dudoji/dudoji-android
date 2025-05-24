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
    val imageUrl: String,
    val isLiked: Boolean,
    val createdDate: LocalDateTime,
    val content: String,
    val master: Who
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(): Pin {
        return Pin(
            lat = lat,
            lng = lng,
            pinId = pinId,
            userId = userId,
            likeCount = likeCount,
            isLiked = isLiked,
            createdDate = createdDate.toLocalDate(),
            imageUrl = imageUrl,
            content = content,
            master = master)
        }
    }
