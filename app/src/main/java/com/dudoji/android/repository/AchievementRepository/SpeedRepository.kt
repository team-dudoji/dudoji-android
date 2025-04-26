package com.dudoji.android.repository.AchievementRepository

import com.dudoji.android.mypage.achievement.speed.SpeedItem

object SpeedRepository { // 싱글톤으로 관리
    val distanceItems = mutableListOf<SpeedItem>(
        SpeedItem("하루 5km", "5km 이동 성공!"),
        SpeedItem("하루 3km", "3km 이동 성공!"),
        SpeedItem("하루 1km", "1km 이동 성공!")
    )

    fun getItems(): List<SpeedItem> {
        return distanceItems
    }

    fun addItem(item: SpeedItem) {
        distanceItems.add(item)
    }
}