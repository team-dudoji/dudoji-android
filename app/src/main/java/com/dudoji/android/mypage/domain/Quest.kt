package com.dudoji.android.mypage.domain

import com.dudoji.android.mypage.type.MissionUnit
import com.dudoji.android.mypage.type.QuestType

data class Quest(
    val title: String,
    val currentValue: Int,
    val goalValue: Int,
    val unit: MissionUnit,
    val questType: QuestType,
    val isCompleted: Boolean
)

