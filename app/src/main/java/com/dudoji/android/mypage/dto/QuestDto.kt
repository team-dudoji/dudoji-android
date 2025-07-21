package com.dudoji.android.mypage.dto

import com.dudoji.android.mypage.domain.Quest
import com.dudoji.android.mypage.domain.MissionUnit
import com.dudoji.android.mypage.domain.QuestType

data class QuestDto (
    val title: String,
    val currentValue: Int,
    val goalValue: Int,
    val unit: MissionUnit,
    val type: QuestType
) {
    fun toDomain(): Quest {
        val isCompleted = this.currentValue >= this.goalValue

        return Quest(
            title = this.title,
            currentValue = this.currentValue,
            goalValue = this.goalValue,
            unit = unit,
            questType = type,
            isCompleted = isCompleted
        )
    }
}