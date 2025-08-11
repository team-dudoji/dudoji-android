package com.dudoji.android.mypage.api.service

import com.dudoji.android.mypage.api.dto.NpcDto
import com.dudoji.android.mypage.api.dto.NpcQuestDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NpcQuestApiService {
    @GET("/api/user/npcs")
    suspend fun getNpcs(@Query("radius") radius: Int,
                        @Query("lat") lat: Double,
                        @Query("lng") lng: Double): Response<List<NpcDto>>

    @GET("/api/user/npcs/{npcId}/quests")
    suspend fun getNpcQuest(@Path("npcId") npcId: Long): Response<NpcQuestDto>
}