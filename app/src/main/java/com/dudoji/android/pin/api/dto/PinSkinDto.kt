package com.dudoji.android.pin.api.dto

import com.dudoji.android.domain.model.PinSkin

data class PinSkinDto(
    val skinId: Long,
    val name: String,
    val content: String,
    val imageUrl: String,
    val price: Int,
    val isPurchased: Boolean
) {
    fun toDomain() = PinSkin(
        id = skinId,
        name = name,
        content = content,
        imageUrl = imageUrl,
        price = price,
        isPurchased = isPurchased
    )
}