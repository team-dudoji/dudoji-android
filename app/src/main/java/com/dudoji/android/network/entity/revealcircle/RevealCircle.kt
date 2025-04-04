package com.dudoji.android.network.entity.revealcircle

data class RevealCircle(
    val lat: Double,
    val lng: Double,
    val radius: Double,
)

data class RevealCircleRequest(
    val revealCircles: List<RevealCircle>,
)