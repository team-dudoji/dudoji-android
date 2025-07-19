package com.dudoji.android.pin.api.dto

import java.time.LocalDateTime

data class PinRequestDto(
    val lat: Double,
    val lng: Double,
    val createdDate: LocalDateTime,
    val imageUrl: String,
    val content: String,
    val placeName: String,
    val address: String,
    val pinSkinId: Long
)

