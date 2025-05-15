package com.dudoji.android.network.dto

import java.time.LocalDateTime

data class PinDto(val lat: Double, val lng: Double, val userId: Long, val createdDate: LocalDateTime, val title: String, val content: String)