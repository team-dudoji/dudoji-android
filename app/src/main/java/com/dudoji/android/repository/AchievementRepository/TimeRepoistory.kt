package com.dudoji.android.repository.AchievementRepository

import com.dudoji.android.mypage.achievement.time.TimeItem


object TimeRepository { // 싱글톤으로 관리
    val timeItems = mutableListOf(
        TimeItem("하루 2시간", "하루\n2시간 운동!"),
        TimeItem("하루 1시간", "하루\n1시간 운동!")
    )

    fun getItems(): List<TimeItem> {
        return timeItems
    }

    fun addItem(item: TimeItem) {
        timeItems.add(item)
    }
}