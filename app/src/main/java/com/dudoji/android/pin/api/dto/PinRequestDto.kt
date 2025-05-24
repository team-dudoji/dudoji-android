package com.dudoji.android.pin.api.dto

import java.time.LocalDate

data class PinRequestDto(
    val lat: Double,
    val lng: Double,
    val createdDate: LocalDate,
    val imageUrl: String,
    val content: String,
)

