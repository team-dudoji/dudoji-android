package com.dudoji.android.mypage.domain

data class Quest(
    val title: String,
    val currentValue: Int,
    val goalValue: Int,
    val unit: MissionUnit,
    val questType: QuestType,
    val isCompleted: Boolean
)

