package com.dudoji.android.mypage.mapper

import com.dudoji.android.mypage.domain.Landmark
import com.dudoji.android.mypage.dto.LandmarkDto
import com.dudoji.android.mypage.type.LandmarkType

fun LandmarkDto.toDomain(): Landmark {
    return Landmark(
        title = this.title,
        currentValue = this.currentValue,
        goalValue = this.goalValue,
        type = when (this.type) {
            "DISTANCE" -> LandmarkType.DISTANCE
            "COUNT" -> LandmarkType.COUNT
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    )
}