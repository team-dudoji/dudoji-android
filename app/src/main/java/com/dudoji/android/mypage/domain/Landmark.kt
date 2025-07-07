package com.dudoji.android.mypage.domain

import com.dudoji.android.mypage.type.LandmarkType

data class Landmark(
    val title: String,
    val currentValue: Int,
    val goalValue: Int,
    val type: LandmarkType
)
