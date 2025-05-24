package com.dudoji.android.pin.api.dto

import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.domain.Who
import java.time.LocalDateTime

data class PinDto(
    val lat: Double,
    val lng: Double,
    val userId: Long,
    val pinId: Long,
    val createdDate: LocalDateTime,
    val title: String,
    val content: String,
    val master: Who
) {
    fun toDomain(): Pin {
        return Pin(
            lat = lat,
            lng = lng,
            pinId = pinId,
            userId = userId,
            createdDate = createdDate,
            title = title,
            content = content,
            master = master)
        }
    }
