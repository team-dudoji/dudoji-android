package com.dudoji.android.network.dto

import com.dudoji.android.map.domain.Pin
import java.time.LocalDateTime

data class PinDto(
    val lat: Double,
    val lng: Double,
    val userId: Long,
    val createdDate: LocalDateTime,
    val title: String,
    val content: String
) {
    fun toDomain(): Pin {
        return Pin(
            lat = lat,
            lng = lng,

            userId = userId,
            createdDate = createdDate,
            title = title,
            content = content
        )
    }
}
