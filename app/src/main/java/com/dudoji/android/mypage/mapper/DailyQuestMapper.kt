package com.dudoji.android.mypage.mapper

import com.dudoji.android.mypage.domain.DailyQuest
import com.dudoji.android.mypage.dto.DailyQuestDto
import com.dudoji.android.mypage.type.DailyQuestType

fun DailyQuestDto.toDomain(): DailyQuest {
    return DailyQuest(
        title = this.title,
        currentValue = this.currentValue,
        goalValue = this.goalValue,
        type = when (this.type) {
            "DISTANCE" -> DailyQuestType.DISTANCE
            "COUNT" -> DailyQuestType.COUNT
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    )
}