package com.dudoji.android.pin.api.dto

data class PinSkinDto(
    val skinId: Long,
    val name: String,
    val content: String,
    val imageUrl: String,
    val price: Int,
    val isPurchased: Boolean
) {
}