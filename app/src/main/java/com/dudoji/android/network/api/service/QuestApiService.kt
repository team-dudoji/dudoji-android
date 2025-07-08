package com.dudoji.android.network.api.service

import com.dudoji.android.mypage.dto.QuestDto
import retrofit2.Response
import retrofit2.http.GET

interface QuestApiService {
    @GET("/api/user/quests/daily")
    suspend fun getDailyQuests(): Response<List<QuestDto>>

    @GET("/api/user/quests/landmark")
    suspend fun getLandmarkQuests(): Response<List<QuestDto>>
}