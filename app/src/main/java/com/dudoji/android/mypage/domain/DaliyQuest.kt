package com.dudoji.android.mypage.domain

import com.dudoji.android.mypage.type.DailyQuestType

data class DailyQuest(
    val title: String,
    val currentValue: Int,
    val goalValue: Int,
    val type: DailyQuestType
)
