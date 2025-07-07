package com.dudoji.android.network.api.service

import com.dudoji.android.mypage.dto.DailyQuestDto
import retrofit2.Response
import retrofit2.http.GET

interface DailyQuestApiService {
    @GET("/api/quests/daily")
    suspend fun getDailyQuests(): Response<List<DailyQuestDto>>
}
