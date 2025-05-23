package com.dudoji.android.network.dto

import com.dudoji.android.map.domain.pin.Pin
import com.dudoji.android.map.domain.pin.Who
import java.time.LocalDateTime

data class PinDto(
    val lat: Double,
    val lng: Double,
    val userId: Long,
    val createdDate: LocalDateTime,
    val title: String,
    val content: String,
    val master: Who
) {
    fun toDomain(): Pin {

        return Pin(
            lat = lat,
            lng = lng,

            userId = userId,
            createdDate = createdDate,
            title = title,
            content = content,
            master = master
        )
        }
    }
