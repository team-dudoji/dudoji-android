package com.dudoji.android.mypage.api.service

import com.dudoji.android.mypage.api.dto.AchievementDto
import com.dudoji.android.mypage.api.dto.QuestDto
import retrofit2.Response
import retrofit2.http.GET

interface MissionApiService {
    @GET("/api/user/quests")
    suspend fun getQuests(): Response<List<QuestDto>>

    @GET("/api/user/achievements")
    suspend fun getAchievements(): Response<List<AchievementDto>>
}