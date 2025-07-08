package com.dudoji.android.mypage.dto

import com.dudoji.android.mypage.domain.Achievement
import com.dudoji.android.mypage.type.MissionUnit

data class AchievementDto (
    val title: String,
    val value: Int,
    val unit: MissionUnit
) {

    fun toDomain(): Achievement {
        return Achievement(
            title = this.title,
            value = this.value,
            unit = this.unit,
        )
    }
}


