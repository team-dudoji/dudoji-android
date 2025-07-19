package com.dudoji.android.pin.domain

data class PinSkin(
    val id: Long,
    val name: String,
    val content: String,
    val imageUrl: String,
    val price: Int,
    val isPurchased: Boolean
) {
}