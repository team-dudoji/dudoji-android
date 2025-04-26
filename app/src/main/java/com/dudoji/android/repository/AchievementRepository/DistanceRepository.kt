package com.dudoji.android.repository.AchievementRepository

import com.dudoji.android.mypage.achievement.distance.DistanceItem

object DistanceRepository { // 싱글톤으로 관리
     val distanceItems = mutableListOf<DistanceItem>(
        DistanceItem("하루 5km", "5km 이동 성공!"),
        DistanceItem("하루 3km", "3km 이동 성공!"),
        DistanceItem("하루 1km", "1km 이동 성공!")
    )

    fun getItems(): List<DistanceItem> {
        return distanceItems
    }

    fun addItem(item: DistanceItem) {
        distanceItems.add(item)
    }
}