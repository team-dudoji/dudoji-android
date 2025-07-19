package com.dudoji.android.pin.util

import android.net.Uri
import java.time.LocalDate

data class PinMakeData(
    val placeName: String,
    val content: String,
    val date: LocalDate,
    val imageUri: Uri,
    val address: String,
    val pinSkinId: Long
)