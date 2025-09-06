package com.dudoji.android.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PinSkin(
    val id: Long,
    val name: String,
    val content: String,
    val imageUrl: String,
    val price: Int,
    val isPurchased: Boolean
) : Parcelable {
}