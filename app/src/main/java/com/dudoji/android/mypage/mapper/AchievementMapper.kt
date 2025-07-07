package com.dudoji.android.mypage.mapper

import com.dudoji.android.mypage.domain.Achievement
import com.dudoji.android.mypage.type.AchievementType
import com.dudoji.android.mypage.dto.AchievementDto

fun AchievementDto.toDomain(): Achievement {
    return Achievement(
        title = this.title,
        totalValue = this.totalValue,
        type = when (this.type) {
            "COUNT" -> AchievementType.COUNT
            "DISTANCE" -> AchievementType.DISTANCE
            "PERCENTAGE" -> AchievementType.PERCENTAGE
            else -> throw IllegalArgumentException("Unknown Achievement type: $type")
        }
    )
}
