package com.dudoji.android.mypage.dto

import com.dudoji.android.mypage.domain.Quest
import com.dudoji.android.mypage.type.MissionUnit
import com.dudoji.android.mypage.type.QuestType

data class QuestDto (
    val title: String,
    val currentValue: Int,
    val goalValue: Int,
    val unit: String,
    val type: String
) {
    fun toDomain(questTypeString: String): Quest {
        val isCompleted = this.currentValue >= this.goalValue

        val missonUnit = when (this.unit) {
            "km" -> MissionUnit.DISTANCE
            "개수" -> MissionUnit.COUNT
            else -> throw IllegalArgumentException("단위 오류: ${this.unit}")
        }

        return Quest(
            title = this.title,
            currentValue = this.currentValue,
            goalValue = this.goalValue,
            unit = missonUnit,
            questType = QuestType.valueOf(questTypeString.uppercase()),
            isCompleted = isCompleted
        )
    }
}