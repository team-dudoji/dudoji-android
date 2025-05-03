package com.dudoji.android.map.domain

import java.util.Date

data class Pin (
    val lat: Double,
    val lng: Double,
    val pinId: Long,
    val userId: Long,
    val createdDate: Date,
    val title: String,
    val content: String
)