package com.dudoji.android.mypage.mapper

import com.dudoji.android.mypage.domain.Achievement
import com.dudoji.android.mypage.type.MissionUnit
import com.dudoji.android.mypage.dto.AchievementDto

fun AchievementDto.toDomain(): Achievement {
    return Achievement(
        title = this.title,
        value = this.value,
        type = when (this.type) {
            "COUNT" -> MissionUnit.COUNT
            "DISTANCE" -> MissionUnit.DISTANCE
            "PERCENTAGE" -> MissionUnit.PERCENTAGE
            else -> throw IllegalArgumentException("Unknown Achievement type: $type")
        }
    )
}
