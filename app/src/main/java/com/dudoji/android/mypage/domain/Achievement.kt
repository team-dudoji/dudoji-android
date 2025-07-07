package com.dudoji.android.mypage.domain

import com.dudoji.android.mypage.type.MissionUnit

data class Achievement(
    val title: String,
    val value: Int,
    val type: MissionUnit
)