package com.dudoji.android.mypage.dto

import com.dudoji.android.mypage.domain.Achievement
import com.dudoji.android.mypage.type.MissionUnit

data class AchievementDto (
    val title: String,
    val value: Int,
    val type: String){

    fun toDomain(): Achievement {
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
}


