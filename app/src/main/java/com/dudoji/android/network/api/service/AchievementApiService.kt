package com.dudoji.android.network.api.service

import com.dudoji.android.mypage.dto.AchievementDto
import retrofit2.Response
import retrofit2.http.GET

interface AchievementApiService {
    @GET("/api/achievements")
    suspend fun getAchievements(): Response<List<AchievementDto>>
}