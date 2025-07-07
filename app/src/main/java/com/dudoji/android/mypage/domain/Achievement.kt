package com.dudoji.android.mypage.domain

import com.dudoji.android.mypage.type.AchievementType

data class Achievement(
    val title: String,
    val totalValue: Int,
    val type: AchievementType
)